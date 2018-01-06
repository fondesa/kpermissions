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
import com.fondesa.kpermissions.extensions.arePermissionsGranted
import com.fondesa.kpermissions.nonce.PermissionNonceGenerator
import com.fondesa.kpermissions.request.PermissionRequest

/**
 * Created by antoniolig on 05/01/18.
 */
class RuntimePermissionRequest(private val context: Context,
                               private val handlerProvider: RuntimePermissionHandlerProvider,
                               private val permissionNonceGenerator: PermissionNonceGenerator,
                               private val permissions: Array<out String>,
                               private val acceptedListener: PermissionRequest.AcceptedListener?,
                               private val deniedListener: PermissionRequest.DeniedListener?,
                               private val rationaleListener: PermissionRequest.RationaleListener?) :

        PermissionRequest, RuntimePermissionHandler.Listener {

    lateinit var handler: RuntimePermissionHandler

    override fun send() {
        if (context.arePermissionsGranted(*permissions)) {
            acceptedListener?.onPermissionsAccepted(permissions)
        } else {
            handler = handlerProvider.provideHandler()
            handler.handleRuntimePermissions(permissions, this)
        }
    }

    override fun permissionsAccepted(permissions: Array<out String>): Boolean = invokeOn(acceptedListener) {
        onPermissionsAccepted(permissions)
    }

    override fun permissionsPermanentlyDenied(permissions: Array<out String>): Boolean = invokeOn(deniedListener) {
        onPermissionsPermanentlyDenied(permissions)
    }

    override fun permissionsShouldShowRationale(permissions: Array<out String>): Boolean = invokeOn(rationaleListener) {
        val nonce = permissionNonceGenerator.provideNonce(handler, permissions)
        onPermissionsShouldShowRationale(permissions, nonce)
    }

    private inline fun <T> invokeOn(instance: T?, block: T.() -> Unit): Boolean {
        if (instance == null)
            return false
        block(instance)
        return true
    }
}