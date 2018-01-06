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

import android.app.AlertDialog
import android.content.Context
import com.fondesa.kpermissions.request.runtime.nonce.PermissionNonce
import com.fondesa.kpermissions.request.PermissionRequest

/**
 * Created by antoniolig on 05/01/18.
 */
class DialogRationaleListener(private val context: Context) : PermissionRequest.RationaleListener {

    override fun onPermissionsShouldShowRationale(permissions: Array<out String>, nonce: PermissionNonce) {
        AlertDialog.Builder(context)
                .setTitle("Rational title")
                .setMessage("Rational message")
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    // Send the request again.
                    nonce.use()
                }
                .setNegativeButton(android.R.string.cancel, null)
                .show()
    }
}