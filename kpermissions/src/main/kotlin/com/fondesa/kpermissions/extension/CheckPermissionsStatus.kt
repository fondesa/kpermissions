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

package com.fondesa.kpermissions.extension

import android.app.Activity
import android.content.Context
import android.os.Build
import androidx.core.app.ActivityCompat
import com.fondesa.kpermissions.PermissionStatus

/**
 * Checks the status of the permissions without sending a request.
 * Below API 23, the permission status can be [PermissionStatus.Granted] or [PermissionStatus.Denied.Permanently]
 * depending on the presence of the permission in the manifest.
 * Above API 23, when runtime permissions are necessary, the client doesn't know if a permission is
 * permanently denied or it was never asked to the user. In this case the returned status is [PermissionStatus.RequestRequired].
 *
 * @param firstPermission the first permission which should be requested.
 * @param otherPermissions the other permissions that must be requested, if the request
 * should handle more than one permission.
 * @return the status of each permission.
 */
public fun Activity.checkPermissionsStatus(
    firstPermission: String,
    vararg otherPermissions: String
): List<PermissionStatus> {
    val permissions = mutableListOf<String>()
    permissions += firstPermission
    permissions += otherPermissions
    return if (Build.VERSION.SDK_INT >= 23) {
        checkRuntimePermissionsStatus(permissions)
    } else {
        checkManifestPermissionsStatus(permissions)
    }
}

/**
 * Checks the status of the permissions below API 23 without sending a request.
 * The permission status can be [PermissionStatus.Granted] or [PermissionStatus.Denied.Permanently]
 * depending on the presence of the permission in the manifest.
 *
 * @param permissions the permissions which should be checked.
 * @return the status of each permission.
 */
internal fun Context.checkManifestPermissionsStatus(permissions: List<String>): List<PermissionStatus> =
    permissions.map { permission ->
        if (isPermissionGranted(permission)) {
            PermissionStatus.Granted(permission)
        } else {
            PermissionStatus.Denied.Permanently(permission)
        }
    }

/**
 * Checks the status of the permissions since API 23 without sending a request.
 * When runtime permissions are necessary, the client doesn't know if a permission is
 * permanently denied or it was never asked to the user. In this case the returned status is [PermissionStatus.RequestRequired].
 * Otherwise, the status can be [PermissionStatus.Granted] or [PermissionStatus.Denied.ShouldShowRationale].
 *
 * @param permissions the permissions which should be checked.
 * @return the status of each permission.
 */
internal fun Activity.checkRuntimePermissionsStatus(permissions: List<String>): List<PermissionStatus> =
    permissions.map { permission ->
        if (isPermissionGranted(permission)) {
            return@map PermissionStatus.Granted(permission)
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            PermissionStatus.Denied.ShouldShowRationale(permission)
        } else {
            PermissionStatus.RequestRequired(permission)
        }
    }
