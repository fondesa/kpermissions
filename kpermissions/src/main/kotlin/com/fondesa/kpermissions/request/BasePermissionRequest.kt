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

package com.fondesa.kpermissions.request

/**
 * Base implementation of [PermissionRequest] that implements all the listeners' logic
 * that must be the same to all its subclasses.
 */
public abstract class BasePermissionRequest : PermissionRequest {
    // Avoids ConcurrentModificationException when updating the set while iterating on it in another thread.
    // https://github.com/fondesa/kpermissions/issues/288
    protected val listeners: Set<PermissionRequest.Listener> get() = mutableListeners.toSet()
    private val mutableListeners: MutableSet<PermissionRequest.Listener> = mutableSetOf()

    override fun addListener(listener: PermissionRequest.Listener) {
        mutableListeners += listener
    }

    override fun removeListener(listener: PermissionRequest.Listener) {
        mutableListeners -= listener
    }

    override fun removeAllListeners() {
        mutableListeners.clear()
    }
}
