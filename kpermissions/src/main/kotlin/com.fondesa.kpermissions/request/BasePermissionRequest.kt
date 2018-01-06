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

package com.fondesa.kpermissions.request

/**
 * Created by antoniolig on 06/01/18.
 */
abstract class BasePermissionRequest : PermissionRequest {

    protected var acceptedListener: PermissionRequest.AcceptedListener? = null
        private set

    protected var deniedListener: PermissionRequest.DeniedListener? = null
        private set

    protected var rationaleListener: PermissionRequest.RationaleListener? = null
        private set

    override fun acceptedListener(listener: PermissionRequest.AcceptedListener) {
        acceptedListener = listener
    }

    override fun deniedListener(listener: PermissionRequest.DeniedListener) {
        deniedListener = listener
    }

    override fun rationaleListener(listener: PermissionRequest.RationaleListener) {
        rationaleListener = listener
    }

    override fun detachAcceptedListener() {
        acceptedListener = null
    }

    override fun detachDeniedListener() {
        deniedListener = null
    }

    override fun detachRationaleListener() {
        rationaleListener = null
    }

    override fun detachAllListeners() {
        detachAcceptedListener()
        detachDeniedListener()
        detachRationaleListener()
    }
}