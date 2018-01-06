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

import android.app.Fragment
import android.os.Build
import com.fondesa.kpermissions.request.CompatPermissionRequestBuilder
import com.fondesa.kpermissions.request.PermissionRequestBuilder
import com.fondesa.kpermissions.request.runtime.normal.NormalRuntimePermissionHandlerProvider
import com.fondesa.kpermissions.request.runtime.support.SupportRuntimePermissionHandlerProvider

/**
 * Created by antoniolig on 05/01/18.
 */
fun Fragment.permissionsBuilder(vararg permissions: String): PermissionRequestBuilder {
    val context = activity?.applicationContext ?:
            throw NullPointerException("The activity mustn't be null.")

    // The child FragmentManager isn't available below API 17.
    val manager = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
        fragmentManager
    } else {
        childFragmentManager
    }

    val handler = NormalRuntimePermissionHandlerProvider(manager)

    return CompatPermissionRequestBuilder(context, handler)
            .permissions(*permissions)
}

fun android.support.v4.app.Fragment.permissionsBuilder(vararg permissions: String): PermissionRequestBuilder {
    val context = activity?.applicationContext ?:
            throw NullPointerException("The activity mustn't be null.")
    val handler = SupportRuntimePermissionHandlerProvider(fragmentManager!!)

    return CompatPermissionRequestBuilder(context, handler)
            .permissions(*permissions)
}