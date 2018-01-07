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
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.fondesa.kpermissions.extensions.flatString
import com.fondesa.kpermissions.extensions.permissionsBuilder
import com.fondesa.kpermissions.request.PermissionRequest
import com.fondesa.kpermissions.request.runtime.nonce.PermissionNonce

/**
 * The main screen of this application that requires some permissions.
 */
class MainActivity : AppCompatActivity(),
        PermissionRequest.AcceptedListener,
        PermissionRequest.PermanentlyDeniedListener,
        PermissionRequest.RationaleListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        log("onCreate")

        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction()
                .add(R.id.first_fragment_container, DummyFragment(), "AAA")
                .add(R.id.second_fragment_container, DummyFragment(), "ZZZ")
                .commit()
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        log("onRestoreInstanceState")
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        log("onSaveInstanceState")
    }

    override fun onStart() {
        super.onStart()
        log("onStart")
    }

    override fun onRestart() {
        super.onRestart()
        log("onRestart")
    }

    override fun onResume() {
        super.onResume()
        log("onResume")
    }

    override fun onStop() {
        super.onStop()
        log("onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        log("onDestroy")
    }

    override fun onPermissionsAccepted(permissions: Array<out String>) {
        log("onPermissionsAccepted: ${permissions.flatString()}")
        Toast.makeText(this, "ACCEPTED", Toast.LENGTH_SHORT).show()
    }

    override fun onPermissionsPermanentlyDenied(permissions: Array<out String>) {
        log("onPermissionsPermanentlyDenied: ${permissions.flatString()}")
    }

    override fun onPermissionsShouldShowRationale(permissions: Array<out String>, nonce: PermissionNonce) {
        log("onPermissionsShouldShowRationale: ${permissions.flatString()}")
    }

    private fun log(s: String) {
        Log.w("LYRA_ACT", s)
    }
}

class DummyFragment : Fragment(),
        PermissionRequest.AcceptedListener,
        PermissionRequest.DeniedListener,
        PermissionRequest.PermanentlyDeniedListener,
        PermissionRequest.RationaleListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        log("onCreate")
    }

    override fun onPermissionsAccepted(permissions: Array<out String>) {
        log("onPermissionsAccepted: ${permissions.flatString()}")
        Toast.makeText(activity!!, "ACCEPTED", Toast.LENGTH_SHORT).show()
    }

    override fun onPermissionsDenied(permissions: Array<out String>) {
        log("onPermissionsDenied: ${permissions.flatString()}")
        Toast.makeText(activity!!, "DENIED", Toast.LENGTH_SHORT).show()
    }

    override fun onPermissionsPermanentlyDenied(permissions: Array<out String>) {
        log("onPermissionsPermanentlyDenied: ${permissions.flatString()}")
    }

    override fun onPermissionsShouldShowRationale(permissions: Array<out String>, nonce: PermissionNonce) {
        log("onPermissionsShouldShowRationale: ${permissions.flatString()}")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        log("onCreateView")
        return inflater.inflate(R.layout.fragment_view, container, false)
    }

    private val request by lazy {
        permissionsBuilder(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.SEND_SMS)
                .build()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        request.acceptedListener(this)
        request.deniedListener(this)
        request.permanentlyDeniedListener(DialogPermanentlyDeniedListener(activity!!))
        request.rationaleListener(DialogRationaleListener(activity!!))

        view.findViewById<View>(R.id.btn_test_permissions).setOnClickListener {
            request.send()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        log("onDestroyView")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        log("onSaveInstanceState")
    }

    override fun onStart() {
        super.onStart()
        log("onStart")
    }

    override fun onResume() {
        super.onResume()
        log("onResume")
    }

    override fun onStop() {
        super.onStop()
        log("onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        log("onDestroy")
    }

    private fun log(s: String) {
        Log.w("LYRA_FRAG|$tag", s)
    }
}