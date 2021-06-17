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
import com.fondesa.kpermissions.request.PermissionRequest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify

/**
 * Tests for SuspendExtensions.kt file.
 */
@ExperimentalCoroutinesApi
class SuspendExtensionsKtTest {
    private val request = mock<PermissionRequest>()
    private val listenerCaptor = argumentCaptor<PermissionRequest.Listener>()

    @Test
    fun `When sendSuspend is invoked, the result is received when listener is notified`() = runBlockingTest {
        var result: List<PermissionStatus>? = null
        val job = launch { result = request.sendSuspend() }

        verify(request).addListener(listenerCaptor.capture())

        val listener = listenerCaptor.lastValue
        listener.onPermissionsResult(
            listOf(
                PermissionStatus.Granted(Manifest.permission.SEND_SMS),
                PermissionStatus.Denied.Permanently(Manifest.permission.CALL_PHONE)
            )
        )

        assertNotNull(result)
        assertEquals(
            listOf(
                PermissionStatus.Granted(Manifest.permission.SEND_SMS),
                PermissionStatus.Denied.Permanently(Manifest.permission.CALL_PHONE)
            ),
            result
        )

        job.cancel()
    }

    @Test
    fun `When sendSuspend is invoked and the result is received, the listener is removed`() = runBlockingTest {
        val job = launch { request.sendSuspend() }

        verify(request).addListener(listenerCaptor.capture())
        verify(request, never()).removeListener(any())

        val listener = listenerCaptor.lastValue
        listener.onPermissionsResult(
            listOf(
                PermissionStatus.Granted(Manifest.permission.SEND_SMS),
                PermissionStatus.Denied.Permanently(Manifest.permission.CALL_PHONE)
            )
        )

        verify(request).removeListener(listener)

        job.cancel()
    }

    @Test
    fun `When sendSuspend is invoked and the job is canceled, the listener is removed`() = runBlockingTest {
        val job = launch { request.sendSuspend() }

        verify(request).addListener(listenerCaptor.capture())
        verify(request, never()).removeListener(any())

        job.cancel()

        verify(request).removeListener(listenerCaptor.lastValue)
    }
}
