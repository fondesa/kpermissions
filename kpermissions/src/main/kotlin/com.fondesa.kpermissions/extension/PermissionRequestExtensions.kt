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

package com.fondesa.kpermissions.extension

import com.fondesa.kpermissions.PermissionStatus
import com.fondesa.kpermissions.alias.AcceptedCallback
import com.fondesa.kpermissions.alias.DeniedCallback
import com.fondesa.kpermissions.alias.PermanentlyDeniedCallback
import com.fondesa.kpermissions.alias.RationaleCallback
import com.fondesa.kpermissions.dsl.PermissionRequestDSL
import com.fondesa.kpermissions.request.PermissionRequest
import com.fondesa.kpermissions.request.runtime.nonce.PermissionNonce

@JvmSynthetic
inline fun PermissionRequest.addListener(crossinline callback: (List<PermissionStatus>) -> Unit) {
    addListener(object : PermissionRequest.Listener {
        override fun onPermissionsResult(result: List<PermissionStatus>) {
            callback(result)
        }
    })
}

/**
 * Used to attach a [PermissionRequest.AcceptedListener] to the request that will
 * invoke an [AcceptedCallback] when it's notified.
 *
 * @param callback lambda that must be executed when the listener is notified.
 * @return the [PermissionRequest] itself.
 */
@Deprecated("LYRA_DEPRECATED")
inline fun PermissionRequest.onAccepted(crossinline callback: AcceptedCallback) = apply {
    // Attach the listener that will invoke the callback.
    acceptedListener(object : PermissionRequest.AcceptedListener {
        override fun onPermissionsAccepted(permissions: Array<out String>) {
            callback(permissions)
        }
    })
}

/**
 * Used to attach a [PermissionRequest.DeniedListener] to the request that will
 * invoke a [DeniedCallback] when it's notified.
 *
 * @param callback lambda that must be executed when the listener is notified.
 * @return the [PermissionRequest] itself.
 */
@Deprecated("LYRA_DEPRECATED")
inline fun PermissionRequest.onDenied(crossinline callback: DeniedCallback) = apply {
    // Attach the listener that will invoke the callback.
    deniedListener(object : PermissionRequest.DeniedListener {
        override fun onPermissionsDenied(permissions: Array<out String>) {
            callback(permissions)
        }
    })
}

/**
 * Used to attach a [PermissionRequest.PermanentlyDeniedListener] to the request that will
 * invoke a [PermanentlyDeniedCallback] when it's notified.
 *
 * @param callback lambda that must be executed when the listener is notified.
 * @return the [PermissionRequest] itself.
 */
@Deprecated("LYRA_DEPRECATED")
inline fun PermissionRequest.onPermanentlyDenied(crossinline callback: PermanentlyDeniedCallback) =
    apply {
        // Attach the listener that will invoke the callback.
        permanentlyDeniedListener(object : PermissionRequest.PermanentlyDeniedListener {
            override fun onPermissionsPermanentlyDenied(permissions: Array<out String>) {
                callback(permissions)
            }
        })
    }

/**
 * Used to attach a [PermissionRequest.RationaleListener] to the request that will
 * invoke a [RationaleCallback] when it's notified.
 *
 * @param callback lambda that must be executed when the listener is notified.
 * @return the [PermissionRequest] itself.
 */
@Deprecated("LYRA_DEPRECATED")
inline fun PermissionRequest.onShouldShowRationale(crossinline callback: RationaleCallback) =
    apply {
        // Attach the listener that will invoke the callback.
        rationaleListener(object : PermissionRequest.RationaleListener {
            override fun onPermissionsShouldShowRationale(
                permissions: Array<out String>,
                nonce: PermissionNonce
            ) {
                callback(permissions, nonce)
            }
        })
    }

/**
 * Used to declare the listeners of a [PermissionRequest] with a DSL style.
 * The object [PermissionRequestDSL] is used only to avoid the access to all
 * [PermissionRequest]'s public APIs.
 *
 * @param listeners lambda invoked on a context of type [PermissionRequestDSL].
 */
@Deprecated("LYRA_DEPRECATED")
fun PermissionRequest.listeners(listeners: PermissionRequestDSL.() -> Unit) {
    val dsl = PermissionRequestDSL(this)
    // Add the listeners to the request.
    listeners(dsl)
}