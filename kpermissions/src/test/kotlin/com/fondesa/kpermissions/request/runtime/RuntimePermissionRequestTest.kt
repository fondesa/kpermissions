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

package com.fondesa.kpermissions.request.runtime

import android.Manifest
import com.fondesa.kpermissions.request.PermissionRequest
import com.fondesa.kpermissions.request.runtime.nonce.PermissionNonce
import com.fondesa.kpermissions.request.runtime.nonce.PermissionNonceGenerator
import com.nhaarman.mockito_kotlin.*
import junit.framework.Assert.assertFalse
import junit.framework.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Tests for [RuntimePermissionRequest].
 */
@RunWith(RobolectricTestRunner::class)
class RuntimePermissionRequestTest {

    private val handler = mock<RuntimePermissionHandler>()
    private val nonce = mock<PermissionNonce>()
    private val nonceGenerator = mock<PermissionNonceGenerator> {
        on(it.provideNonce(eq(handler), any())).thenReturn(nonce)
    }

    private val acceptedListener = mock<PermissionRequest.AcceptedListener>()
    private val deniedListener = mock<PermissionRequest.DeniedListener>()
    private val permDeniedListener = mock<PermissionRequest.PermanentlyDeniedListener>()
    private val rationaleListener = mock<PermissionRequest.RationaleListener>()

    @Test
    fun onePermissionHandled() {
        val permission = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        val request = requestOf(*permission)

        verify(handler).attachListener(permission, request)

        request.send()
        verify(handler).handleRuntimePermissions(permission)
    }

    @Test
    fun moreThanOnePermissionsHandled() {
        val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.SEND_SMS)
        val request = requestOf(*permissions)

        verify(handler).attachListener(permissions, request)

        request.send()
        verify(handler).handleRuntimePermissions(permissions)
    }

    @Test
    fun acceptedPermissionsHandled() {
        val first = Manifest.permission.ACCESS_FINE_LOCATION
        val second = Manifest.permission.SEND_SMS
        val permissions = arrayOf(first, second)
        val request = requestOf(*permissions)

        val acceptedPermissions = arrayOf(first)
        val handled = request.permissionsAccepted(acceptedPermissions)

        assertTrue(handled)
        verify(acceptedListener).onPermissionsAccepted(acceptedPermissions)
    }

    @Test
    fun deniedPermissionsHandled() {
        val first = Manifest.permission.ACCESS_FINE_LOCATION
        val second = Manifest.permission.SEND_SMS
        val permissions = arrayOf(first, second)
        val request = requestOf(*permissions)

        val deniedPermissions = arrayOf(first)
        val handled = request.permissionsDenied(deniedPermissions)

        assertTrue(handled)
        verify(deniedListener).onPermissionsDenied(deniedPermissions)
    }

    @Test
    fun permanentlyDeniedPermissionsHandled() {
        val first = Manifest.permission.ACCESS_FINE_LOCATION
        val second = Manifest.permission.SEND_SMS
        val permissions = arrayOf(first, second)
        val request = requestOf(*permissions)

        val permDeniedPermissions = arrayOf(first)
        val handled = request.permissionsPermanentlyDenied(permDeniedPermissions)

        assertTrue(handled)
        verify(permDeniedListener).onPermissionsPermanentlyDenied(permDeniedPermissions)
    }

    @Test
    fun rationalePermissionsHandled() {
        val first = Manifest.permission.ACCESS_FINE_LOCATION
        val second = Manifest.permission.SEND_SMS
        val permissions = arrayOf(first, second)
        val request = requestOf(*permissions)

        val rationalePermissions = arrayOf(first)
        val handled = request.permissionsShouldShowRationale(rationalePermissions)

        assertTrue(handled)
        // The nonce must be provided for the full permission set.
        verify(nonceGenerator).provideNonce(handler, permissions)
        verify(rationaleListener).onPermissionsShouldShowRationale(rationalePermissions, nonce)
    }

    @Test
    fun detachAcceptedListener() {
        val permission = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        val request = requestOf(*permission)

        var handled = request.permissionsAccepted(permission)
        assertTrue(handled)
        verify(acceptedListener).onPermissionsAccepted(permission)

        request.detachAcceptedListener()
        handled = request.permissionsAccepted(permission)
        assertFalse(handled)
        verify(acceptedListener).onPermissionsAccepted(permission)

        request.acceptedListener(acceptedListener)
        handled = request.permissionsAccepted(permission)
        assertTrue(handled)
        verify(acceptedListener, times(2)).onPermissionsAccepted(permission)

        request.detachAllListeners()
        handled = request.permissionsAccepted(permission)
        assertFalse(handled)
        verify(acceptedListener, times(2)).onPermissionsAccepted(permission)
    }

    @Test
    fun detachDeniedListener() {
        val permission = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        val request = requestOf(*permission)

        var handled = request.permissionsDenied(permission)
        assertTrue(handled)
        verify(deniedListener).onPermissionsDenied(permission)

        request.detachDeniedListener()
        handled = request.permissionsDenied(permission)
        assertFalse(handled)
        verify(deniedListener).onPermissionsDenied(permission)

        request.deniedListener(deniedListener)
        handled = request.permissionsDenied(permission)
        assertTrue(handled)
        verify(deniedListener, times(2)).onPermissionsDenied(permission)

        request.detachAllListeners()
        handled = request.permissionsDenied(permission)
        assertFalse(handled)
        verify(deniedListener, times(2)).onPermissionsDenied(permission)
    }

    @Test
    fun detachPermanentlyDeniedListener() {
        val permission = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        val request = requestOf(*permission)

        var handled = request.permissionsPermanentlyDenied(permission)
        assertTrue(handled)
        verify(permDeniedListener).onPermissionsPermanentlyDenied(permission)

        request.detachPermanentlyDeniedListener()
        handled = request.permissionsPermanentlyDenied(permission)
        assertFalse(handled)
        verify(permDeniedListener).onPermissionsPermanentlyDenied(permission)

        request.permanentlyDeniedListener(permDeniedListener)
        handled = request.permissionsPermanentlyDenied(permission)
        assertTrue(handled)
        verify(permDeniedListener, times(2)).onPermissionsPermanentlyDenied(permission)

        request.detachAllListeners()
        handled = request.permissionsPermanentlyDenied(permission)
        assertFalse(handled)
        verify(permDeniedListener, times(2)).onPermissionsPermanentlyDenied(permission)
    }

    @Test
    fun detachRationaleListener() {
        val permission = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        val request = requestOf(*permission)

        var handled = request.permissionsShouldShowRationale(permission)
        assertTrue(handled)
        verify(rationaleListener).onPermissionsShouldShowRationale(permission, nonce)

        request.detachRationaleListener()
        handled = request.permissionsShouldShowRationale(permission)
        assertFalse(handled)
        verify(rationaleListener).onPermissionsShouldShowRationale(permission, nonce)

        request.rationaleListener(rationaleListener)
        handled = request.permissionsShouldShowRationale(permission)
        assertTrue(handled)
        verify(rationaleListener, times(2)).onPermissionsShouldShowRationale(permission, nonce)

        request.detachAllListeners()
        handled = request.permissionsShouldShowRationale(permission)
        assertFalse(handled)
        verify(rationaleListener, times(2)).onPermissionsShouldShowRationale(permission, nonce)
    }

    private fun requestOf(vararg permissions: String) =
            RuntimePermissionRequest(permissions, nonceGenerator, handler).apply {
                acceptedListener(acceptedListener)
                deniedListener(deniedListener)
                permanentlyDeniedListener(permDeniedListener)
                rationaleListener(rationaleListener)
            }
}