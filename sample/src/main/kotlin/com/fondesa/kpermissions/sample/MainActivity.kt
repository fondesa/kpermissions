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
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import com.fondesa.kpermissions.extension.flatString
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.fondesa.kpermissions.request.PermissionRequest
import com.fondesa.kpermissions.request.runtime.nonce.PermissionNonce

/**
 * The main screen of this application that requires some permissions.
 */
class MainActivity : AppCompatActivity(),
    PermissionRequest.AcceptedListener,
    PermissionRequest.DeniedListener,
    PermissionRequest.PermanentlyDeniedListener,
    PermissionRequest.RationaleListener {

    private val request by lazy {
        permissionsBuilder(Manifest.permission.CAMERA)
            .build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        request.acceptedListener(this)
        request.deniedListener(this)
        request.permanentlyDeniedListener(DialogPermanentlyDeniedListener(this))
        request.rationaleListener(DialogRationaleListener(this))

        findViewById<View>(R.id.btn_test_activity_permissions).setOnClickListener {
            request.send()
        }

        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container, DummyFragment())
            .commit()
    }

    override fun onPermissionsAccepted(permissions: Array<out String>) {
        toastOf(R.string.accepted_permissions, permissions)
    }

    override fun onPermissionsDenied(permissions: Array<out String>) {
        toastOf(R.string.denied_permissions, permissions)
    }

    override fun onPermissionsPermanentlyDenied(permissions: Array<out String>) {
        toastOf(R.string.permanently_denied_permissions, permissions)
    }

    override fun onPermissionsShouldShowRationale(
        permissions: Array<out String>,
        nonce: PermissionNonce
    ) {
        toastOf(R.string.rationale_permissions, permissions)
    }

    private fun toastOf(@StringRes format: Int, permissions: Array<out String>) {
        val msg = String.format(getString(format), permissions.flatString())
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}

