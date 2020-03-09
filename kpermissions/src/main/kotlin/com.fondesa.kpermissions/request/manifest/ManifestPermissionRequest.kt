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

package com.fondesa.kpermissions.request.manifest

import android.content.Context
import com.fondesa.kpermissions.PermissionStatus
import com.fondesa.kpermissions.extension.checkManifestPermissionsStatus
import com.fondesa.kpermissions.isDenied
import com.fondesa.kpermissions.request.BasePermissionRequest
import com.fondesa.kpermissions.request.PermissionRequest

/**
 * Implementation of [BasePermissionRequest] that checks the permissions below Android M.
 * To check if a permission is granted or not, the only information needed is its presence in the manifest.
 * With the legacy API, only two listeners can be notified:
 * - [PermissionRequest.AcceptedListener] when ALL permissions are accepted
 * - [PermissionRequest.DeniedListener] when AT LEAST one permission is denied
 * If you are using the legacy API, please use the new [PermissionStatus] listeners since the legacy API
 * will be removed soon.
 *
 * @property context the [Context] used to check the status of the permissions.
 * @property permissions the set of permissions that must be checked.
 */
class ManifestPermissionRequest(
    private val context: Context,
    private val permissions: Array<out String>
) : BasePermissionRequest() {

    override fun checkStatus(): List<PermissionStatus> =
        context.checkManifestPermissionsStatus(permissions.toList())

    override fun send() {
        val result = checkStatus()
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
