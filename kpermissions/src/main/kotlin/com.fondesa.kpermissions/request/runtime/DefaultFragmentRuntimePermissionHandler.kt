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

import android.content.pm.PackageManager
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.Log
import com.fondesa.kpermissions.extensions.flatString
import com.fondesa.kpermissions.extensions.isPermissionGranted

/**
 * Created by antoniolig on 05/01/18.
 */
@RequiresApi(Build.VERSION_CODES.M)
class DefaultFragmentRuntimePermissionHandler : FragmentRuntimePermissionHandler() {

    private var isProcessingPermissions = false

    override fun managePermissionsResult(permissions: Array<out String>, grantResults: IntArray) {
        // Now the Fragment is not processing the permissions anymore.
        isProcessingPermissions = false

        // Get the listener for this set of permissions.
        val listener = listenerOf(permissions)

        // Get the denied permissions.
        val deniedPermissions = permissions.filterIndexed { index, _ ->
            grantResults[index] == PackageManager.PERMISSION_DENIED
        }

        if (deniedPermissions.isNotEmpty()) {
            var rationaleHandled = false
            val permissionsWithRationale = permissionsThatShouldShowRationale(deniedPermissions.toTypedArray())
            if (permissionsWithRationale.isNotEmpty()) {
                // Show rationale of permissions if possible.
                val rationaleShown = listener.permissionsShouldShowRationale(permissionsWithRationale)
                if (!rationaleShown) {
                    // Otherwise, if possible, notify the listener that the permissions are denied.
                    rationaleHandled = listener.permissionsDenied(permissionsWithRationale)
                }
            }

            val permanentlyDeniedPermissions = deniedPermissions.minus(permissionsWithRationale).toTypedArray()
            if (!rationaleHandled && permanentlyDeniedPermissions.isNotEmpty()) {
                // Some permissions are permanently denied by the user.
                Log.d(TAG, "permissions permanently denied: ${permanentlyDeniedPermissions.flatString()}")
                listener.permissionsPermanentlyDenied(permanentlyDeniedPermissions)
            }
        } else {
            // All permissions are accepted.
            listener.permissionsAccepted(permissions)
        }
    }

    override fun handleRuntimePermissions(permissions: Array<out String>) {
        val context = activity ?: throw NullPointerException("The activity mustn't be null.")

        val areAllGranted = permissions.all {
            context.isPermissionGranted(it)
        }
        if (!areAllGranted) {
            if (isProcessingPermissions) {
                // The Fragment can process only one request at the same time.
                return
            }

            val permissionsWithRationale = permissionsThatShouldShowRationale(permissions)
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
        }
    }

    override fun requestRuntimePermissions(permissions: Array<out String>) {
        // The Fragment is now processing some permissions.
        isProcessingPermissions = true
        Log.d(TAG, "requesting permissions: ${permissions.flatString()}")
        requestPermissions(permissions)
    }
}