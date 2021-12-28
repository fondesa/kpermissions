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

package com.fondesa.kpermissions.coroutines

import android.Manifest
import com.fondesa.kpermissions.PermissionStatus
import com.fondesa.kpermissions.testing.fakes.FakePermissionRequest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Tests for FlowExtensions.kt file.
 */
@ExperimentalCoroutinesApi
class FlowExtensionsKtTest {
    private val request = FakePermissionRequest()

    @Test
    fun `When request listener is notified, the collector is notified too`() = runBlockingTest {
        val values = mutableListOf<List<PermissionStatus>>()
        val job = launch {
            request.flow().collect { values += it }
        }

        assertEquals(1, request.listeners.size)

        val listener = request.listeners.first()
        listener.onPermissionsResult(
            listOf(
                PermissionStatus.Granted(Manifest.permission.SEND_SMS),
                PermissionStatus.Denied.Permanently(Manifest.permission.CALL_PHONE)
            )
        )

        assertEquals(1, values.size)
        assertEquals(
            values,
            listOf(
                listOf(
                    PermissionStatus.Granted(Manifest.permission.SEND_SMS),
                    PermissionStatus.Denied.Permanently(Manifest.permission.CALL_PHONE)
                )
            )
        )

        listener.onPermissionsResult(
            listOf(
                PermissionStatus.Granted(Manifest.permission.SEND_SMS),
                PermissionStatus.Granted(Manifest.permission.CALL_PHONE)
            )
        )

        assertEquals(2, values.size)
        assertEquals(
            listOf(
                listOf(
                    PermissionStatus.Granted(Manifest.permission.SEND_SMS),
                    PermissionStatus.Denied.Permanently(Manifest.permission.CALL_PHONE)
                ),
                listOf(
                    PermissionStatus.Granted(Manifest.permission.SEND_SMS),
                    PermissionStatus.Granted(Manifest.permission.CALL_PHONE)
                )
            ),
            values
        )

        job.cancel()
    }

    @Test
    fun `When the collector is launched and canceled, the request listener is added and removed`() = runBlockingTest {
        val flow = request.flow()

        assertTrue(request.listeners.isEmpty())

        val job1 = launch { flow.collect() }

        assertEquals(1, request.listeners.size)

        val job2 = launch { flow.collect() }

        assertEquals(2, request.listeners.size)
        val secondListener = request.listeners[1]
        assertNotEquals(request.listeners.first(), secondListener)

        job1.cancel()

        assertEquals(listOf(secondListener), request.listeners)

        job2.cancel()

        assertTrue(request.listeners.isEmpty())
    }
}
