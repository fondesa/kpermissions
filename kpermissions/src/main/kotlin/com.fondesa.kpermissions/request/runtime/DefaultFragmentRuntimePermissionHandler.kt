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

package com.fondesa.kpermissions.request.runtime

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.annotation.RequiresApi
import com.fondesa.kpermissions.PermissionStatus
import com.fondesa.kpermissions.allGranted
import com.fondesa.kpermissions.extension.checkRuntimePermissionsStatus
import com.fondesa.kpermissions.isPermanentlyDenied
import com.fondesa.kpermissions.request.PermissionRequest
import com.fondesa.kpermissions.shouldShowRationale

/**
 * Implementation of [FragmentRuntimePermissionHandler] that specifies the lifecycle of the
 * runtime permissions' requests.
 *
 * It can process maximum one permissions' request at the same. This is done to avoid multiple
 * requests handled by the OS together that will show overlapped permission's dialogs.
 *
 * If you are using the new status API:
 * The [PermissionRequest.Listener] will be notified with a number of [PermissionStatus] equals to
 * the number of permissions requested.
 * The possible status which can be notified with a runtime request are:
 * - [PermissionStatus.Granted] -> the permission is granted
 * - [PermissionStatus.Denied.ShouldShowRationale] -> the permission is denied by the user and it can be useful to
 * show a rationale explaining the motivation of this permission request
 * - [PermissionStatus.Denied.Permanently] -> the permission is permanently denied by the user using the
 * "never ask again" button on the permissions dialog.
 *
 * If you are using the legacy API:
 * Considering a lifecycle the group of phases that passes from [requestPermissions] till the end
 * of [managePermissionsResult], this handler notifies the [RuntimePermissionHandler.Listener]
 * for maximum one event during the lifecycle.
 * For example if a permission's request contains two permissions and the user accepts only one of
 * them, the [RuntimePermissionHandler.Listener] won't be notified on the accepted permissions, but
 * only on the denied one.
 *
 * This is done following the consideration that a permissions' request must contain only
 * the permissions that are related to a single functionality, so the functionality mustn't be
 * available if the user doesn't "resolve" the permissions.
 *
 * Every state needs a [PermissionRequest]'s listener attached to be handled, otherwise the
 * application will proceed to the next state, if any.
 *
 * The available states are the following.
 * - rationale: happens when a permission is denied and a rationale is needed. It can be notified
 * before the request is sent and after the result is received.
 * - denied: happens when a permission is denied and the rationale state isn't handled. It can be
 * notified after the result is received.
 * - permanently denied: happens when the user selects the "never ask again" checkbox and the
 * rationale/denied state isn't handled. It can be notified after the result is received.
 * - accepted: happens when the user accepts ALL the permissions. It can be notified before
 * the request is sent or after the result is received.
 */
@RequiresApi(23)
class DefaultFragmentRuntimePermissionHandler : FragmentRuntimePermissionHandler() {

    private var isProcessingPermissions = false
    private var dispatchableHandleRuntimePermissions: (() -> Unit)? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        dispatchableHandleRuntimePermissions?.invoke()
        dispatchableHandleRuntimePermissions = null
    }

    override fun managePermissionsResult(permissions: Array<out String>, grantResults: IntArray) {
        // Now the Fragment is not processing the permissions anymore.
        isProcessingPermissions = false

        // Get the listener for this set of permissions.
        val listener = listenerOf(permissions)

        val result = permissions.mapIndexed { index, permission ->
            val isGranted = grantResults[index] == PackageManager.PERMISSION_GRANTED
            if (isGranted) {
                return@mapIndexed PermissionStatus.Granted(permission)
            }
            if (shouldShowRequestPermissionRationale(permission)) {
                PermissionStatus.Denied.ShouldShowRationale(permission)
            } else {
                PermissionStatus.Denied.Permanently(permission)
            }
        }
        listener.onPermissionsResult(result)

        // Get the denied permissions.
        val deniedPermissions = result.filterIsInstance<PermissionStatus.Denied>()

        if (deniedPermissions.isNotEmpty()) {
            var rationaleHandled = false
            // Get the permissions that need a rationale.
            val permissionsWithRationale = deniedPermissions
                .filter { it.shouldShowRationale() }
                .map { it.permission }
                .toTypedArray()
            if (permissionsWithRationale.isNotEmpty()) {
                // Show rationale of permissions if possible.
                rationaleHandled = listener.permissionsShouldShowRationale(permissionsWithRationale)
                if (!rationaleHandled) {
                    // Otherwise, if possible, notify the listener that the permissions are denied.
                    rationaleHandled = listener.permissionsDenied(permissionsWithRationale)
                }
            }

            val permanentlyDeniedPermissions = deniedPermissions
                .filter { it.isPermanentlyDenied() }
                .map { it.permission }
                .toTypedArray()
            if (!rationaleHandled && permanentlyDeniedPermissions.isNotEmpty()) {
                // Some permissions are permanently denied by the user.
                Log.d(
                    TAG,
                    "permissions permanently denied: ${permanentlyDeniedPermissions.joinToString(
                        separator = ","
                    )}"
                )
                listener.permissionsPermanentlyDenied(permanentlyDeniedPermissions)
            }
        } else {
            // All permissions are accepted.
            listener.permissionsAccepted(permissions)
        }
    }

    override fun handleRuntimePermissions(permissions: Array<out String>) {
        if (isAdded) {
            handleRuntimePermissionsWhenAdded(permissions)
        } else {
            dispatchableHandleRuntimePermissions = {
                handleRuntimePermissionsWhenAdded(permissions)
            }
        }
    }

    private fun handleRuntimePermissionsWhenAdded(permissions: Array<out String>) {
        val activity = requireActivity()
        val currentStatus = activity.checkRuntimePermissionsStatus(permissions.toList())
        val areAllGranted = currentStatus.allGranted()
        if (!areAllGranted) {
            if (isProcessingPermissions) {
                // The Fragment can process only one request at the same time.
                return
            }
            val permissionsWithRationale = currentStatus
                .filter { it.shouldShowRationale() }
                .map { it.permission }
                .toTypedArray()

            val rationaleHandled = if (permissionsWithRationale.isNotEmpty()) {
                val listener = listenerOf(permissions)
                // Show rationale of permissions.
                listener.permissionsShouldShowRationale(permissionsWithRationale)
            } else false

            if (!rationaleHandled) {
                // Request the permissions.
                requestRuntimePermissions(permissions)
            }
        } else {
            val listener = listenerOf(permissions)
            // All permissions are accepted.
            listener.permissionsAccepted(permissions)
            listener.onPermissionsResult(currentStatus)
        }
    }

    override fun requestRuntimePermissions(permissions: Array<out String>) {
        // The Fragment is now processing some permissions.
        isProcessingPermissions = true
        Log.d(TAG, "requesting permissions: ${permissions.joinToString(separator = ",")}")
        requestPermissions(permissions)
    }
}
