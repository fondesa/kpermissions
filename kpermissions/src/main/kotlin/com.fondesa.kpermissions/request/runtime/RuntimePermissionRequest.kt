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

import android.app.Activity
import com.fondesa.kpermissions.PermissionStatus
import com.fondesa.kpermissions.extension.checkPermissionsStatus
import com.fondesa.kpermissions.request.BasePermissionRequest
import com.fondesa.kpermissions.request.runtime.nonce.PermissionNonceGenerator

/**
 * Implementation of [BasePermissionRequest] that checks the permissions since Android M.
 *
 * The checks on the permissions are delegated to the [RuntimePermissionHandler] provided
 * to this request.
 * All the listeners could be notified.
 *
 * @property permissions the set of permissions that must be checked.
 * @property permissionNonceGenerator the [PermissionNonceGenerator] that generates the nonce for
 * the permissions that needs a rationale.
 * @property handler the [RuntimePermissionHandler] which all checks on permissions are delegated to.
 */
class RuntimePermissionRequest : BasePermissionRequest, RuntimePermissionHandler.Listener {
    private val activity: Activity?
    private val permissions: Array<out String>
    private val permissionNonceGenerator: PermissionNonceGenerator
    private val handler: RuntimePermissionHandler

    constructor(
        activity: Activity,
        permissions: Array<out String>,
        permissionNonceGenerator: PermissionNonceGenerator,
        handler: RuntimePermissionHandler
    ) : super() {
        this.activity = activity
        this.permissions = permissions
        this.permissionNonceGenerator = permissionNonceGenerator
        this.handler = handler
        // Attach this request as listener.
        handler.attachListener(permissions, this)
    }

    @Deprecated("LYRA_DEPRECATED")
    constructor(
        permissions: Array<out String>,
        permissionNonceGenerator: PermissionNonceGenerator,
        handler: RuntimePermissionHandler
    ) : super() {
        this.activity = null
        this.permissions = permissions
        this.permissionNonceGenerator = permissionNonceGenerator
        this.handler = handler
        // Attach this request as listener.
        handler.attachListener(permissions, this)
    }

    override fun checkStatus(): List<PermissionStatus> {
        val activity = activity ?: throw IllegalStateException(
            "The status can be checked only with an ${Activity::class.java.simpleName} instance."
        )
        return activity.checkPermissionsStatus(permissions.toList())
    }

    override fun send() {
        // The RuntimePermissionHandler will handle the request.
        handler.handleRuntimePermissions(permissions)
    }

    override fun onPermissionsResult(result: List<PermissionStatus>) {
        listeners.forEach { it.onPermissionsResult(result) }
    }

    override fun permissionsAccepted(permissions: Array<out String>): Boolean =
        invokeOn(acceptedListener) {
            onPermissionsAccepted(permissions)
        }

    override fun permissionsDenied(permissions: Array<out String>): Boolean =
        invokeOn(deniedListener) {
            onPermissionsDenied(permissions)
        }

    override fun permissionsPermanentlyDenied(permissions: Array<out String>): Boolean =
        invokeOn(permanentlyDeniedListener) {
            onPermissionsPermanentlyDenied(permissions)
        }

    override fun permissionsShouldShowRationale(permissions: Array<out String>): Boolean =
        invokeOn(rationaleListener) {
            val fullPermissions = this@RuntimePermissionRequest.permissions
            // Generate the nonce for all the permissions.
            val nonce = permissionNonceGenerator.generateNonce(handler, fullPermissions)
            onPermissionsShouldShowRationale(permissions, nonce)
        }

    private inline fun <T> invokeOn(instance: T?, block: T.() -> Unit): Boolean {
        if (instance == null)
            return false
        block(instance)
        return true
    }
}