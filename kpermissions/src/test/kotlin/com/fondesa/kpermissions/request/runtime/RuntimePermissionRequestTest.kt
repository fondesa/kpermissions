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

package com.fondesa.kpermissions.request.runtime

import android.Manifest
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.fondesa.kpermissions.PermissionStatus
import com.fondesa.kpermissions.request.PermissionRequest
import com.fondesa.test.TestActivity
import com.fondesa.test.denyPermissions
import com.fondesa.test.grantPermissions
import com.fondesa.test.launchTestActivity
import com.fondesa.test.letActivity
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.spy
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoMoreInteractions
import org.mockito.kotlin.whenever
import org.robolectric.annotation.Config

/**
 * Tests for [RuntimePermissionRequest].
 */
@RunWith(AndroidJUnit4::class)
@Config(minSdk = 23)
class RuntimePermissionRequestTest {
    private val handler = mock<RuntimePermissionHandler>()
    private lateinit var scenario: ActivityScenario<TestActivity>
    private lateinit var spiedActivity: TestActivity

    @Before
    fun spyActivity() {
        scenario = launchTestActivity()
        spiedActivity = scenario.letActivity { spy(it) }
    }

    @After
    fun destroyScenario() {
        if (::scenario.isInitialized) {
            scenario.close()
        }
    }

    @Test
    fun onePermissionHandled() {
        val permission = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        val request = RuntimePermissionRequest(spiedActivity, permission, handler)

        verify(handler).attachListener(permission, request)

        request.send()
        verify(handler).handleRuntimePermissions(permission)
    }

    @Test
    fun moreThanOnePermissionsHandled() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.SEND_SMS
        )
        val request = RuntimePermissionRequest(spiedActivity, permissions, handler)

        verify(handler).attachListener(permissions, request)

        request.send()
        verify(handler).handleRuntimePermissions(permissions)
    }

    @Test
    fun `When onPermissionsResult is invoked, the result is sent to the notified listeners`() {
        val firstListener = mock<PermissionRequest.Listener>()
        val secondListener = mock<PermissionRequest.Listener>()
        val permissions =
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.SEND_SMS)
        val request = RuntimePermissionRequest(spiedActivity, permissions, handler).apply {
            addListener(firstListener)
            addListener(secondListener)
        }

        val result = listOf(
            PermissionStatus.Granted(Manifest.permission.ACCESS_FINE_LOCATION),
            PermissionStatus.Denied.Permanently(Manifest.permission.SEND_SMS)
        )
        request.onPermissionsResult(result)

        verify(firstListener).onPermissionsResult(result)
        verify(secondListener).onPermissionsResult(result)
        verifyNoMoreInteractions(firstListener, secondListener)
    }

    @Test
    fun `When one listener is detached and onPermissionsResult is invoked, the detached listener is not notified anymore`() {
        val firstListener = mock<PermissionRequest.Listener>()
        val secondListener = mock<PermissionRequest.Listener>()
        val permissions =
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.SEND_SMS)
        val request = RuntimePermissionRequest(spiedActivity, permissions, handler).apply {
            addListener(firstListener)
            addListener(secondListener)
        }

        val result = listOf(
            PermissionStatus.Granted(Manifest.permission.ACCESS_FINE_LOCATION),
            PermissionStatus.Denied.Permanently(Manifest.permission.SEND_SMS)
        )
        request.onPermissionsResult(result)

        verify(firstListener).onPermissionsResult(result)
        verify(secondListener).onPermissionsResult(result)
        verifyNoMoreInteractions(firstListener, secondListener)

        request.removeListener(firstListener)
        request.onPermissionsResult(result)

        verify(firstListener).onPermissionsResult(result)
        verify(secondListener, times(2)).onPermissionsResult(result)
        verifyNoMoreInteractions(firstListener, secondListener)
    }

    @Test
    fun `When all listeners are detached and onPermissionsResult is invoked, they are not notified anymore`() {
        val firstListener = mock<PermissionRequest.Listener>()
        val secondListener = mock<PermissionRequest.Listener>()
        val permissions =
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.SEND_SMS)
        val request = RuntimePermissionRequest(spiedActivity, permissions, handler).apply {
            addListener(firstListener)
            addListener(secondListener)
        }

        val result = listOf(
            PermissionStatus.Granted(Manifest.permission.ACCESS_FINE_LOCATION),
            PermissionStatus.Denied.Permanently(Manifest.permission.SEND_SMS)
        )
        request.onPermissionsResult(result)

        verify(firstListener).onPermissionsResult(result)
        verify(secondListener).onPermissionsResult(result)
        verifyNoMoreInteractions(firstListener, secondListener)

        request.removeAllListeners()
        request.onPermissionsResult(result)

        verify(firstListener).onPermissionsResult(result)
        verify(secondListener).onPermissionsResult(result)
        verifyNoMoreInteractions(firstListener, secondListener)
    }

    @Test
    fun `When checkStatus() is invoked with an Activity, the runtime permissions status is retrieved`() {
        spiedActivity.grantPermissions(Manifest.permission.SEND_SMS)
        spiedActivity.denyPermissions(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CALL_PHONE
        )
        whenever(spiedActivity.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) doReturn true
        whenever(spiedActivity.shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE)) doReturn false
        val expected = listOf(
            PermissionStatus.Granted(Manifest.permission.SEND_SMS),
            PermissionStatus.Denied.ShouldShowRationale(Manifest.permission.ACCESS_FINE_LOCATION),
            PermissionStatus.RequestRequired(Manifest.permission.CALL_PHONE)
        )
        val request = RuntimePermissionRequest(
            spiedActivity,
            arrayOf(
                Manifest.permission.SEND_SMS,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.CALL_PHONE
            ),
            handler
        )

        val actual = request.checkStatus()
        assertEquals(expected, actual)
    }
}
