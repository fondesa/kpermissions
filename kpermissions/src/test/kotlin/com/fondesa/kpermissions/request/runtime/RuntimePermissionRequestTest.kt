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
import android.app.Activity
import com.fondesa.kpermissions.PermissionStatus
import com.fondesa.kpermissions.request.PermissionRequest
import com.fondesa.kpermissions.request.runtime.nonce.PermissionNonce
import com.fondesa.kpermissions.request.runtime.nonce.PermissionNonceGenerator
import com.fondesa.test.createActivity
import com.fondesa.test.denyPermissions
import com.fondesa.test.grantPermissions
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.eq
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Tests for [RuntimePermissionRequest].
 */
@RunWith(RobolectricTestRunner::class)
@Config(minSdk = 23)
class RuntimePermissionRequestTest {
    private val activity = spy(createActivity<Activity>())
    private val handler = mock<RuntimePermissionHandler>()
    private val nonce = mock<PermissionNonce>()
    private val nonceGenerator = mock<PermissionNonceGenerator> {
        on(it.generateNonce(eq(handler), any())).thenReturn(nonce)
    }

    private val acceptedListener = mock<PermissionRequest.AcceptedListener>()
    private val deniedListener = mock<PermissionRequest.DeniedListener>()
    private val permDeniedListener = mock<PermissionRequest.PermanentlyDeniedListener>()
    private val rationaleListener = mock<PermissionRequest.RationaleListener>()

    @Test
    fun onePermissionHandled() {
        val permission = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        val request = RuntimePermissionRequest(permission, nonceGenerator, handler)

        verify(handler).attachListener(permission, request)

        request.send()
        verify(handler).handleRuntimePermissions(permission)
    }

    @Test
    fun moreThanOnePermissionsHandled() {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.SEND_SMS
        )
        val request = RuntimePermissionRequest(permissions, nonceGenerator, handler)

        verify(handler).attachListener(permissions, request)

        request.send()
        verify(handler).handleRuntimePermissions(permissions)
    }

    @Test
    fun acceptedPermissionsHandled() {
        val first = Manifest.permission.ACCESS_FINE_LOCATION
        val second = Manifest.permission.SEND_SMS
        val permissions = arrayOf(first, second)
        val request = RuntimePermissionRequest(permissions, nonceGenerator, handler).apply {
            acceptedListener(acceptedListener)
        }

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
        val request = RuntimePermissionRequest(permissions, nonceGenerator, handler).apply {
            deniedListener(deniedListener)
        }

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
        val request = RuntimePermissionRequest(permissions, nonceGenerator, handler).apply {
            permanentlyDeniedListener(permDeniedListener)
        }

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
        val request = RuntimePermissionRequest(permissions, nonceGenerator, handler).apply {
            rationaleListener(rationaleListener)
        }

        val rationalePermissions = arrayOf(first)
        val handled = request.permissionsShouldShowRationale(rationalePermissions)

        assertTrue(handled)
        // The nonce must be provided for the full permission set.
        verify(nonceGenerator).generateNonce(handler, permissions)
        verify(rationaleListener).onPermissionsShouldShowRationale(rationalePermissions, nonce)
    }

    @Test
    fun detachAcceptedListener() {
        val permission = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        val request = RuntimePermissionRequest(permission, nonceGenerator, handler).apply {
            acceptedListener(acceptedListener)
        }

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
        val request = RuntimePermissionRequest(permission, nonceGenerator, handler).apply {
            deniedListener(deniedListener)
        }

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
        val request = RuntimePermissionRequest(permission, nonceGenerator, handler).apply {
            permanentlyDeniedListener(permDeniedListener)
        }

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
        val request = RuntimePermissionRequest(permission, nonceGenerator, handler).apply {
            rationaleListener(rationaleListener)
        }

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

    @Test(expected = IllegalStateException::class)
    fun `When rationale listener is attached but the RuntimePermissionRequest is created without nonce, an exception is thrown`() {
        val first = Manifest.permission.ACCESS_FINE_LOCATION
        val second = Manifest.permission.SEND_SMS
        val permissions = arrayOf(first, second)
        val request = RuntimePermissionRequest(activity, permissions, handler).apply {
            rationaleListener(rationaleListener)
        }

        val rationalePermissions = arrayOf(first)
        request.permissionsShouldShowRationale(rationalePermissions)
    }

    @Test
    fun `When onPermissionsResult is invoked, the result is sent to the notified listeners`() {
        val firstListener = mock<PermissionRequest.Listener>()
        val secondListener = mock<PermissionRequest.Listener>()
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

        verify(firstListener).onPermissionsResult(result)
        verify(secondListener).onPermissionsResult(result)
        verifyNoMoreInteractions(firstListener, secondListener)
    }

    @Test
    fun `When one listener is detached and onPermissionsResult is invoked, the detached listener is not notified anymore`() {
        val firstListener = mock<PermissionRequest.Listener>()
        val secondListener = mock<PermissionRequest.Listener>()
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

        verify(firstListener).onPermissionsResult(result)
        verify(secondListener).onPermissionsResult(result)
        verifyNoMoreInteractions(firstListener, secondListener)

        request.removeListener(firstListener)
        request.onPermissionsResult(result)

        verify(firstListener).onPermissionsResult(result)
        verify(secondListener, times(2)).onPermissionsResult(result)
        verifyNoMoreInteractions(firstListener, secondListener)
    }

    @Test
    fun `When all listeners are detached and onPermissionsResult is invoked, they are not notified anymore`() {
        val firstListener = mock<PermissionRequest.Listener>()
        val secondListener = mock<PermissionRequest.Listener>()
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

        verify(firstListener).onPermissionsResult(result)
        verify(secondListener).onPermissionsResult(result)
        verifyNoMoreInteractions(firstListener, secondListener)

        request.removeAllListeners()
        request.onPermissionsResult(result)

        verify(firstListener).onPermissionsResult(result)
        verify(secondListener).onPermissionsResult(result)
        verifyNoMoreInteractions(firstListener, secondListener)
    }

    @Test(expected = IllegalStateException::class)
    fun `When checkStatus() is invoked without an Activity, an exception is thrown`() {
        val request = RuntimePermissionRequest(
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            nonceGenerator,
            handler
        )

        request.checkStatus()
    }

    @Test
    fun `When checkStatus() is invoked with an Activity, the runtime permissions status is retrieved`() {
        activity.grantPermissions(Manifest.permission.SEND_SMS)
        activity.denyPermissions(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CALL_PHONE
        )
        whenever(activity.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION))
            .thenReturn(true)
        whenever(activity.shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE))
            .thenReturn(false)
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
            nonceGenerator,
            handler
        )

        val actual = request.checkStatus()
        assertEquals(expected, actual)
    }
}
