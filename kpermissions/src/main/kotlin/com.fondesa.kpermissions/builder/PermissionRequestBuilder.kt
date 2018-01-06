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

import com.fondesa.kpermissions.controller.PermissionLifecycleController
import com.fondesa.kpermissions.request.PermissionRequest
import com.fondesa.kpermissions.request.runtime.RuntimePermissionHandlerProvider
import com.fondesa.kpermissions.request.runtime.nonce.PermissionNonceGenerator

/**
 * Created by antoniolig on 05/01/18.
 */
interface PermissionRequestBuilder {

    fun permissions(vararg permissions: String): PermissionRequestBuilder

    fun lifecycleController(lifecycleController: PermissionLifecycleController): PermissionRequestBuilder

    fun nonceGenerator(nonceGenerator: PermissionNonceGenerator): PermissionRequestBuilder

    fun runtimeHandlerProvider(runtimeHandlerProvider: RuntimePermissionHandlerProvider): PermissionRequestBuilder

    fun build(): PermissionRequest

    fun send(): PermissionRequest
}