/*
 * Copyright (c) 2020 Giorgio Antonioli
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Suppress("DEPRECATION")

package com.fondesa.kpermissions.request.runtime

import android.Manifest
import android.content.pm.PackageManager
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragment
import androidx.fragment.app.testing.withFragment
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.fondesa.kpermissions.PermissionStatus
import com.fondesa.test.context
import com.fondesa.test.denyPermissions
import com.fondesa.test.grantPermissions
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.spy
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import org.robolectric.annotation.Config

/**
 * Tests for [DefaultFragmentRuntimePermissionHandler].
 */
@RunWith(AndroidJUnit4::class)
@Config(minSdk = 23)
class DefaultFragmentRuntimePermissionHandlerTest {
    private val firstPermission = Manifest.permission.ACCESS_FINE_LOCATION
    private val secondPermission = Manifest.permission.SEND_SMS
    private val permissions = arrayOf(firstPermission, secondPermission)
    private val listener = mock<RuntimePermissionHandler.Listener>()
    private lateinit var scenario: FragmentScenario<DefaultFragmentRuntimePermissionHandler>

    @Before
    fun launchScenario() {
        scenario = launchFragment()
    }

    @Test
    fun fragmentCreationSuccessful() {
        scenario.onFragment { fragment ->
            fragment.attachListener(permissions, listener)
            // The Fragment must retain the instance.
            assertTrue(fragment.retainInstance)
            // It mustn't have a layout.
            assertNull(fragment.view)
        }
    }

    @Test
    fun permissionsRequested() {
        scenario.onFragment { it.attachListener(permissions, listener) }
        val permissions =
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.SEND_SMS)
        val spiedFragment = scenario.withFragment(::spy)

        // Request the permissions.
        spiedFragment.requestRuntimePermissions(permissions)

        // It must send the permission request with a request code.
        verify(spiedFragment).requestPermissions(eq(permissions), any())
    }

    @Test
    fun permissionsHandledByDefault() {
        scenario.onFragment { it.attachListener(permissions, listener) }
        val spiedFragment = scenario.withFragment(::spy)
        // Handle the permissions.
        spiedFragment.handleRuntimePermissions(permissions)
        // The Fragment must request the runtime permissions at first, because they are denied.
        verify(spiedFragment).requestRuntimePermissions(permissions)
        // The listener mustn't be invoked.
        verifyNoInteractions(listener)
    }

    @Test
    fun `When listener is not attached and permissions status should be notified, nothing happens`() {
        scenario.onFragment {
            // It shouldn't throw an exception.
            it.handleRuntimePermissions(permissions)
            // It shouldn't throw an exception.
            it.onRequestPermissionsResult(
                // Since we can't get the real request code, just hardcode it.
                requestCode = 986,
                permissions = permissions,
                grantResults = grantResults(
                    firstGranted = true,
                    secondGranted = true
                )
            )
        }
    }

    @Test
    fun permissionsAcceptedNotifyListener() {
        scenario.onFragment { it.attachListener(permissions, listener) }
        // Grant the permissions.
        context.grantPermissions(*permissions)
        scenario.onFragment { it.handleRuntimePermissions(permissions) }

        verify(listener).onPermissionsResult(permissions.map { PermissionStatus.Granted(it) })
        verifyNoMoreInteractions(listener)

        context.denyPermissions(firstPermission)
        // The listener mustn't be invoked anymore.
        scenario.onFragment { it.handleRuntimePermissions(permissions) }
    }

    @Test
    fun permissionsNotifyRationaleListener() {
        scenario.onFragment { it.attachListener(permissions, listener) }
        val mockActivity = mock<FragmentActivity> {
            on(it.checkPermission(any(), any(), any())) doReturn PackageManager.PERMISSION_DENIED
        }
        val spiedFragment = scenario.withFragment {
            spy(this) {
                on(it.requireActivity()) doReturn mockActivity
            }
        }

        whenever(mockActivity.shouldShowRequestPermissionRationale(firstPermission)).thenReturn(
            true
        )
        whenever(mockActivity.shouldShowRequestPermissionRationale(secondPermission)).thenReturn(
            true
        )

        spiedFragment.handleRuntimePermissions(permissions)
        verify(listener, never()).onPermissionsResult(any())

        whenever(mockActivity.shouldShowRequestPermissionRationale(firstPermission)).thenReturn(
            false
        )

        spiedFragment.handleRuntimePermissions(permissions)
        verify(listener, never()).onPermissionsResult(any())

        whenever(mockActivity.shouldShowRequestPermissionRationale(secondPermission)).thenReturn(
            false
        )

        spiedFragment.handleRuntimePermissions(permissions)

        verify(spiedFragment).requestRuntimePermissions(permissions)
        verifyNoMoreInteractions(listener)
    }

    @Test
    fun processingPermissions() {
        scenario.onFragment { it.attachListener(permissions, listener) }
        val spiedFragment = scenario.withFragment(::spy)

        spiedFragment.requestRuntimePermissions(permissions)

        spiedFragment.handleRuntimePermissions(permissions)
        // The second requestRuntimePermissions() mustn't be called.
        verify(spiedFragment).requestRuntimePermissions(permissions)
    }

    @Test
    fun processingPermissionsUnlocked() {
        scenario.onFragment { it.attachListener(permissions, listener) }
        val spiedFragment = scenario.withFragment(::spy)
        val reqCodeCaptor = argumentCaptor<Int>()

        spiedFragment.requestRuntimePermissions(permissions)

        spiedFragment.handleRuntimePermissions(permissions)
        // The second requestRuntimePermissions() mustn't be called.
        verify(spiedFragment).requestRuntimePermissions(permissions)
        // Capture the request code used by the Fragment.
        verify(spiedFragment).requestPermissions(eq(permissions), reqCodeCaptor.capture())

        val reqCode = reqCodeCaptor.lastValue
        // Call the result with the captured code.
        spiedFragment.onRequestPermissionsResult(
            requestCode = reqCode,
            permissions = permissions,
            grantResults = grantResults(
                firstGranted = true,
                secondGranted = true
            )
        )

        spiedFragment.requestRuntimePermissions(permissions)
        // Now the method requestRuntimePermissions() can be called again because the Fragment
        // isn't processing the permissions anymore.
        verify(spiedFragment, times(2)).requestRuntimePermissions(permissions)
    }

    @Test
    fun `When onRequestPermissionsResult is invoked with a different request code, the listeners aren't notified`() {
        scenario.onFragment { it.attachListener(permissions, listener) }
        val spiedFragment = scenario.withFragment(::spy)
        val reqCodeCaptor = argumentCaptor<Int>()
        spiedFragment.requestRuntimePermissions(permissions)
        // Captures the request code used by the Fragment.
        verify(spiedFragment).requestPermissions(eq(permissions), reqCodeCaptor.capture())

        val reqCode = reqCodeCaptor.lastValue
        // Calls the result with another request code.
        spiedFragment.onRequestPermissionsResult(
            requestCode = reqCode + 1,
            permissions = permissions,
            grantResults = grantResults(
                firstGranted = true,
                secondGranted = true
            )
        )

        verifyNoInteractions(listener)
    }

    @Test
    fun `When onRequestPermissionsResult is invoked without permissions, the listeners aren't notified`() {
        scenario.onFragment { it.attachListener(permissions, listener) }
        val spiedFragment = scenario.withFragment(::spy)
        val reqCodeCaptor = argumentCaptor<Int>()
        spiedFragment.requestRuntimePermissions(permissions)
        // Captures the request code used by the Fragment.
        verify(spiedFragment).requestPermissions(eq(permissions), reqCodeCaptor.capture())

        val reqCode = reqCodeCaptor.lastValue
        // Calls the result with the captured code.
        spiedFragment.onRequestPermissionsResult(reqCode, emptyArray(), intArrayOf())

        verifyNoInteractions(listener)
    }

    @Test
    fun manageResultWithAcceptedPermissions() {
        scenario.onFragment { it.attachListener(permissions, listener) }
        val spiedFragment = scenario.withFragment(::spy)
        val reqCodeCaptor = argumentCaptor<Int>()
        spiedFragment.requestRuntimePermissions(permissions)
        // Capture the request code used by the Fragment.
        verify(spiedFragment).requestPermissions(eq(permissions), reqCodeCaptor.capture())

        val reqCode = reqCodeCaptor.lastValue
        // Call the result with the captured code.
        spiedFragment.onRequestPermissionsResult(
            requestCode = reqCode,
            permissions = permissions,
            grantResults = grantResults(
                firstGranted = true,
                secondGranted = true
            )
        )

        verify(listener).onPermissionsResult(permissions.map { PermissionStatus.Granted(it) })

        spiedFragment.onRequestPermissionsResult(
            requestCode = reqCode,
            permissions = permissions,
            grantResults = grantResults(
                firstGranted = true,
                secondGranted = false
            )
        )

        verify(listener).onPermissionsResult(permissions.map { PermissionStatus.Granted(it) })
    }

    @Test
    fun manageResultWithRationalePermissions() {
        scenario.onFragment { it.attachListener(permissions, listener) }
        val spiedFragment = scenario.withFragment(::spy)
        val reqCodeCaptor = argumentCaptor<Int>()
        spiedFragment.requestRuntimePermissions(permissions)
        // Capture the request code used by the Fragment.
        verify(spiedFragment).requestPermissions(eq(permissions), reqCodeCaptor.capture())

        val reqCode = reqCodeCaptor.lastValue

        whenever(spiedFragment.shouldShowRequestPermissionRationale(firstPermission)).thenReturn(
            true
        )
        whenever(spiedFragment.shouldShowRequestPermissionRationale(secondPermission)).thenReturn(
            true
        )

        spiedFragment.onRequestPermissionsResult(
            requestCode = reqCode,
            permissions = permissions,
            grantResults = grantResults(
                firstGranted = false,
                secondGranted = false
            )
        )

        verify(listener).onPermissionsResult(
            permissions.map { PermissionStatus.Denied.ShouldShowRationale(it) }
        )

        whenever(spiedFragment.shouldShowRequestPermissionRationale(firstPermission))
            .thenReturn(false)

        spiedFragment.onRequestPermissionsResult(
            requestCode = reqCode,
            permissions = permissions,
            grantResults = grantResults(
                firstGranted = false,
                secondGranted = false
            )
        )

        verify(listener).onPermissionsResult(
            listOf(
                PermissionStatus.Denied.Permanently(firstPermission),
                PermissionStatus.Denied.ShouldShowRationale(secondPermission)
            )
        )

        whenever(spiedFragment.shouldShowRequestPermissionRationale(secondPermission)).thenReturn(
            false
        )

        spiedFragment.onRequestPermissionsResult(
            requestCode = reqCode,
            permissions = permissions,
            grantResults = grantResults(
                firstGranted = false,
                secondGranted = false
            )
        )

        verify(listener)
            .onPermissionsResult(permissions.map { PermissionStatus.Denied.Permanently(it) })
    }

    @Test
    fun manageResultWithRationalePermissionsSolvedByUser() {
        scenario.onFragment { it.attachListener(permissions, listener) }
        val spiedFragment = scenario.withFragment(::spy)
        val reqCodeCaptor = argumentCaptor<Int>()
        spiedFragment.requestRuntimePermissions(permissions)
        // Capture the request code used by the Fragment.
        verify(spiedFragment).requestPermissions(eq(permissions), reqCodeCaptor.capture())

        val reqCode = reqCodeCaptor.lastValue

        whenever(spiedFragment.shouldShowRequestPermissionRationale(firstPermission)).thenReturn(
            true
        )
        whenever(spiedFragment.shouldShowRequestPermissionRationale(secondPermission)).thenReturn(
            true
        )

        spiedFragment.onRequestPermissionsResult(
            requestCode = reqCode,
            permissions = permissions,
            grantResults = grantResults(
                firstGranted = true,
                secondGranted = false
            )
        )

        verify(listener).onPermissionsResult(
            listOf(
                PermissionStatus.Granted(firstPermission),
                PermissionStatus.Denied.ShouldShowRationale(secondPermission)
            )
        )

        spiedFragment.onRequestPermissionsResult(
            requestCode = reqCode,
            permissions = permissions,
            grantResults = grantResults(
                firstGranted = true,
                secondGranted = true
            )
        )

        verify(listener).onPermissionsResult(permissions.map { PermissionStatus.Granted(it) })
    }

    @Test
    fun manageResultWithDeniedPermissions() {
        scenario.onFragment { it.attachListener(permissions, listener) }
        val spiedFragment = scenario.withFragment(::spy)
        val reqCodeCaptor = argumentCaptor<Int>()
        spiedFragment.requestRuntimePermissions(permissions)
        // Capture the request code used by the Fragment.
        verify(spiedFragment).requestPermissions(eq(permissions), reqCodeCaptor.capture())

        val reqCode = reqCodeCaptor.lastValue

        whenever(spiedFragment.shouldShowRequestPermissionRationale(firstPermission)).thenReturn(
            true
        )
        whenever(spiedFragment.shouldShowRequestPermissionRationale(secondPermission)).thenReturn(
            true
        )

        spiedFragment.onRequestPermissionsResult(
            requestCode = reqCode,
            permissions = permissions,
            grantResults = grantResults(
                firstGranted = false,
                secondGranted = false
            )
        )

        verify(listener).onPermissionsResult(
            permissions.map { PermissionStatus.Denied.ShouldShowRationale(it) }
        )

        whenever(spiedFragment.shouldShowRequestPermissionRationale(firstPermission))
            .thenReturn(false)

        spiedFragment.onRequestPermissionsResult(
            requestCode = reqCode,
            permissions = permissions,
            grantResults = grantResults(
                firstGranted = false,
                secondGranted = false
            )
        )

        verify(listener).onPermissionsResult(
            listOf(
                PermissionStatus.Denied.Permanently(firstPermission),
                PermissionStatus.Denied.ShouldShowRationale(secondPermission)
            )
        )

        whenever(spiedFragment.shouldShowRequestPermissionRationale(secondPermission)).thenReturn(
            false
        )

        spiedFragment.onRequestPermissionsResult(
            requestCode = reqCode,
            permissions = permissions,
            grantResults = grantResults(
                firstGranted = false,
                secondGranted = false
            )
        )

        verify(listener)
            .onPermissionsResult(permissions.map { PermissionStatus.Denied.Permanently(it) })
    }

    @Test
    fun manageResultWithDeniedPermissionsSolvedByUser() {
        scenario.onFragment { it.attachListener(permissions, listener) }
        val spiedFragment = scenario.withFragment(::spy)
        val reqCodeCaptor = argumentCaptor<Int>()
        spiedFragment.requestRuntimePermissions(permissions)
        // Capture the request code used by the Fragment.
        verify(spiedFragment).requestPermissions(eq(permissions), reqCodeCaptor.capture())

        val reqCode = reqCodeCaptor.lastValue

        whenever(spiedFragment.shouldShowRequestPermissionRationale(firstPermission)).thenReturn(
            true
        )
        whenever(spiedFragment.shouldShowRequestPermissionRationale(secondPermission)).thenReturn(
            true
        )

        spiedFragment.onRequestPermissionsResult(
            requestCode = reqCode,
            permissions = permissions,
            grantResults = grantResults(
                firstGranted = true,
                secondGranted = false
            )
        )

        verify(listener).onPermissionsResult(
            listOf(
                PermissionStatus.Granted(firstPermission),
                PermissionStatus.Denied.ShouldShowRationale(secondPermission)
            )
        )

        spiedFragment.onRequestPermissionsResult(
            requestCode = reqCode,
            permissions = permissions,
            grantResults = grantResults(
                firstGranted = true,
                secondGranted = true
            )
        )

        verify(listener).onPermissionsResult(permissions.map { PermissionStatus.Granted(it) })
    }

    @Test
    fun manageResultWithPermanentlyDeniedPermissionsSolvedByUser() {
        scenario.onFragment { it.attachListener(permissions, listener) }
        val spiedFragment = scenario.withFragment(::spy)
        val reqCodeCaptor = argumentCaptor<Int>()
        spiedFragment.requestRuntimePermissions(permissions)
        // Capture the request code used by the Fragment.
        verify(spiedFragment).requestPermissions(eq(permissions), reqCodeCaptor.capture())

        val reqCode = reqCodeCaptor.lastValue

        whenever(spiedFragment.shouldShowRequestPermissionRationale(firstPermission)).thenReturn(
            false
        )
        whenever(spiedFragment.shouldShowRequestPermissionRationale(secondPermission)).thenReturn(
            false
        )

        spiedFragment.onRequestPermissionsResult(
            requestCode = reqCode,
            permissions = permissions,
            grantResults = grantResults(
                firstGranted = true,
                secondGranted = false
            )
        )

        verify(listener).onPermissionsResult(
            listOf(
                PermissionStatus.Granted(firstPermission),
                PermissionStatus.Denied.Permanently(secondPermission)
            )
        )

        spiedFragment.onRequestPermissionsResult(
            requestCode = reqCode,
            permissions = permissions,
            grantResults = grantResults(
                firstGranted = true,
                secondGranted = true
            )
        )

        verify(listener).onPermissionsResult(permissions.map { PermissionStatus.Granted(it) })
    }

    @Test
    fun `When rationale permissions are not handled and there aren't permanently denied permissions, listeners are not notified`() {
        scenario.onFragment { it.attachListener(permissions, listener) }
        val spiedFragment = scenario.withFragment(::spy)
        val reqCodeCaptor = argumentCaptor<Int>()
        spiedFragment.requestRuntimePermissions(permissions)
        // Capture the request code used by the Fragment.
        verify(spiedFragment).requestPermissions(eq(permissions), reqCodeCaptor.capture())

        val reqCode = reqCodeCaptor.lastValue

        whenever(spiedFragment.shouldShowRequestPermissionRationale(firstPermission))
            .thenReturn(true)
        whenever(spiedFragment.shouldShowRequestPermissionRationale(secondPermission))
            .thenReturn(true)

        spiedFragment.onRequestPermissionsResult(
            requestCode = reqCode,
            permissions = permissions,
            grantResults = grantResults(
                firstGranted = false,
                secondGranted = false
            )
        )

        verify(listener)
            .onPermissionsResult(permissions.map { PermissionStatus.Denied.ShouldShowRationale(it) })
    }

    @Test
    fun `When Fragment is not added yet, the permissions are handled when it will be attached`() {
        scenario.onFragment { it.attachListener(permissions, listener) }
        context.grantPermissions(*permissions)
        val spiedFragment = scenario.withFragment {
            spy(this) {
                on(it.isAdded) doReturn false
            }
        }

        spiedFragment.handleRuntimePermissions(permissions)

        verify(listener, never()).onPermissionsResult(any())

        spiedFragment.onAttach(context)

        verify(listener).onPermissionsResult(permissions.map { PermissionStatus.Granted(it) })
    }

    private fun grantResults(firstGranted: Boolean, secondGranted: Boolean): IntArray {
        val transform = { granted: Boolean ->
            if (granted) PackageManager.PERMISSION_GRANTED else PackageManager.PERMISSION_DENIED
        }
        return intArrayOf(transform(firstGranted), transform(secondGranted))
    }
}
