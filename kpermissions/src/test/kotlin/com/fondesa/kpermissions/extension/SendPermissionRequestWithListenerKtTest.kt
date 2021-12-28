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

package com.fondesa.kpermissions.extension

import android.Manifest
import com.fondesa.kpermissions.PermissionStatus
import com.fondesa.kpermissions.testing.fakes.FakePermissionRequest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Tests for SendPermissionRequestWithListener.kt file.
 */
class SendPermissionRequestWithListenerKtTest {
    private val request = FakePermissionRequest()

    @Test
    fun `When send { } is invoked and listener is notified, callback is invoked`() {
        val results = mutableListOf<List<PermissionStatus>>()

        request.send { results += it }

        assertTrue(results.isEmpty())
        assertEquals(1, request.listeners.size)

        val listener = request.listeners.first()
        listener.onPermissionsResult(
            listOf(
                PermissionStatus.Granted(Manifest.permission.SEND_SMS),
                PermissionStatus.Denied.Permanently(Manifest.permission.CALL_PHONE)
            )
        )

        assertEquals(1, results.size)
        assertEquals(
            listOf(
                PermissionStatus.Granted(Manifest.permission.SEND_SMS),
                PermissionStatus.Denied.Permanently(Manifest.permission.CALL_PHONE)
            ),
            results.first()
        )
    }

    @Test
    fun `When send { } is invoked and listener is notified, listener is removed`() {
        request.send {}

        assertEquals(1, request.listeners.size)

        val listener = request.listeners.first()
        listener.onPermissionsResult(
            listOf(
                PermissionStatus.Granted(Manifest.permission.SEND_SMS),
                PermissionStatus.Denied.Permanently(Manifest.permission.CALL_PHONE)
            )
        )

        assertTrue(request.listeners.isEmpty())
    }
}
