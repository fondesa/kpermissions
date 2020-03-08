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

package com.fondesa.kpermissions.extension

import android.Manifest
import com.fondesa.kpermissions.PermissionStatus
import com.fondesa.kpermissions.request.PermissionRequest
import com.fondesa.kpermissions.request.runtime.nonce.PermissionNonce
import com.nhaarman.mockitokotlin2.*
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Tests for PermissionRequestListeners.kt file.
 */
@RunWith(RobolectricTestRunner::class)
class PermissionRequestListenersKtTest {
    private val request = mock<PermissionRequest>()

    @Test
    fun `When addListener() is invoked, a new listener is created and the lambda is invoked when the listener is notified`() {
        val expectedResult =
            listOf(PermissionStatus.Granted(Manifest.permission.ACCESS_FINE_LOCATION))
        var result: List<PermissionStatus>? = null
        val listenerCaptor = argumentCaptor<PermissionRequest.Listener>()

        // The result will be assigned to the variable if the method is notified.
        request.addListener { result = it }
        // Captures the listener.
        verify(request).addListener(listenerCaptor.capture())
        verifyNoMoreInteractions(request)

        // Invokes the method on the captured listener.
        listenerCaptor.lastValue.onPermissionsResult(expectedResult)

        assertEquals(expectedResult, result)
    }

    @Test
    fun onAcceptedInvoked() {
        val expectedPermissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        var executedPermissions: Array<out String>? = null
        val acceptedCaptor = argumentCaptor<PermissionRequest.AcceptedListener>()

        // The permissions will be assigned to the variable if the method is notified.
        request.onAccepted { executedPermissions = it }
        // Captures the listener.
        verify(request).acceptedListener(acceptedCaptor.capture())
        verifyNoMoreInteractions(request)

        // Invokes the method on the captured listener.
        acceptedCaptor.lastValue.onPermissionsAccepted(expectedPermissions)

        assertArrayEquals(expectedPermissions, executedPermissions)
    }

    @Test
    fun onDeniedInvoked() {
        val expectedPermissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        var executedPermissions: Array<out String>? = null
        val deniedCaptor = argumentCaptor<PermissionRequest.DeniedListener>()

        // The permissions will be assigned to the variable if the method is notified.
        request.onDenied { executedPermissions = it }
        // Captures the listener.
        verify(request).deniedListener(deniedCaptor.capture())
        verifyNoMoreInteractions(request)

        // Invokes the method on the captured listener.
        deniedCaptor.lastValue.onPermissionsDenied(expectedPermissions)

        assertArrayEquals(expectedPermissions, executedPermissions)
    }

    @Test
    fun onPermanentlyDeniedInvoked() {
        val expectedPermissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        var executedPermissions: Array<out String>? = null
        val permDeniedCaptor = argumentCaptor<PermissionRequest.PermanentlyDeniedListener>()

        // The permissions will be assigned to the variable if the method is notified.
        request.onPermanentlyDenied { executedPermissions = it }
        // Capture the listener.
        verify(request).permanentlyDeniedListener(permDeniedCaptor.capture())
        verifyNoMoreInteractions(request)

        // Invokes the method on the captured listener.
        permDeniedCaptor.lastValue.onPermissionsPermanentlyDenied(expectedPermissions)

        assertArrayEquals(expectedPermissions, executedPermissions)
    }

    @Test
    fun onShouldShowRationaleInvoked() {
        val expectedPermissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        val expectedNonce = object : PermissionNonce {
            override fun use() = Unit
        }
        var executedPermissions: Array<out String>? = null
        var executedNonce: PermissionNonce? = null
        val rationaleCaptor = argumentCaptor<PermissionRequest.RationaleListener>()

        // The permissions and the nonce will be assigned to the variables if the method is notified.
        request.onShouldShowRationale { permissions, nonce ->
            executedPermissions = permissions
            executedNonce = nonce
        }
        // Captures the listener.
        verify(request).rationaleListener(rationaleCaptor.capture())
        verifyNoMoreInteractions(request)

        // Invokes the method on the captured listener.
        rationaleCaptor.lastValue.onPermissionsShouldShowRationale(
            expectedPermissions,
            expectedNonce
        )

        assertArrayEquals(expectedPermissions, executedPermissions)
        assertEquals(expectedNonce, executedNonce)
    }

    @Test
    fun listenersAddedWithDSL() {
        // Adds the listeners with the DSL.
        request.listeners {
            onAccepted { }
            onDenied { }
            onPermanentlyDenied { }
            onShouldShowRationale { _, _ -> }
        }

        // Verifies that all listeners are added.
        verify(request).acceptedListener(any())
        verify(request).deniedListener(any())
        verify(request).permanentlyDeniedListener(any())
        verify(request).rationaleListener(any())
        verifyNoMoreInteractions(request)
    }
}