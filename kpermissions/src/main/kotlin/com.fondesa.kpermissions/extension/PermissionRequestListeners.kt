/*
 * Copyright (c) 2020 Giorgio Antonioli
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

@file:Suppress("DEPRECATION", "OverridingDeprecatedMember", "TYPEALIAS_EXPANSION_DEPRECATION")

package com.fondesa.kpermissions.extension

import com.fondesa.kpermissions.PermissionStatus
import com.fondesa.kpermissions.alias.AcceptedCallback
import com.fondesa.kpermissions.alias.DeniedCallback
import com.fondesa.kpermissions.alias.PermanentlyDeniedCallback
import com.fondesa.kpermissions.alias.RationaleCallback
import com.fondesa.kpermissions.dsl.PermissionRequestDSL
import com.fondesa.kpermissions.request.PermissionRequest

/**
 * Adds a [PermissionRequest.Listener] using a convenience lambda.
 * This method isn't needed in Java since there's the SAM conversion for [PermissionRequest.Listener].
 *
 * @param callback the lambda which must be executed when the listener is notified.
 */
@JvmSynthetic
@Deprecated("Use the fun interface PermissionRequest.Listener.")
inline fun PermissionRequest.addListener(crossinline callback: (List<PermissionStatus>) -> Unit) {
    addListener { result -> callback(result) }
}

/**
 * Used to attach a [PermissionRequest.AcceptedListener] to the request that will
 * invoke an [AcceptedCallback] when it's notified.
 *
 * @param callback lambda that must be executed when the listener is notified.
 * @return the [PermissionRequest] itself.
 */
@Deprecated("Use the PermissionStatus API instead.")
inline fun PermissionRequest.onAccepted(crossinline callback: AcceptedCallback) = apply {
    // Attach the listener that will invoke the callback.
    acceptedListener { permissions -> callback(permissions) }
}

/**
 * Used to attach a [PermissionRequest.DeniedListener] to the request that will
 * invoke a [DeniedCallback] when it's notified.
 *
 * @param callback lambda that must be executed when the listener is notified.
 * @return the [PermissionRequest] itself.
 */
@Deprecated("Use the PermissionStatus API instead.")
inline fun PermissionRequest.onDenied(crossinline callback: DeniedCallback) = apply {
    // Attach the listener that will invoke the callback.
    deniedListener { permissions -> callback(permissions) }
}

/**
 * Used to attach a [PermissionRequest.PermanentlyDeniedListener] to the request that will
 * invoke a [PermanentlyDeniedCallback] when it's notified.
 *
 * @param callback lambda that must be executed when the listener is notified.
 * @return the [PermissionRequest] itself.
 */
@Deprecated("Use the PermissionStatus API instead.")
inline fun PermissionRequest.onPermanentlyDenied(crossinline callback: PermanentlyDeniedCallback) = apply {
    // Attach the listener that will invoke the callback.
    permanentlyDeniedListener { permissions -> callback(permissions) }
}

/**
 * Used to attach a [PermissionRequest.RationaleListener] to the request that will
 * invoke a [RationaleCallback] when it's notified.
 *
 * @param callback lambda that must be executed when the listener is notified.
 * @return the [PermissionRequest] itself.
 */
@Deprecated("Use the PermissionStatus API instead.")
inline fun PermissionRequest.onShouldShowRationale(crossinline callback: RationaleCallback) = apply {
    // Attach the listener that will invoke the callback.
    rationaleListener { permissions, nonce -> callback(permissions, nonce) }
}

/**
 * Used to declare the listeners of a [PermissionRequest] with a DSL style.
 * The object [PermissionRequestDSL] is used only to avoid the access to all
 * [PermissionRequest]'s public APIs.
 *
 * @param listeners lambda invoked on a context of type [PermissionRequestDSL].
 */
@Deprecated("Use the PermissionStatus API instead.")
fun PermissionRequest.listeners(listeners: PermissionRequestDSL.() -> Unit) {
    val dsl = PermissionRequestDSL(this)
    // Add the listeners to the request.
    listeners(dsl)
}
