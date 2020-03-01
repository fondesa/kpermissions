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

package com.fondesa.kpermissions.request.manifest

import android.Manifest
import com.fondesa.kpermissions.request.PermissionRequest
import com.fondesa.test.context
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf

/**
 * Tests for [ManifestPermissionRequest].
 */
@RunWith(RobolectricTestRunner::class)
class ManifestPermissionRequestTest {
    private val acceptedListener = mock<PermissionRequest.AcceptedListener>()
    private val deniedListener = mock<PermissionRequest.DeniedListener>()

    @Test
    fun requestWithoutListeners() {
        val shadowApp = shadowOf(context)
        val permission = Manifest.permission.ACCESS_FINE_LOCATION
        // The request will be sent without listeners and it mustn't throw an exception.
        val request = requestOf(permission, listeners = false)

        request.send()

        // Grant the permission.
        shadowApp.grantPermissions(permission)
        request.send()

        // Revoke the permission.
        shadowApp.denyPermissions(permission)
        request.send()
    }

    @Test
    fun onePermissionHandled() {
        val shadowApp = shadowOf(context)
        val permission = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        val request = requestOf(*permission)

        request.send()
        // The first time, the denied listener must be called.
        verify(deniedListener).onPermissionsDenied(permission)

        // Grant the permission.
        shadowApp.grantPermissions(*permission)
        request.send()
        verify(acceptedListener).onPermissionsAccepted(permission)

        // Revoke the permission.
        shadowApp.denyPermissions(*permission)
        request.send()
        verify(deniedListener, times(2)).onPermissionsDenied(permission)
    }

    @Test
    fun moreThanOnePermissionsHandled() {
        val shadowApp = shadowOf(context)
        val first = Manifest.permission.ACCESS_FINE_LOCATION
        val second = Manifest.permission.SEND_SMS
        val permissions = arrayOf(first, second)
        val request = requestOf(*permissions)

        request.send()
        // The first time, the denied listener must be called.
        verify(deniedListener).onPermissionsDenied(permissions)

        // Grant the permissions.
        shadowApp.grantPermissions(*permissions)
        request.send()
        verify(acceptedListener).onPermissionsAccepted(permissions)

        // Revoke the permissions.
        shadowApp.denyPermissions(*permissions)
        request.send()
        verify(deniedListener, times(2)).onPermissionsDenied(permissions)

        // Grant only the first permission.
        shadowApp.grantPermissions(first)
        request.send()
        verify(deniedListener).onPermissionsDenied(arrayOf(second))
    }

    @Test
    fun detachAcceptedListener() {
        val permission = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        val request = requestOf(*permission)

        // Grant the permission
        shadowOf(context).grantPermissions(*permission)

        request.send()
        verify(acceptedListener).onPermissionsAccepted(permission)

        request.detachAcceptedListener()
        request.send()
        verify(acceptedListener).onPermissionsAccepted(permission)

        request.acceptedListener(acceptedListener)
        request.send()
        verify(acceptedListener, times(2)).onPermissionsAccepted(permission)

        request.detachAllListeners()
        request.send()
        verify(acceptedListener, times(2)).onPermissionsAccepted(permission)
    }

    @Test
    fun detachDeniedListener() {
        val permission = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        val request = requestOf(*permission)

        request.send()
        verify(deniedListener).onPermissionsDenied(permission)

        request.detachDeniedListener()
        request.send()
        verify(deniedListener).onPermissionsDenied(permission)

        request.deniedListener(deniedListener)
        request.send()
        verify(deniedListener, times(2)).onPermissionsDenied(permission)

        request.detachAllListeners()
        request.send()
        verify(deniedListener, times(2)).onPermissionsDenied(permission)
    }

    private fun requestOf(
        vararg permissions: String,
        listeners: Boolean = true
    ): ManifestPermissionRequest {
        val request = ManifestPermissionRequest(context, permissions)
        if (listeners) {
            request.acceptedListener(acceptedListener)
            request.deniedListener(deniedListener)
        }
        return request
    }
}