/*
 * Copyright (c) 2020 Giorgio Antonioli
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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.fondesa.kpermissions.PermissionStatus
import com.fondesa.kpermissions.allGranted
import com.fondesa.kpermissions.anyPermanentlyDenied
import com.fondesa.kpermissions.anyShouldShowRationale
import com.fondesa.kpermissions.extension.addListener
import com.fondesa.kpermissions.extension.permissionsBuilder
import com.fondesa.kpermissions.request.PermissionRequest

/**
 * An simple [Fragment] used to request the permissions.
 */
class DummyFragment : Fragment(), PermissionRequest.Listener {

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
    ): View? = inflater.inflate(R.layout.fragment_view, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        request.addListener(this)
        request.addListener {
            if (it.anyPermanentlyDenied()) {
                Toast.makeText(requireContext(), R.string.additional_listener_msg, Toast.LENGTH_SHORT).show()
            }
        }

        view.findViewById<View>(R.id.btn_test_fragment_permissions).setOnClickListener {
            request.send()
        }
    }

    override fun onPermissionsResult(result: List<PermissionStatus>) {
        val context = requireContext()
        when {
            result.anyPermanentlyDenied() -> context.showPermanentlyDeniedDialog(result)
            result.anyShouldShowRationale() -> context.showRationaleDialog(result, request)
            result.allGranted() -> context.showGrantedToast(result)
        }
    }
}
