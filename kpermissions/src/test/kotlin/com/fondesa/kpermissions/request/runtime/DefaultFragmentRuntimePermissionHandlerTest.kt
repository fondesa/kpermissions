/*
 * Copyright (c) 2018 Fondesa
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

package com.fondesa.kpermissions.request.runtime

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import com.fondesa.test.context
import com.fondesa.test.createFragment
import com.nhaarman.mockitokotlin2.*
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

/**
 * Tests for [DefaultFragmentRuntimePermissionHandler].
 */
@RunWith(RobolectricTestRunner::class)
@Config(minSdk = Build.VERSION_CODES.M)
class DefaultFragmentRuntimePermissionHandlerTest {

    private val firstPermission = Manifest.permission.ACCESS_FINE_LOCATION
    private val secondPermission = Manifest.permission.SEND_SMS
    private val permissions = arrayOf(firstPermission, secondPermission)

    private val listener = mock<RuntimePermissionHandler.Listener>()
    private val fragment = createFragment<DefaultFragmentRuntimePermissionHandler>()

    @Before
    fun attachListenersToFragment() {
        // Attach the listener for the permissions.
        fragment.attachListener(permissions, listener)
    }

    @Test
    fun fragmentCreationSuccessful() {
        // The Fragment must retain the instance.
        assertTrue(fragment.retainInstance)
        // It mustn't have a layout.
        assertNull(fragment.view)
    }

    @Test
    fun permissionsRequested() {
        val permissions =
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.SEND_SMS)
        val spiedFragment = spy(fragment)

        // Request the permissions.
        spiedFragment.requestRuntimePermissions(permissions)

        // It must send the permission request with a request code.
        verify(spiedFragment).requestPermissions(eq(permissions), any())
    }

    @Test
    fun permissionsHandledByDefault() {
        val spiedFragment = spy(fragment)
        // Handle the permissions.
        spiedFragment.handleRuntimePermissions(permissions)
        // The Fragment must request the runtime permissions at first, because they are denied.
        verify(spiedFragment).requestRuntimePermissions(permissions)
        // The listener mustn't be invoked.
        verifyZeroInteractions(listener)
    }

    @Test
    fun permissionsAcceptedNotifyListener() {
        val shadowApp = shadowOf(context)
        // Grant the permissions.
        shadowApp.grantPermissions(*permissions)
        fragment.handleRuntimePermissions(permissions)

        verify(listener).permissionsAccepted(permissions)
        verifyNoMoreInteractions(listener)

        shadowApp.denyPermissions(firstPermission)
        // The listener mustn't be invoked anymore.
        fragment.handleRuntimePermissions(permissions)
    }

    @Test
    fun permissionsNotifyRationaleListener() {
        val spiedFragment = spy(fragment)

        whenever(listener.permissionsShouldShowRationale(any())).thenReturn(true)
        whenever(spiedFragment.shouldShowRequestPermissionRationale(firstPermission)).thenReturn(
            true
        )
        whenever(spiedFragment.shouldShowRequestPermissionRationale(secondPermission)).thenReturn(
            true
        )

        spiedFragment.handleRuntimePermissions(permissions)
        verify(listener).permissionsShouldShowRationale(permissions)

        whenever(spiedFragment.shouldShowRequestPermissionRationale(firstPermission)).thenReturn(
            false
        )

        spiedFragment.handleRuntimePermissions(permissions)
        verify(listener).permissionsShouldShowRationale(arrayOf(secondPermission))

        whenever(spiedFragment.shouldShowRequestPermissionRationale(secondPermission)).thenReturn(
            false
        )

        spiedFragment.handleRuntimePermissions(permissions)

        verify(spiedFragment).requestRuntimePermissions(permissions)
        verifyNoMoreInteractions(listener)
    }

    @Test
    fun processingPermissions() {
        val spiedFragment = spy(fragment)

        spiedFragment.requestRuntimePermissions(permissions)

        spiedFragment.handleRuntimePermissions(permissions)
        // The second requestRuntimePermissions() mustn't be called.
        verify(spiedFragment).requestRuntimePermissions(permissions)
    }

    @Test
    fun processingPermissionsUnlocked() {
        val spiedFragment = spy(fragment)
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
            reqCode, permissions, grantResults(
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
    fun manageResultWithAcceptedPermissions() {
        val spiedFragment = spy(fragment)
        val reqCodeCaptor = argumentCaptor<Int>()
        spiedFragment.requestRuntimePermissions(permissions)
        // Capture the request code used by the Fragment.
        verify(spiedFragment).requestPermissions(eq(permissions), reqCodeCaptor.capture())

        val reqCode = reqCodeCaptor.lastValue
        // Call the result with the captured code.
        spiedFragment.onRequestPermissionsResult(
            reqCode, permissions, grantResults(
                firstGranted = true,
                secondGranted = true
            )
        )

        verify(listener).permissionsAccepted(permissions)

        spiedFragment.onRequestPermissionsResult(
            reqCode, permissions, grantResults(
                firstGranted = true,
                secondGranted = false
            )
        )

        // The listener mustn't be called with a denied permission.
        verify(listener).permissionsAccepted(permissions)
    }

    @Test
    fun manageResultWithRationalePermissions() {
        val spiedFragment = spy(fragment)
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
        whenever(listener.permissionsShouldShowRationale(any())).thenReturn(true)
        whenever(listener.permissionsPermanentlyDenied(any())).thenReturn(true)

        spiedFragment.onRequestPermissionsResult(
            reqCode, permissions, grantResults(
                firstGranted = false,
                secondGranted = false
            )
        )

        verify(listener).permissionsShouldShowRationale(permissions)
        verify(listener, never()).permissionsPermanentlyDenied(any())

        whenever(spiedFragment.shouldShowRequestPermissionRationale(firstPermission)).thenReturn(
            false
        )

        spiedFragment.onRequestPermissionsResult(
            reqCode, permissions, grantResults(
                firstGranted = false,
                secondGranted = false
            )
        )

        verify(listener).permissionsShouldShowRationale(arrayOf(secondPermission))
        // The listener mustn't be notified about permanently denied permissions if there's at least
        // one rationale to show to the user.
        verify(listener, never()).permissionsPermanentlyDenied(any())

        whenever(spiedFragment.shouldShowRequestPermissionRationale(secondPermission)).thenReturn(
            false
        )

        spiedFragment.onRequestPermissionsResult(
            reqCode, permissions, grantResults(
                firstGranted = false,
                secondGranted = false
            )
        )

        // Now the listener can be notified about the permanently denied permissions because
        // all rationales are solved.
        verify(listener).permissionsPermanentlyDenied(permissions)
    }

    @Test
    fun manageResultWithRationalePermissionsSolvedByUser() {
        val spiedFragment = spy(fragment)
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
        whenever(listener.permissionsShouldShowRationale(any())).thenReturn(true)

        spiedFragment.onRequestPermissionsResult(
            reqCode, permissions, grantResults(
                firstGranted = true,
                secondGranted = false
            )
        )

        verify(listener).permissionsShouldShowRationale(arrayOf(secondPermission))
        verify(listener, never()).permissionsAccepted(any())

        spiedFragment.onRequestPermissionsResult(
            reqCode, permissions, grantResults(
                firstGranted = true,
                secondGranted = true
            )
        )

        verify(listener).permissionsAccepted(permissions)
    }

    @Test
    fun manageResultWithDeniedPermissions() {
        val spiedFragment = spy(fragment)
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
        whenever(listener.permissionsDenied(any())).thenReturn(true)
        whenever(listener.permissionsPermanentlyDenied(any())).thenReturn(true)

        spiedFragment.onRequestPermissionsResult(
            reqCode, permissions, grantResults(
                firstGranted = false,
                secondGranted = false
            )
        )

        verify(listener).permissionsDenied(permissions)
        verify(listener, never()).permissionsPermanentlyDenied(any())

        whenever(spiedFragment.shouldShowRequestPermissionRationale(firstPermission)).thenReturn(
            false
        )

        spiedFragment.onRequestPermissionsResult(
            reqCode, permissions, grantResults(
                firstGranted = false,
                secondGranted = false
            )
        )

        verify(listener).permissionsDenied(arrayOf(secondPermission))
        // The listener mustn't be notified about permanently denied permissions if there's at least
        // one rationale to show to the user.
        verify(listener, never()).permissionsPermanentlyDenied(any())

        whenever(spiedFragment.shouldShowRequestPermissionRationale(secondPermission)).thenReturn(
            false
        )

        spiedFragment.onRequestPermissionsResult(
            reqCode, permissions, grantResults(
                firstGranted = false,
                secondGranted = false
            )
        )

        // Now the listener can be notified about the permanently denied permissions because
        // all rationales are solved.
        verify(listener).permissionsPermanentlyDenied(permissions)
    }

    @Test
    fun manageResultWithDeniedPermissionsSolvedByUser() {
        val spiedFragment = spy(fragment)
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
        whenever(listener.permissionsDenied(any())).thenReturn(true)

        spiedFragment.onRequestPermissionsResult(
            reqCode, permissions, grantResults(
                firstGranted = true,
                secondGranted = false
            )
        )

        verify(listener).permissionsDenied(arrayOf(secondPermission))
        verify(listener, never()).permissionsAccepted(any())

        spiedFragment.onRequestPermissionsResult(
            reqCode, permissions, grantResults(
                firstGranted = true,
                secondGranted = true
            )
        )

        verify(listener).permissionsAccepted(permissions)
    }

    @Test
    fun manageResultWithPermanentlyDeniedPermissionsSolvedByUser() {
        val spiedFragment = spy(fragment)
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
        whenever(listener.permissionsPermanentlyDenied(any())).thenReturn(true)

        spiedFragment.onRequestPermissionsResult(
            reqCode, permissions, grantResults(
                firstGranted = true,
                secondGranted = false
            )
        )

        verify(listener).permissionsPermanentlyDenied(arrayOf(secondPermission))
        verify(listener, never()).permissionsAccepted(any())

        spiedFragment.onRequestPermissionsResult(
            reqCode, permissions, grantResults(
                firstGranted = true,
                secondGranted = true
            )
        )

        verify(listener).permissionsAccepted(permissions)
    }


    private fun grantResults(firstGranted: Boolean, secondGranted: Boolean): IntArray {
        val transform = { granted: Boolean ->
            if (granted) PackageManager.PERMISSION_GRANTED else PackageManager.PERMISSION_DENIED
        }
        return intArrayOf(transform(firstGranted), transform(secondGranted))
    }
}