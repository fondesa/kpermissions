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

package com.fondesa.kpermissions.request.runtime

import com.fondesa.kpermissions.controller.PermissionLifecycleController

/**
 * Created by antoniolig on 06/01/18.
 */
interface RuntimePermissionHandler {

    fun attachListener(permissions: Array<out String>, listener: Listener)

    fun attachLifecycleController(permissions: Array<out String>, controller: PermissionLifecycleController)

    fun handleRuntimePermissions(permissions: Array<out String>)

    fun requestRuntimePermissions(permissions: Array<out String>)

    interface Listener {

        fun permissionsAccepted(permissions: Array<out String>): Boolean

        fun permissionsPermanentlyDenied(permissions: Array<out String>): Boolean

        fun permissionsShouldShowRationale(permissions: Array<out String>): Boolean
    }
}