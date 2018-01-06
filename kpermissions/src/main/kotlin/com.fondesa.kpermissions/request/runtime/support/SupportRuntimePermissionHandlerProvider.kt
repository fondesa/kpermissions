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

package com.fondesa.kpermissions.request.runtime.support

import android.os.Build
import android.support.v4.app.FragmentManager
import com.fondesa.kpermissions.request.runtime.RuntimePermissionHandler
import com.fondesa.kpermissions.request.runtime.RuntimePermissionHandlerProvider

/**
 * Created by antoniolig on 06/01/18.
 */
class SupportRuntimePermissionHandlerProvider(private val manager: FragmentManager) :
        RuntimePermissionHandlerProvider {

    override fun provideHandler(): RuntimePermissionHandler {
        var fragment = manager.findFragmentByTag(FRAGMENT_TAG) as? SupportPermissionFragment
        if (fragment == null) {
            fragment = SupportPermissionFragment()
            val transaction = manager.beginTransaction()
                    .add(fragment, FRAGMENT_TAG)

            // Commit the fragment synchronously.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                transaction.commitNowAllowingStateLoss()
            } else {
                transaction.commitAllowingStateLoss()
                manager.executePendingTransactions()
            }
        }
        return fragment
    }

    companion object {
        private const val FRAGMENT_TAG = "KPermissionsFragment-support"
    }
}