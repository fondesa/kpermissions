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

package com.fondesa.kpermissions.dsl

import android.Manifest
import com.fondesa.kpermissions.request.PermissionRequest
import com.fondesa.kpermissions.request.runtime.nonce.PermissionNonce
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Tests for [PermissionRequestDSL].
 */
@RunWith(RobolectricTestRunner::class)
class PermissionRequestDSLTest {
    private val request = mock<PermissionRequest>()
    private val dsl = PermissionRequestDSL(request)

    @Test
    fun onAcceptedInvoked() {
        val expectedPermissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        var executedPermissions: Array<out String>? = null
        val acceptedCaptor = argumentCaptor<PermissionRequest.AcceptedListener>()

        // The permissions will be assigned to the variable if the method is notified.
        dsl.onAccepted { executedPermissions = it }
        // Capture the listener.
        verify(request).acceptedListener(acceptedCaptor.capture())
        verifyNoMoreInteractions(request)

        // Invoke the method on the captured listener.
        acceptedCaptor.lastValue.onPermissionsAccepted(expectedPermissions)

        assertEquals(expectedPermissions, executedPermissions)
    }

    @Test
    fun onDeniedInvoked() {
        val expectedPermissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        var executedPermissions: Array<out String>? = null
        val deniedCaptor = argumentCaptor<PermissionRequest.DeniedListener>()

        // The permissions will be assigned to the variable if the method is notified.
        dsl.onDenied { executedPermissions = it }
        // Capture the listener.
        verify(request).deniedListener(deniedCaptor.capture())
        verifyNoMoreInteractions(request)

        // Invoke the method on the captured listener.
        deniedCaptor.lastValue.onPermissionsDenied(expectedPermissions)

        assertEquals(expectedPermissions, executedPermissions)
    }

    @Test
    fun onPermanentlyDeniedInvoked() {
        val expectedPermissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        var executedPermissions: Array<out String>? = null
        val permDeniedCaptor = argumentCaptor<PermissionRequest.PermanentlyDeniedListener>()

        // The permissions will be assigned to the variable if the method is notified.
        dsl.onPermanentlyDenied { executedPermissions = it }
        // Capture the listener.
        verify(request).permanentlyDeniedListener(permDeniedCaptor.capture())
        verifyNoMoreInteractions(request)

        // Invoke the method on the captured listener.
        permDeniedCaptor.lastValue.onPermissionsPermanentlyDenied(expectedPermissions)

        assertEquals(expectedPermissions, executedPermissions)
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
        dsl.onShouldShowRationale { permissions, nonce ->
            executedPermissions = permissions
            executedNonce = nonce
        }
        // Capture the listener.
        verify(request).rationaleListener(rationaleCaptor.capture())
        verifyNoMoreInteractions(request)

        // Invoke the method on the captured listener.
        rationaleCaptor.lastValue.onPermissionsShouldShowRationale(
            expectedPermissions,
            expectedNonce
        )

        assertEquals(expectedPermissions, executedPermissions)
        assertEquals(expectedNonce, executedNonce)
    }
}