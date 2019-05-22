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

package com.fondesa.kpermissions.extension

import android.app.Activity
import androidx.fragment.app.Fragment
import com.fondesa.kpermissions.builder.PermissionRequestBuilder

/**
 * Creates the default [PermissionRequestBuilder] using the context of the [Activity] at which
 * this [Fragment] is attached.
 * The builder will use the default configurations and will be provided with
 * the set of [permissions] attached to it.
 *
 * @param permissions set of permissions that must be attached to the builder.
 * @return new instance of the default [PermissionRequestBuilder].
 * @throws NullPointerException if the [Fragment] is not attached to an [Activity].
 */
fun Fragment.permissionsBuilder(vararg permissions: String): PermissionRequestBuilder {
    val activity = activity ?: throw NullPointerException("The activity mustn't be null.")
    return activity.permissionsBuilder(*permissions)
}