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

package com.fondesa.kpermissions.request.manifest

import android.Manifest
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.fondesa.kpermissions.PermissionStatus
import com.fondesa.kpermissions.testing.context
import com.fondesa.kpermissions.testing.denyPermissions
import com.fondesa.kpermissions.testing.fakes.FakePermissionRequestListener
import com.fondesa.kpermissions.testing.grantPermissions
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

/**
 * Tests for [ManifestPermissionRequest].
 */
@RunWith(AndroidJUnit4::class)
@Config(maxSdk = 22)
class ManifestPermissionRequestTest {
    @Test
    fun requestWithoutListeners() {
        val permission = Manifest.permission.ACCESS_FINE_LOCATION
        // The request will be sent without listeners and it mustn't throw an exception.
        val request = requestOf(permission)

        request.send()

        // Grant the permission.
        context.grantPermissions(permission)
        request.send()

        // Revoke the permission.
        context.denyPermissions(permission)
        request.send()
    }

    @Test
    fun `When request is sent, listeners are notified with the right permission status`() {
        val firstListener = FakePermissionRequestListener()
        val secondListener = FakePermissionRequestListener()
        val request = requestOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.SEND_SMS
        )
        request.addListener(firstListener)
        request.addListener(secondListener)
        context.grantPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
        context.denyPermissions(Manifest.permission.SEND_SMS)

        request.send()

        val expectedResult = listOf(
            PermissionStatus.Granted(Manifest.permission.ACCESS_FINE_LOCATION),
            PermissionStatus.Denied.Permanently(Manifest.permission.SEND_SMS)
        )
        assertEquals(listOf(expectedResult), firstListener.receivedPermissionsResults)
        assertEquals(listOf(expectedResult), secondListener.receivedPermissionsResults)
    }

    @Test
    fun `When request is sent with empty array, listeners are with empty result`() {
        val listener = FakePermissionRequestListener()
        val request = requestOf()
        request.addListener(listener)

        request.send()

        assertEquals(listOf(emptyList<PermissionStatus>()), listener.receivedPermissionsResults)
    }

    @Test
    fun `When one listener is detached and request is sent, the detached listener is not notified anymore`() {
        val firstListener = FakePermissionRequestListener()
        val secondListener = FakePermissionRequestListener()
        val request = requestOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.SEND_SMS
        )
        request.addListener(firstListener)
        request.addListener(secondListener)
        context.grantPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
        context.denyPermissions(Manifest.permission.SEND_SMS)

        request.send()

        val expectedResult = listOf(
            PermissionStatus.Granted(Manifest.permission.ACCESS_FINE_LOCATION),
            PermissionStatus.Denied.Permanently(Manifest.permission.SEND_SMS)
        )
        assertEquals(listOf(expectedResult), firstListener.receivedPermissionsResults)
        assertEquals(listOf(expectedResult), secondListener.receivedPermissionsResults)

        request.removeListener(firstListener)
        request.send()

        assertEquals(listOf(expectedResult), firstListener.receivedPermissionsResults)
        assertEquals(listOf(expectedResult, expectedResult), secondListener.receivedPermissionsResults)
    }

    @Test
    fun `When all listeners are detached and request is sent, the listeners are not notified anymore`() {
        val firstListener = FakePermissionRequestListener()
        val secondListener = FakePermissionRequestListener()
        val request = requestOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.SEND_SMS
        )
        request.addListener(firstListener)
        request.addListener(secondListener)
        context.grantPermissions(Manifest.permission.ACCESS_FINE_LOCATION)
        context.denyPermissions(Manifest.permission.SEND_SMS)

        request.send()

        val expectedResult = listOf(
            PermissionStatus.Granted(Manifest.permission.ACCESS_FINE_LOCATION),
            PermissionStatus.Denied.Permanently(Manifest.permission.SEND_SMS)
        )
        assertEquals(listOf(expectedResult), firstListener.receivedPermissionsResults)
        assertEquals(listOf(expectedResult), secondListener.receivedPermissionsResults)

        request.removeAllListeners()
        request.send()

        assertEquals(listOf(expectedResult), firstListener.receivedPermissionsResults)
        assertEquals(listOf(expectedResult), secondListener.receivedPermissionsResults)
    }

    private fun requestOf(vararg permissions: String): ManifestPermissionRequest = ManifestPermissionRequest(context, permissions)
}
