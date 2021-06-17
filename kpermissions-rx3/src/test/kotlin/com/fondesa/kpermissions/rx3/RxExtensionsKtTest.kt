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

package com.fondesa.kpermissions.rx3

import android.Manifest
import com.fondesa.kpermissions.PermissionStatus
import com.fondesa.kpermissions.request.PermissionRequest
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify

/**
 * Tests for RxExtensions.kt file.
 */
class RxExtensionsKtTest {
    private val request = mock<PermissionRequest>()
    private val listenerCaptor = argumentCaptor<PermissionRequest.Listener>()

    @Test
    fun `When request listener is notified, the observer is notified too`() {
        val observer = request.observe().test()

        verify(request).addListener(listenerCaptor.capture())

        val listener = listenerCaptor.lastValue
        listener.onPermissionsResult(
            listOf(
                PermissionStatus.Granted(Manifest.permission.SEND_SMS),
                PermissionStatus.Denied.Permanently(Manifest.permission.CALL_PHONE)
            )
        )

        observer.assertValueCount(1)
        observer.assertValues(
            listOf(
                PermissionStatus.Granted(Manifest.permission.SEND_SMS),
                PermissionStatus.Denied.Permanently(Manifest.permission.CALL_PHONE)
            )
        )

        listener.onPermissionsResult(
            listOf(
                PermissionStatus.Granted(Manifest.permission.SEND_SMS),
                PermissionStatus.Granted(Manifest.permission.CALL_PHONE)
            )
        )

        observer.assertValueCount(2)
        observer.assertValues(
            listOf(
                PermissionStatus.Granted(Manifest.permission.SEND_SMS),
                PermissionStatus.Denied.Permanently(Manifest.permission.CALL_PHONE)
            ),
            listOf(
                PermissionStatus.Granted(Manifest.permission.SEND_SMS),
                PermissionStatus.Granted(Manifest.permission.CALL_PHONE)
            )
        )
    }

    @Test
    fun `When observer is subscribed, the request listener is added`() {
        val observable = request.observe()

        verify(request, never()).addListener(any())

        observable.test()

        verify(request).addListener(any())

        observable.test()

        // The method addListener() is not invoked again.
        verify(request).addListener(any())
    }

    @Test
    fun `When observer is disposed, the request listener is removed`() {
        val observable = request.observe()
        val observer1 = observable.test()
        val observer2 = observable.test()

        verify(request).addListener(listenerCaptor.capture())

        observer1.dispose()

        verify(request, never()).removeListener(any())

        observer2.dispose()

        verify(request).removeListener(listenerCaptor.lastValue)
    }
}
