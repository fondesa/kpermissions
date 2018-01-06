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

package com.fondesa.kpermissions.extensions

import android.app.Activity
import com.fondesa.kpermissions.nonce.RationalePermissionNonceGenerator
import com.fondesa.kpermissions.request.CompatPermissionRequestBuilder
import com.fondesa.kpermissions.request.PermissionRequestBuilder
import com.fondesa.kpermissions.request.runtime.FragmentRuntimePermissionHandlerProvider

/**
 * Created by antoniolig on 05/01/18.
 */
fun Activity.permissionsBuilder(vararg permissions: String): PermissionRequestBuilder {
    val context = applicationContext
    val handler = FragmentRuntimePermissionHandlerProvider(fragmentManager)
    val nonceGenerator = RationalePermissionNonceGenerator()

    return CompatPermissionRequestBuilder(context, handler, nonceGenerator)
            .permissions(*permissions)
}