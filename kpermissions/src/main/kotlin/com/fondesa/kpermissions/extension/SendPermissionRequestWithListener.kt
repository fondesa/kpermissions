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

package com.fondesa.kpermissions.extension

import com.fondesa.kpermissions.PermissionStatus
import com.fondesa.kpermissions.request.PermissionRequest

/**
 * Sends the [PermissionRequest] and performs the checks on its status.
 * The result will be returned in the given [callback].
 *
 * @param callback the callback which should be invoked when the result of the permissions' request is returned.
 */
public inline fun PermissionRequest.send(crossinline callback: (List<PermissionStatus>) -> Unit) {
    addListener(
        object : PermissionRequest.Listener {
            override fun onPermissionsResult(result: List<PermissionStatus>) {
                callback(result)
                removeListener(this)
            }
        }
    )
    send()
}
