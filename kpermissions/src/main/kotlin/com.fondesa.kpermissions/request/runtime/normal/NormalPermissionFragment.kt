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

package com.fondesa.kpermissions.request.runtime.normal

import android.app.Fragment
import android.content.Context
import android.os.Bundle
import com.fondesa.kpermissions.request.runtime.PermissionFragmentExecutor
import com.fondesa.kpermissions.request.runtime.RuntimePermissionHandler

/**
 * Created by antoniolig on 05/01/18.
 */
class NormalPermissionFragment : Fragment(), RuntimePermissionHandler, PermissionFragmentExecutor.Callback {

    private val executor by lazy { PermissionFragmentExecutor(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        executor.onCreate()
    }

    override fun onDetach() {
        super.onDetach()
        executor.onDetach()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        executor.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun handleRuntimePermissions(permissions: Array<out String>,
                                          listener: RuntimePermissionHandler.Listener) {
        executor.handleRuntimePermissions(permissions, listener)
    }

    override fun requestRuntimePermissions(permissions: Array<out String>) {
        executor.requestRuntimePermissions(permissions)
    }

    override fun obtainContext(): Context? = activity
}