/*
 * Copyright (c) 2020 Fondesa
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
import com.fondesa.kpermissions.request.PermissionRequest
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

/**
 * Tests for FlowExtensions.kt file.
 */
@ExperimentalCoroutinesApi
class FlowExtensionsKtTest {
    private val request = mock<PermissionRequest>()
    private val listenerCaptor = argumentCaptor<PermissionRequest.Listener>()

    @Test
    fun `When request listener is notified, the collector is notified too`() = runBlockingTest {
        val values = mutableListOf<List<PermissionStatus>>()
        val job = launch {
            request.flow().collect { values += it }
        }

        verify(request).addListener(listenerCaptor.capture())

        val listener = listenerCaptor.lastValue
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
            values,
            listOf(
                listOf(
                    PermissionStatus.Granted(Manifest.permission.SEND_SMS),
                    PermissionStatus.Denied.Permanently(Manifest.permission.CALL_PHONE)
                ),
                listOf(
                    PermissionStatus.Granted(Manifest.permission.SEND_SMS),
                    PermissionStatus.Granted(Manifest.permission.CALL_PHONE)
                )
            )
        )

        job.cancel()
    }

    @Test
    fun `When the collector is launched and canceled, the request listener is added and removed`() = runBlockingTest {
        val flow = request.flow()

        verify(request, never()).addListener(any())

        val job1 = launch { flow.collect() }

        verify(request).addListener(listenerCaptor.capture())

        val job2 = launch { flow.collect() }

        verify(request, times(2)).addListener(listenerCaptor.capture())
        assertNotEquals(listenerCaptor.firstValue, listenerCaptor.lastValue)

        job1.cancel()

        verify(request).removeListener(listenerCaptor.firstValue)

        job2.cancel()

        verify(request).removeListener(listenerCaptor.lastValue)
    }
}
