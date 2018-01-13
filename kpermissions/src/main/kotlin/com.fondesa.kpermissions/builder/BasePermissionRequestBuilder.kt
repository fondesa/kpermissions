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

package com.fondesa.kpermissions.builder

import com.fondesa.kpermissions.request.PermissionRequest
import com.fondesa.kpermissions.request.runtime.RuntimePermissionHandlerProvider
import com.fondesa.kpermissions.request.runtime.nonce.PermissionNonceGenerator
import com.fondesa.kpermissions.request.runtime.nonce.RationalePermissionNonceGenerator

/**
 * Base [PermissionRequestBuilder] that specifies the common configurations.
 *
 * This builder specifies also the default configurations and throws the correct
 * exceptions when the method [build] is invoked with an invalid configuration.
 * When the method [createRequest] is called, the configuration can't change anymore,
 * all the defaults are assigned and it can be considered valid.
 */
abstract class BasePermissionRequestBuilder : PermissionRequestBuilder {

    private var permissions: Array<out String>? = null
    private var nonceGenerator: PermissionNonceGenerator? = null
    private var runtimeHandlerProvider: RuntimePermissionHandlerProvider? = null

    override fun permissions(vararg permissions: String): PermissionRequestBuilder = apply {
        this.permissions = permissions
    }

    override fun nonceGenerator(nonceGenerator: PermissionNonceGenerator): PermissionRequestBuilder = apply {
        this.nonceGenerator = nonceGenerator
    }

    override fun runtimeHandlerProvider(runtimeHandlerProvider: RuntimePermissionHandlerProvider): PermissionRequestBuilder = apply {
        this.runtimeHandlerProvider = runtimeHandlerProvider
    }

    override fun build(): PermissionRequest {
        val permissions = permissions
        if (permissions == null || permissions.isEmpty()) {
            // Throw an exception if there isn't any permission specified.
            throw IllegalArgumentException("You have to specify at least one permission.")
        }

        // Instantiate the default NonceGenerator if a custom one isn't set.
        val nonceGenerator = nonceGenerator ?: RationalePermissionNonceGenerator()

        // Get the runtime handler.
        val runtimeHandlerProvider = runtimeHandlerProvider
                ?: throw IllegalArgumentException("A runtime handler is necessary to request the permissions.")

        return createRequest(permissions,
                nonceGenerator,
                runtimeHandlerProvider)
    }

    /**
     * Create the [PermissionRequest] after the common checks are executed, the defaults are
     * assigned and this configuration can be considered valid.
     *
     * @param permissions set of permissions that must be requested.
     * @param nonceGenerator instance of [PermissionNonceGenerator] specified in this configuration.
     * @param runtimeHandlerProvider instance of [RuntimePermissionHandlerProvider] specified in this configuration.
     * @return instance of [PermissionRequest] that uses this configuration.
     */
    protected abstract fun createRequest(permissions: Array<out String>,
                                         nonceGenerator: PermissionNonceGenerator,
                                         runtimeHandlerProvider: RuntimePermissionHandlerProvider): PermissionRequest
}