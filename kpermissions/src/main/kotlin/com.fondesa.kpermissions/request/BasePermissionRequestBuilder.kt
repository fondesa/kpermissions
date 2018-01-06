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
abstract class BasePermissionRequestBuilder : PermissionRequestBuilder {

    private var permissions: Array<out String>? = null
    private var acceptedListener: PermissionRequest.AcceptedListener? = null
    private var deniedListener: PermissionRequest.DeniedListener? = null
    private var rationaleListener: PermissionRequest.RationaleListener? = null

    override fun permissions(vararg permissions: String): PermissionRequestBuilder = apply {
        this.permissions = permissions
    }

    override fun acceptedListener(acceptedListener: PermissionRequest.AcceptedListener) = apply {
        this.acceptedListener = acceptedListener
    }

    override fun deniedListener(deniedListener: PermissionRequest.DeniedListener) = apply {
        this.deniedListener = deniedListener
    }

    override fun rationaleListener(rationaleListener: PermissionRequest.RationaleListener) = apply {
        this.rationaleListener = rationaleListener
    }

    override fun build(): PermissionRequest {
        val permissions = permissions
        if (permissions == null || permissions.isEmpty()) {
            // Throw an exception if there isn't any permission specified.
            throw IllegalArgumentException("You have to specify at least one permission.")
        }

        return createRequest(permissions, acceptedListener, deniedListener, rationaleListener)
    }

    override fun send(): PermissionRequest {
        // Build the request.
        val request = build()
        // Send it directly.
        request.send()
        return request
    }

    abstract fun createRequest(permissions: Array<out String>,
                               acceptedListener: PermissionRequest.AcceptedListener?,
                               deniedListener: PermissionRequest.DeniedListener?,
                               rationaleListener: PermissionRequest.RationaleListener?): PermissionRequest
}