/*
 * Copyright (c) 2020 Fondesa
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

import android.app.Activity
import android.os.Build
import com.fondesa.kpermissions.request.PermissionRequest
import com.fondesa.kpermissions.request.manifest.ManifestPermissionRequest
import com.fondesa.kpermissions.request.runtime.RuntimePermissionHandler
import com.fondesa.kpermissions.request.runtime.RuntimePermissionHandlerProvider
import com.fondesa.kpermissions.request.runtime.RuntimePermissionRequest
import com.fondesa.kpermissions.request.runtime.nonce.PermissionNonceGenerator

/**
 * Implementation of [BasePermissionRequestBuilder] that creates a different request depending
 * on the device's API version.
 *
 * Since Android M, this builder creates a [RuntimePermissionRequest] that uses the
 * [RuntimePermissionHandler] provided by this builder.
 * Below Android M, this builder creates a [ManifestPermissionRequest].
 *
 * @property activity the [Activity] used to create the [PermissionRequest].
 */
class CompatPermissionRequestBuilder internal constructor(private val activity: Activity) :
    BasePermissionRequestBuilder() {

    override fun createRequest(
        permissions: Array<out String>,
        nonceGenerator: PermissionNonceGenerator,
        runtimeHandlerProvider: RuntimePermissionHandlerProvider
    ): PermissionRequest {

        return if (Build.VERSION.SDK_INT >= 23) {
            // Provide the handler.
            val handler = runtimeHandlerProvider.provideHandler()
            // Create the runtime request.
            RuntimePermissionRequest(activity, permissions, nonceGenerator, handler)
        } else {
            // Create the manifest request.
            ManifestPermissionRequest(activity, permissions)
        }
    }
}