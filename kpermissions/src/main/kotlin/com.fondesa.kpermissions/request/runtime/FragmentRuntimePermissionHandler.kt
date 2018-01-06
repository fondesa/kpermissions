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

import android.app.Fragment
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.util.Log
import com.fondesa.kpermissions.controller.PermissionLifecycleController
import com.fondesa.kpermissions.extensions.arePermissionsGranted
import com.fondesa.kpermissions.extensions.flatString

/**
 * Created by antoniolig on 05/01/18.
 */
@RequiresApi(Build.VERSION_CODES.M)
class FragmentRuntimePermissionHandler : Fragment(), RuntimePermissionHandler {

    private lateinit var controller: PermissionLifecycleController
    private val listeners = mutableMapOf<String, RuntimePermissionHandler.Listener>()

    private var isProcessingPermissions = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Retain the instance of the Fragment.
        retainInstance = true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode != REQ_CODE_PERMISSIONS || permissions.isEmpty()) {
            // Ignore the result if the request code doesn't match or
            // avoid the computation if there aren't processed permissions.
            return
        }

        // Now the Fragment is not processing the permissions anymore.
        isProcessingPermissions = false

        // Avoid the computation if the listener is null.
        val listener = listenerOf(permissions) ?: return

        // Get the denied permissions.
        val deniedPermissions = permissions.filterIndexed { index, _ ->
            grantResults[index] == PackageManager.PERMISSION_DENIED
        }

        if (deniedPermissions.isNotEmpty()) {
            val permissionsWithRationale = permissionsThatShouldShowRationale(deniedPermissions.toTypedArray())
            val rationaleHandled = if (permissionsWithRationale.isNotEmpty()) {
                // Show rationale of permissions.
                dispatchPermissionsShouldShowRationale(permissionsWithRationale, listener)
            } else false

            val permanentlyDeniedPermissions = deniedPermissions.minus(permissionsWithRationale).toTypedArray()
            if (!rationaleHandled && permanentlyDeniedPermissions.isNotEmpty()) {
                // Some permissions are permanently denied by the user.
                Log.d(TAG, "permissions permanently denied: ${permanentlyDeniedPermissions.flatString()}")
                dispatchPermissionsPermanentlyDenied(permanentlyDeniedPermissions, listener)
            }
        } else {
            // All permissions are accepted.
            dispatchPermissionsAccepted(permissions, listener)
        }
    }

    override fun attachListener(permissions: Array<out String>, listener: RuntimePermissionHandler.Listener) {
        val key = keyOf(permissions)
        listeners[key] = listener
    }

    override fun handleRuntimePermissions(permissions: Array<out String>,
                                          lifecycleController: PermissionLifecycleController) {
        val context = activity ?: throw NullPointerException("The activity mustn't be null.")
        this.controller = lifecycleController

        if (!context.arePermissionsGranted(*permissions)) {
            if (isProcessingPermissions) {
                // The Fragment can process only one request at the same time.
                return
            }

            val permissionsWithRationale = permissionsThatShouldShowRationale(permissions)
            val rationaleHandled = if (permissionsWithRationale.isNotEmpty()) {
                val listener = listenerOf(permissions)
                // Show rationale of permissions.
                dispatchPermissionsShouldShowRationale(permissionsWithRationale, listener)
            } else false

            if (!rationaleHandled) {
                // Request the permissions.
                requestRuntimePermissions(permissions)
            }
        } else {
            val listener = listenerOf(permissions)
            // All permissions are accepted.
            dispatchPermissionsAccepted(permissions, listener)
        }
    }

    override fun requestRuntimePermissions(permissions: Array<out String>) {
        // The Fragment is now processing some permissions.
        isProcessingPermissions = true
        Log.d(TAG, "requesting permissions: ${permissions.flatString()}")
        requestPermissions(permissions, REQ_CODE_PERMISSIONS)
    }

    private fun dispatchPermissionsAccepted(permissions: Array<out String>,
                                            listener: RuntimePermissionHandler.Listener?): Boolean =
            listener?.permissionsAccepted(permissions) ?: false

    private fun dispatchPermissionsPermanentlyDenied(permissions: Array<out String>,
                                                     listener: RuntimePermissionHandler.Listener?): Boolean =
            listener?.permissionsPermanentlyDenied(permissions) ?: false

    private fun dispatchPermissionsShouldShowRationale(permissions: Array<out String>,
                                                       listener: RuntimePermissionHandler.Listener?): Boolean =
            listener?.permissionsShouldShowRationale(permissions) ?: false

    private fun permissionsThatShouldShowRationale(permissions: Array<out String>): Array<out String> =
            permissions.filter {
                shouldShowRequestPermissionRationale(it)
            }.toTypedArray()

    private fun listenerOf(key: String): RuntimePermissionHandler.Listener? =
            listeners[key]

    private fun listenerOf(permissions: Array<out String>) = listenerOf(keyOf(permissions))

    private fun keyOf(permissions: Array<out String>) = permissions.flatString()

    companion object {
        private val TAG = FragmentRuntimePermissionHandler::class.java.simpleName
        private const val REQ_CODE_PERMISSIONS = 986
    }
}