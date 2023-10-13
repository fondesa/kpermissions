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

import androidx.lifecycle.LiveData
import com.fondesa.kpermissions.PermissionStatus
import com.fondesa.kpermissions.request.PermissionRequest

/**
 * Observes the [PermissionRequest]'s status.
 * The returned [LiveData] emits the list of [PermissionStatus] when the result of [PermissionRequest.send] is received.
 * The documentation of the possible values of [PermissionStatus] can be found at [PermissionRequest.Listener.onPermissionsResult].
 *
 * @return a new [LiveData] which emits a new item to the observers every time the result of [PermissionRequest.send] is received.
 * @see PermissionRequest.Listener.onPermissionsResult
 */
public fun PermissionRequest.liveData(): LiveData<List<PermissionStatus>> = PermissionsLiveData(this)

private class PermissionsLiveData(private val request: PermissionRequest) : LiveData<List<PermissionStatus>>() {
    private var listener: PermissionRequest.Listener? = null

    override fun onActive() {
        super.onActive()
        listener = PermissionRequest.Listener { result -> value = result }.also(request::addListener)
    }

    override fun onInactive() {
        super.onInactive()
        listener?.let(request::removeListener)
        listener = null
    }
}
