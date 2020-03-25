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

package com.fondesa.kpermissions.coroutines

import com.fondesa.kpermissions.PermissionStatus
import com.fondesa.kpermissions.request.PermissionRequest
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Sends the permission request suspending until its result isn't received.
 * The list of [PermissionStatus] is the result received from the call to [PermissionRequest.send].
 * The documentation of the possible values of [PermissionStatus] can be found at [PermissionRequest.Listener.onPermissionsResult].
 *
 * @return the list of [PermissionStatus] received from the call to [PermissionRequest.send].
 * @see PermissionRequest.Listener.onPermissionsResult
 */
suspend fun PermissionRequest.sendSuspend(): List<PermissionStatus> = suspendCancellableCoroutine { continuation ->
    val listener = object : PermissionRequest.Listener {
        override fun onPermissionsResult(result: List<PermissionStatus>) {
            removeListener(this)
            continuation.resume(result)
        }
    }
    addListener(listener)
    continuation.invokeOnCancellation { removeListener(listener) }
    send()
}
