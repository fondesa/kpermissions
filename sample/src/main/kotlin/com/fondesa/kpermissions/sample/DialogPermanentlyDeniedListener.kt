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
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import com.fondesa.kpermissions.extension.flatString
import com.fondesa.kpermissions.request.PermissionRequest

/**
 * An example of a [PermissionRequest.PermanentlyDeniedListener].
 */
class DialogPermanentlyDeniedListener(private val context: Context) :
    PermissionRequest.PermanentlyDeniedListener {

    override fun onPermissionsPermanentlyDenied(permissions: Array<out String>) {
        val msg = String.format(
            context.getString(R.string.permanently_denied_permissions),
            permissions.flatString()
        )

        AlertDialog.Builder(context)
            .setTitle(R.string.permissions_required)
            .setMessage(msg)
            .setPositiveButton(R.string.action_settings) { _, _ ->
                // Open the app's settings.
                val intent = createAppSettingsIntent()
                context.startActivity(intent)
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    private fun createAppSettingsIntent() = Intent().apply {
        action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        data = Uri.fromParts("package", context.packageName, null)
    }
}