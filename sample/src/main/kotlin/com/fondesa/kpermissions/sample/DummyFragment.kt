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

package com.fondesa.kpermissions.sample

import android.Manifest
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.fondesa.kpermissions.PermissionStatus
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.fondesa.kpermissions.isDenied
import com.fondesa.kpermissions.request.PermissionRequest
import com.fondesa.kpermissions.request.runtime.nonce.PermissionNonce
import com.fondesa.kpermissions.shouldShowRationale

/**
 * An simple [Fragment] used to request the permissions.
 */
class DummyFragment :
    Fragment(),
    PermissionRequest.AcceptedListener,
    PermissionRequest.DeniedListener,
    PermissionRequest.PermanentlyDeniedListener,
    PermissionRequest.RationaleListener,
    PermissionRequest.Listener {

    private val request by lazy {
        permissionsBuilder(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.SEND_SMS
        ).build()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = activity ?: throw NullPointerException("The Activity mustn't be null.")

//        request.acceptedListener(this)
//        request.deniedListener(this)
//        request.permanentlyDeniedListener(DialogPermanentlyDeniedListener(activity))
//        request.rationaleListener(this)
        request.addListener(this)

        view.findViewById<View>(R.id.btn_test_fragment_permissions).setOnClickListener {
            request.send()
        }
    }

    override fun onPermissionsResult(result: List<PermissionStatus>) {
//        val msg =
//            result.joinToString(separator = "\n") { "${it.permission} = ${it::class.java.simpleName}" }
//        Toast.makeText(requireActivity(), msg, Toast.LENGTH_SHORT).show()
        if (result.any { it.isDenied() && it.shouldShowRationale() }) {
            val msg = String.format(
                getString(R.string.rationale_permissions),
                "CAO"
//                permissions.joinToString()
            )

            AlertDialog.Builder(context)
                .setTitle(R.string.permissions_required)
                .setMessage(msg)
                .setPositiveButton(R.string.request_again) { _, _ ->
                    // Send the request again.
                    request.send()
                }
                .setNegativeButton(android.R.string.cancel, null)
                .show()
        }
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
        activity?.let {
            val msg = String.format(getString(format), permissions.joinToString())
            Toast.makeText(it, msg, Toast.LENGTH_SHORT).show()
        }
    }
}
