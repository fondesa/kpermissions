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
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import com.fondesa.kpermissions.extensions.flatString

/**
 * Created by antoniolig on 05/01/18.
 */
@RequiresApi(Build.VERSION_CODES.M)
abstract class FragmentRuntimePermissionHandler : Fragment(), RuntimePermissionHandler {

    private val listeners = mutableMapOf<String, RuntimePermissionHandler.Listener>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Retain the instance of the Fragment.
        retainInstance = true
    }

    final override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode != REQ_CODE_PERMISSIONS || permissions.isEmpty()) {
            // Ignore the result if the request code doesn't match or
            // avoid the computation if there aren't processed permissions.
            return
        }

        // Manage the result.
        managePermissionsResult(permissions, grantResults)
    }

    override fun attachListener(permissions: Array<out String>, listener: RuntimePermissionHandler.Listener) {
        val key = keyOf(permissions)
        listeners[key] = listener
    }

    protected abstract fun managePermissionsResult(permissions: Array<out String>, grantResults: IntArray)

    protected open fun keyOf(permissions: Array<out String>) = permissions.flatString()

    protected fun requestPermissions(permissions: Array<out String>) {
        requestPermissions(permissions, REQ_CODE_PERMISSIONS)
    }

    protected fun permissionsThatShouldShowRationale(permissions: Array<out String>): Array<out String> =
            permissions.filter {
                shouldShowRequestPermissionRationale(it)
            }.toTypedArray()

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