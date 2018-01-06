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

package com.fondesa.kpermissions.request

import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import com.fondesa.kpermissions.extensions.arePermissionsGranted
import com.fondesa.kpermissions.extensions.flatString

/**
 * Created by antoniolig on 05/01/18.
 */
class PermissionFragment : Fragment() {

    private var listener: PermissionRequestImpl.Listener? = null

    private var isProcessingPermissions = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Retain the instance of the Fragment.
        retainInstance = true
    }

    override fun onDetach() {
        super.onDetach()
        // Avoid to retain the reference to the listener that can create a memory leak.
        // A leak can happen if the listener's instance can't be garbage collected due to
        // this Fragment's lifecycle (retainInstance = true).
        listener = null
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

    fun requestPermissions(permissions: Array<out String>, listener: PermissionRequestImpl.Listener) {
        val activity = activity ?: throw NullPointerException("The activity mustn't be null.")

        // Assign the listener.
        this.listener = listener

        if (isProcessingPermissions) {
            // The Fragment can process only one request at the same time.
            return
        }

        if (!activity.arePermissionsGranted(*permissions)) {
            val permissionsWithRationale = permissionsThatShouldShowRationale(permissions)
            if (permissionsWithRationale.isNotEmpty()) {
                // Show rationale of permissions.
                dispatchPermissionsShouldShowRationale(permissionsWithRationale)
            } else {
                // Request the permissions.
                requestPermissionsAvoidingChecks(permissions)
            }
        } else {
            // All permissions are accepted.
            dispatchPermissionsAccepted(permissions)
        }
    }

    fun requestPermissionsAvoidingChecks(permissions: Array<out String>) {
        // The Fragment is now processing some permissions.
        isProcessingPermissions = true
        Log.d(TAG, "requesting permissions: ${permissions.flatString()}")
        requestPermissions(permissions, REQ_CODE_PERMISSIONS)
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
                shouldShowRequestPermissionRationale(it)
            }.toTypedArray()


    companion object {
        private val TAG = PermissionFragment::class.java.simpleName
        private const val REQ_CODE_PERMISSIONS = 986
    }
}