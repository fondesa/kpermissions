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

package com.fondesa.kpermissions

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.support.v4.app.FragmentManager
import android.support.v4.content.ContextCompat

/**
 * Created by antoniolig on 05/01/18.
 */
class PermissionRequestImpl(private val context: Context,
                            private val fragmentManager: FragmentManager,
                            private val permissions: Array<out String>) :
        PermissionRequest {

    override fun send() {
        if (arePermissionGranted()) {

        } else {

        }
    }

    private fun arePermissionGranted(): Boolean {
        permissions.forEach {
            if (ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED)
                return false
        }
        return true
    }

    private fun getPermissionFragment(): PermissionFragment {
        var fragment = fragmentManager.findFragmentByTag(FRAGMENT_TAG) as? PermissionFragment
        if (fragment == null) {
            fragment = PermissionFragment()
            val transaction = fragmentManager.beginTransaction()
                    .add(fragment, FRAGMENT_TAG)

            // Commit the fragment synchronously.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                transaction.commitNowAllowingStateLoss()
            } else {
                transaction.commitAllowingStateLoss()
                fragmentManager.executePendingTransactions()
            }
        }
        return fragment
    }

    companion object {
        private const val FRAGMENT_TAG = "KPermissionsFragment"
    }
}