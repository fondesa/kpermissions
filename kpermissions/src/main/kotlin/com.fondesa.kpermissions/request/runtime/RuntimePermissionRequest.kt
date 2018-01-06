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
import com.fondesa.kpermissions.nonce.PermissionNonce
import com.fondesa.kpermissions.nonce.RationalePermissionNonce
import com.fondesa.kpermissions.request.PermissionRequest

/**
 * Created by antoniolig on 05/01/18.
 */
class RuntimePermissionRequest(private val context: Context,
                               private val handlerProvider: RuntimePermissionHandlerProvider,
                               private val permissions: Array<out String>,
                               private val acceptedListener: PermissionRequest.AcceptedListener?,
                               private val deniedListener: PermissionRequest.DeniedListener?,
                               private val rationaleListener: PermissionRequest.RationaleListener?) :

        PermissionRequest, RuntimePermissionHandler.Listener {

    lateinit var nonce: PermissionNonce

    override fun send() {
        if (context.arePermissionsGranted(*permissions)) {
            acceptedListener?.onPermissionsAccepted(permissions)
        } else {
            val handler = handlerProvider.provideHandler()
            nonce = RationalePermissionNonce(handler, permissions)
            handler.handleRuntimePermissions(permissions, this)
        }
    }

    override fun permissionsAccepted(permissions: Array<out String>) {
        acceptedListener?.onPermissionsAccepted(permissions)
    }

    override fun permissionsPermanentlyDenied(permissions: Array<out String>) {
        deniedListener?.onPermissionsPermanentlyDenied(permissions)
    }

    override fun permissionsShouldShowRationale(permissions: Array<out String>) {
        rationaleListener?.onPermissionsShouldShowRationale(permissions, nonce)
    }
}