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

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.fondesa.kpermissions.extension.flatString

/**
 * Implementation of [RuntimePermissionHandler] that uses a [Fragment] to handle the permissions.
 *
 * This [Fragment] retains its instance to persist across configuration changes.
 * It also haven't a view to make itself hidden.
 * It handles the listeners' caching and retrieving.
 * The runtime permissions are requested through [requestPermissions] and the result
 * will be notified to [onRequestPermissionsResult].
 */
@RequiresApi(Build.VERSION_CODES.M)
abstract class FragmentRuntimePermissionHandler : Fragment(), RuntimePermissionHandler {

    private val listeners = mutableMapOf<String, RuntimePermissionHandler.Listener>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Retain the instance of the Fragment.
        retainInstance = true
    }

    final override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode != REQ_CODE_PERMISSIONS || permissions.isEmpty()) {
            // Ignore the result if the request code doesn't match or
            // avoid the computation if there aren't processed permissions.
            return
        }

        // Manage the result.
        managePermissionsResult(permissions, grantResults)
    }

    override fun attachListener(
        permissions: Array<out String>,
        listener: RuntimePermissionHandler.Listener
    ) {
        val key = keyOf(permissions)
        listeners[key] = listener
    }

    /**
     * Manage the result returned from a permissions' request.
     * This method is invoked only when the request code of the result matches with the
     * request code of the request.
     *
     * @param permissions the permissions that were checked with the request.
     * @param grantResults the array containing the result of the permissions' request.
     * The [grantResults] array matches the [permissions] array 1 by 1, so the size is the same.
     */
    protected abstract fun managePermissionsResult(
        permissions: Array<out String>,
        grantResults: IntArray
    )

    /**
     * Get a unique key from a set of permissions.
     *
     * @param permissions the permissions that are used to generate the key.
     * @return unique key in [String] format generated from [permissions].
     */
    protected open fun keyOf(permissions: Array<out String>): String = permissions.flatString()

    /**
     * Request the permissions with a fixed request code.
     *
     * @param permissions set of permissions that must be requested.
     */
    protected fun requestPermissions(permissions: Array<out String>) {
        requestPermissions(permissions, REQ_CODE_PERMISSIONS)
    }

    /**
     * Get the [RuntimePermissionHandler.Listener] identified with a set of permissions.
     *
     * @param permissions set of permissions used to identify the correct [RuntimePermissionHandler.Listener].
     * @return correct instance of [RuntimePermissionHandler.Listener].
     * @throws IllegalArgumentException if a [RuntimePermissionHandler.Listener] for the given [permissions]
     * wasn't found.
     */
    protected fun listenerOf(permissions: Array<out String>): RuntimePermissionHandler.Listener {
        val key = keyOf(permissions)
        return listeners.getOrElse(key) {
            throw IllegalArgumentException("You need a listener for the key $key.")
        }
    }

    companion object {
        val TAG: String = FragmentRuntimePermissionHandler::class.java.simpleName
        private const val REQ_CODE_PERMISSIONS = 986
    }
}