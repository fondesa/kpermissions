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

package com.fondesa.kpermissions.dsl

import com.fondesa.kpermissions.alias.AcceptedCallback
import com.fondesa.kpermissions.alias.DeniedCallback
import com.fondesa.kpermissions.alias.PermanentlyDeniedCallback
import com.fondesa.kpermissions.alias.RationaleCallback
import com.fondesa.kpermissions.extension.onAccepted
import com.fondesa.kpermissions.extension.onDenied
import com.fondesa.kpermissions.extension.onPermanentlyDenied
import com.fondesa.kpermissions.extension.onShouldShowRationale
import com.fondesa.kpermissions.request.PermissionRequest

/**
 * Used only to define the listeners of a [PermissionRequest] with a DSL style.
 * It must be accessed using the extension [PermissionRequest.listeners].
 *
 * @property request the [PermissionRequest] which the listeners will be attached to.
 */
@Suppress("KDocUnresolvedReference")
@Deprecated("Use the PermissionStatus API instead.")
class PermissionRequestDSL internal constructor(private val request: PermissionRequest) {

    /**
     * Used to attach a [PermissionRequest.AcceptedListener] to the request that will
     * invoke an [AcceptedCallback] when it's notified.
     *
     * @param callback lambda that must be executed when the listener is notified.
     */
    @Deprecated("Use the PermissionStatus API instead.")
    fun onAccepted(callback: AcceptedCallback) {
        request.onAccepted(callback)
    }

    /**
     * Used to attach a [PermissionRequest.DeniedListener] to the request that will
     * invoke a [DeniedCallback] when it's notified.
     *
     * @param callback lambda that must be executed when the listener is notified.
     */
    @Deprecated("Use the PermissionStatus API instead.")
    fun onDenied(callback: DeniedCallback) {
        request.onDenied(callback)
    }

    /**
     * Used to attach a [PermissionRequest.PermanentlyDeniedListener] to the request that will
     * invoke a [PermanentlyDeniedCallback] when it's notified.
     *
     * @param callback lambda that must be executed when the listener is notified.
     */
    @Deprecated("Use the PermissionStatus API instead.")
    fun onPermanentlyDenied(callback: PermanentlyDeniedCallback) {
        request.onPermanentlyDenied(callback)
    }

    /**
     * Used to attach a [PermissionRequest.RationaleListener] to the request that will
     * invoke a [RationaleCallback] when it's notified.
     *
     * @param callback lambda that must be executed when the listener is notified.
     */
    @Deprecated("Use the PermissionStatus API instead.")
    fun onShouldShowRationale(callback: RationaleCallback) {
        request.onShouldShowRationale(callback)
    }
}
