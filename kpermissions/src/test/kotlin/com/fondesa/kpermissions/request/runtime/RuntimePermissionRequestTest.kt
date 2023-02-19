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
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.fondesa.kpermissions.PermissionStatus
import com.fondesa.kpermissions.testing.activity
import com.fondesa.kpermissions.testing.denyPermissions
import com.fondesa.kpermissions.testing.fakes.FakeFragmentActivity
import com.fondesa.kpermissions.testing.fakes.FakePermissionRequestListener
import com.fondesa.kpermissions.testing.fakes.FakeRuntimePermissionHandler
import com.fondesa.kpermissions.testing.grantPermissions
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

/**
 * Tests for [RuntimePermissionRequest].
 */
@RunWith(AndroidJUnit4::class)
@Config(minSdk = 23)
class RuntimePermissionRequestTest {
    @get:Rule
    internal val scenarioRule = ActivityScenarioRule(FakeFragmentActivity::class.java)
    private val handler = FakeRuntimePermissionHandler()
    private val activity get() = scenarioRule.activity

    @Test
    fun onePermissionHandled() {
        val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        val request = RuntimePermissionRequest(activity, permissions, handler)

        request.send()

        assertEquals(listOf(permissions), handler.handledRuntimePermissions)
    }

    @Test
    fun moreThanOnePermissionsHandled() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.SEND_SMS
        )
        val request = RuntimePermissionRequest(activity, permissions, handler)

        request.send()

        assertEquals(listOf(permissions), handler.handledRuntimePermissions)
    }

    @Test
    fun `When onPermissionsResult is invoked, the result is sent to the notified listeners`() {
        val firstListener = FakePermissionRequestListener()
        val secondListener = FakePermissionRequestListener()
        val permissions =
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.SEND_SMS)
        val request = RuntimePermissionRequest(activity, permissions, handler).apply {
            addListener(firstListener)
            addListener(secondListener)
        }

        val result = listOf(
            PermissionStatus.Granted(Manifest.permission.ACCESS_FINE_LOCATION),
            PermissionStatus.Denied.Permanently(Manifest.permission.SEND_SMS)
        )
        request.onPermissionsResult(result)

        assertEquals(listOf(result), firstListener.receivedPermissionsResults)
        assertEquals(listOf(result), secondListener.receivedPermissionsResults)
    }

    @Test
    fun `When one listener is detached and onPermissionsResult is invoked, the detached listener is not notified anymore`() {
        val firstListener = FakePermissionRequestListener()
        val secondListener = FakePermissionRequestListener()
        val permissions =
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.SEND_SMS)
        val request = RuntimePermissionRequest(activity, permissions, handler).apply {
            addListener(firstListener)
            addListener(secondListener)
        }

        val result = listOf(
            PermissionStatus.Granted(Manifest.permission.ACCESS_FINE_LOCATION),
            PermissionStatus.Denied.Permanently(Manifest.permission.SEND_SMS)
        )
        request.onPermissionsResult(result)

        assertEquals(listOf(result), firstListener.receivedPermissionsResults)
        assertEquals(listOf(result), secondListener.receivedPermissionsResults)

        request.removeListener(firstListener)
        request.onPermissionsResult(result)

        assertEquals(listOf(result), firstListener.receivedPermissionsResults)
        assertEquals(listOf(result, result), secondListener.receivedPermissionsResults)
    }

    @Test
    fun `When all listeners are detached and onPermissionsResult is invoked, they are not notified anymore`() {
        val firstListener = FakePermissionRequestListener()
        val secondListener = FakePermissionRequestListener()
        val permissions =
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.SEND_SMS)
        val request = RuntimePermissionRequest(activity, permissions, handler).apply {
            addListener(firstListener)
            addListener(secondListener)
        }

        val result = listOf(
            PermissionStatus.Granted(Manifest.permission.ACCESS_FINE_LOCATION),
            PermissionStatus.Denied.Permanently(Manifest.permission.SEND_SMS)
        )
        request.onPermissionsResult(result)

        assertEquals(listOf(result), firstListener.receivedPermissionsResults)
        assertEquals(listOf(result), secondListener.receivedPermissionsResults)

        request.removeAllListeners()
        request.onPermissionsResult(result)

        assertEquals(listOf(result), firstListener.receivedPermissionsResults)
        assertEquals(listOf(result), secondListener.receivedPermissionsResults)
    }

    @Test
    fun `When checkStatus() is invoked with an Activity, the runtime permissions status is retrieved`() {
        activity.grantPermissions(Manifest.permission.SEND_SMS)
        activity.denyPermissions(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CALL_PHONE
        )
        activity.overrideShouldShowRequestPermissionRationale(
            Manifest.permission.ACCESS_FINE_LOCATION to true,
            Manifest.permission.CALL_PHONE to false
        )
        val expected = listOf(
            PermissionStatus.Granted(Manifest.permission.SEND_SMS),
            PermissionStatus.Denied.ShouldShowRationale(Manifest.permission.ACCESS_FINE_LOCATION),
            PermissionStatus.RequestRequired(Manifest.permission.CALL_PHONE)
        )
        val request = RuntimePermissionRequest(
            activity,
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
