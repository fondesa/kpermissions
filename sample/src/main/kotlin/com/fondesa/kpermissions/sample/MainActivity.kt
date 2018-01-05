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

package com.fondesa.kpermissions.sample

import android.Manifest
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.fondesa.kpermissions.extensions.flatString
import com.fondesa.kpermissions.extensions.permissionsBuilder
import com.fondesa.kpermissions.nonce.PermissionNonce
import com.fondesa.kpermissions.request.PermissionRequest

/**
 * The main screen of this application that requires some permissions.
 */
class MainActivity : AppCompatActivity(),
        PermissionRequest.AcceptedListener,
        PermissionRequest.DeniedListener,
        PermissionRequest.RationaleListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        permissionsBuilder(Manifest.permission.ACCESS_FINE_LOCATION)
                .acceptedListener(this)
                .deniedListener(DialogDeniedListener(this))
                .rationaleListener(DialogRationaleListener(this))
                .send()
    }

    override fun onPermissionsAccepted(permissions: Array<out String>) {
        Log.d(TAG, "accepted: ${permissions.flatString()}")
        Toast.makeText(this, "ACCEPTED", Toast.LENGTH_SHORT).show()
    }

    override fun onPermissionsPermanentlyDenied(permissions: Array<out String>) {
        Log.d(TAG, "denied: ${permissions.flatString()}")
    }

    override fun onPermissionsShouldShowRationale(permissions: Array<out String>, nonce: PermissionNonce) {
        Log.d(TAG, "rationale: ${permissions.flatString()}")
    }

    companion object {
        private val TAG = MainActivity::class.java.simpleName
    }
}