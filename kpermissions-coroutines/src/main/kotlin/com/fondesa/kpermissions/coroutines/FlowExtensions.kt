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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * Collects the [PermissionRequest]'s status list.
 * The returned [Flow] emits the list of [PermissionStatus] when the result of [PermissionRequest.send] is received.
 * The documentation of the possible values of [PermissionStatus] can be found at [PermissionRequest.Listener.onPermissionsResult].
 *
 * @return a new [Flow] which notifies the collector with a new item every time the result of [PermissionRequest.send] is received.
 * @see PermissionRequest.Listener.onPermissionsResult
 */
@ExperimentalCoroutinesApi
public fun PermissionRequest.flow(): Flow<List<PermissionStatus>> = callbackFlow {
    val listener = PermissionRequest.Listener { result -> offer(result) }
    addListener(listener)
    awaitClose { removeListener(listener) }
}
