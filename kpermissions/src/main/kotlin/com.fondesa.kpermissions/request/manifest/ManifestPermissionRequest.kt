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

import android.content.Context
import com.fondesa.kpermissions.PermissionStatus
import com.fondesa.kpermissions.extension.isPermissionGranted
import com.fondesa.kpermissions.isDenied
import com.fondesa.kpermissions.request.BasePermissionRequest
import com.fondesa.kpermissions.request.PermissionRequest

/**
 * Implementation of [BasePermissionRequest] that checks the permissions below Android M.
 *
 * The check is done using [Context.isPermissionGranted] that is compatible with all APIs.
 * In this case, the check is executed using only the information contained in the manifest.
 * Only two listeners can be notified:
 * - [PermissionRequest.AcceptedListener] when ALL permissions are accepted
 * - [PermissionRequest.DeniedListener] when AT LEAST one permission is denied
 *
 * @property context the [Context] used to check the status of the permissions.
 * @property permissions the set of permissions that must be checked.
 */
class ManifestPermissionRequest(
    private val context: Context,
    private val permissions: Array<out String>
) : BasePermissionRequest() {

    override fun checkCurrentStatus(): List<PermissionStatus> = permissions.map { permission ->
        if (context.isPermissionGranted(permission)) {
            PermissionStatus.Granted(permission)
        } else {
            PermissionStatus.Denied.Permanently(permission)
        }
    }

    override fun send() {
        val result = checkCurrentStatus()
        listeners.forEach { it.onPermissionsResult(result) }

        // Get all the permissions that are denied.
        val deniedPermissions = result.filter { status -> status.isDenied() }
            .map { it.permission }
            .toTypedArray()

        if (deniedPermissions.isNotEmpty()) {
            deniedListener?.onPermissionsDenied(deniedPermissions)
        } else {
            // If there aren't denied permissions, it means that are all accepted.
            acceptedListener?.onPermissionsAccepted(permissions)
        }


    }
}