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

import com.fondesa.kpermissions.controller.PermissionLifecycleController
import com.fondesa.kpermissions.request.BasePermissionRequest
import com.fondesa.kpermissions.request.runtime.nonce.PermissionNonceGenerator

/**
 * Created by antoniolig on 05/01/18.
 */
class RuntimePermissionRequest(private val permissions: Array<out String>,
                               private val lifecycleController: PermissionLifecycleController,
                               private val permissionNonceGenerator: PermissionNonceGenerator,
                               private val handler: RuntimePermissionHandler) :

        BasePermissionRequest(),
        RuntimePermissionHandler.Listener {

    init {
        // Attach this request as listener.
        handler.attachListener(permissions, this)
    }

    override fun send() {
        // Send permission request.
        handler.handleRuntimePermissions(permissions, lifecycleController)
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