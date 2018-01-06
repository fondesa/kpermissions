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

package com.fondesa.kpermissions.request.manifest

import android.content.Context
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat
import com.fondesa.kpermissions.controller.Delivering
import com.fondesa.kpermissions.controller.PermissionLifecycleController
import com.fondesa.kpermissions.request.BasePermissionRequest
import com.fondesa.kpermissions.request.PermissionRequest

/**
 * Created by antoniolig on 05/01/18.
 */
class ManifestPermissionRequest(private val context: Context,
                                private val permissions: Array<out String>,
                                private val lifecycleController: PermissionLifecycleController) :
        BasePermissionRequest() {

    override fun send() {
        val acceptedList = mutableListOf<String>()
        val deniedList = mutableListOf<String>()

        permissions.forEach {
            if (ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED) {
                acceptedList.add(it)
            } else {
                deniedList.add(it)
            }
        }

        val acceptedPermissions = acceptedList.toTypedArray()
        val deniedPermissions = deniedList.toTypedArray()

        val acceptedDelivering = lifecycleController.acceptedDelivering()
        val deniedDelivering = lifecycleController.permanentlyDeniedDelivering()

        if (deniedDelivering == Delivering.ALL) {
            if (acceptedPermissions.isEmpty()) {
                deniedListener?.onPermissionsPermanentlyDenied(deniedPermissions)
            }
        } else if (deniedDelivering == Delivering.AT_LEAST_ONE) {
            if (deniedPermissions.isNotEmpty()) {
                deniedListener?.onPermissionsPermanentlyDenied(deniedPermissions)
            }
        }

        if (acceptedDelivering == Delivering.ALL) {
            if (deniedPermissions.isEmpty()) {
                acceptedListener?.onPermissionsAccepted(acceptedPermissions)
            }
        } else if (acceptedDelivering == Delivering.AT_LEAST_ONE) {
            if (acceptedPermissions.isNotEmpty()) {
                acceptedListener?.onPermissionsAccepted(acceptedPermissions)
            }
        }
    }
}