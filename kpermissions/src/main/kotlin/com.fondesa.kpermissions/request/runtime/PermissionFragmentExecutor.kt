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

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import com.fondesa.kpermissions.extensions.arePermissionsGranted
import com.fondesa.kpermissions.extensions.flatString
import com.fondesa.kpermissions.request.runtime.support.SupportPermissionFragment

/**
 * Created by antoniolig on 06/01/18.
 */
class PermissionFragmentExecutor(private val callback: Callback) {

    private var listener: RuntimePermissionHandler.Listener? = null

    private var isProcessingPermissions = false

    fun onCreate() {
        // Retain the instance of the Fragment.
        callback.setRetainInstance(true)
    }

    fun onDetach() {
        // Avoid to retain the reference to the listener that can create a memory leak.
        // A leak can happen if the listener's instance can't be garbage collected due to
        // this Fragment's lifecycle (retainInstance = true).
        listener = null
    }

    fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode != REQ_CODE_PERMISSIONS || permissions.isEmpty()) {
            // Ignore the result if the request code doesn't match or
            // avoid the computation if there aren't processed permissions.
            return
        }

        // Now the Fragment is not processing the permissions anymore.
        isProcessingPermissions = false

        // Get the denied permissions.
        val deniedPermissions = permissions.filterIndexed { index, _ ->
            grantResults[index] == PackageManager.PERMISSION_DENIED
        }

        if (deniedPermissions.isNotEmpty()) {
            val permissionsWithRationale = permissionsThatShouldShowRationale(deniedPermissions.toTypedArray())
            if (permissionsWithRationale.isNotEmpty()) {
                // Show rationale of permissions.
                dispatchPermissionsShouldShowRationale(permissionsWithRationale)
            }

            val permanentlyDeniedPermissions = deniedPermissions.minus(permissionsWithRationale).toTypedArray()
            if (permanentlyDeniedPermissions.isNotEmpty()) {
                // Some permissions are permanently denied by the user.
                Log.d(TAG, "permissions permanently denied: ${permanentlyDeniedPermissions.flatString()}")
                dispatchPermissionsPermanentlyDenied(permanentlyDeniedPermissions)
            }
        } else {
            // All permissions are accepted.
            dispatchPermissionsAccepted(permissions)
        }
    }

    fun handleRuntimePermissions(permissions: Array<out String>,
                                 listener: RuntimePermissionHandler.Listener) {
        val context = callback.obtainContext() ?: throw NullPointerException("The activity mustn't be null.")
        // Assign the listener.
        this.listener = listener

        if (isProcessingPermissions) {
            // The Fragment can process only one request at the same time.
            return
        }

        if (!context.arePermissionsGranted(*permissions)) {
            val permissionsWithRationale = permissionsThatShouldShowRationale(permissions)
            if (permissionsWithRationale.isNotEmpty()) {
                // Show rationale of permissions.
                dispatchPermissionsShouldShowRationale(permissionsWithRationale)
            } else {
                // Request the permissions.
                requestRuntimePermissions(permissions)
            }
        } else {
            // All permissions are accepted.
            dispatchPermissionsAccepted(permissions)
        }
    }

    fun requestRuntimePermissions(permissions: Array<out String>) {
        // The Fragment is now processing some permissions.
        isProcessingPermissions = true
        Log.d(TAG, "requesting permissions: ${permissions.flatString()}")
        callback.requestPermissions(permissions, REQ_CODE_PERMISSIONS)
    }

    private fun dispatchPermissionsAccepted(permissions: Array<out String>) {
        listener?.permissionsAccepted(permissions)
    }

    private fun dispatchPermissionsPermanentlyDenied(permissions: Array<out String>) {
        listener?.permissionsPermanentlyDenied(permissions)
    }

    private fun dispatchPermissionsShouldShowRationale(permissions: Array<out String>) {
        listener?.permissionsShouldShowRationale(permissions)
    }


    private fun permissionsThatShouldShowRationale(permissions: Array<out String>): Array<out String> =
            permissions.filter {
                callback.shouldShowRequestPermissionRationale(it)
            }.toTypedArray()

    companion object {
        private val TAG = SupportPermissionFragment::class.java.simpleName
        private const val REQ_CODE_PERMISSIONS = 986
    }

    interface Callback {

        fun setRetainInstance(retain: Boolean)

        fun obtainContext(): Context?

        fun requestPermissions(permissions: Array<out String>, requestCode: Int)

        fun shouldShowRequestPermissionRationale(permission: String): Boolean
    }
}