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

import android.app.AlertDialog
import android.content.Context
import com.fondesa.kpermissions.request.PermissionRequest
import com.fondesa.kpermissions.request.runtime.nonce.PermissionNonce

/**
 * An example of a [PermissionRequest.RationaleListener].
 */
class DialogRationaleListener(private val context: Context) : PermissionRequest.RationaleListener {

    override fun onPermissionsShouldShowRationale(
        permissions: Array<out String>,
        nonce: PermissionNonce
    ) {
        val msg = String.format(
            context.getString(R.string.rationale_permissions),
            permissions.joinToString()
        )

        AlertDialog.Builder(context)
            .setTitle(R.string.permissions_required)
            .setMessage(msg)
            .setPositiveButton(R.string.request_again) { _, _ ->
                // Send the request again.
                nonce.use()
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }
}
