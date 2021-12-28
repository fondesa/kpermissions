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

package com.fondesa.kpermissions.testing.fakes

import androidx.fragment.app.FragmentActivity

/**
 * Fake implementation of [FragmentActivity] used in tests.
 */
public class FakeFragmentActivity : FragmentActivity() {
    private val shouldShowRequestPermissionRationaleResults = mutableMapOf<String, Boolean>()

    public fun overrideShouldShowRequestPermissionRationale(vararg results: Pair<String, Boolean>) {
        shouldShowRequestPermissionRationaleResults.clear()
        shouldShowRequestPermissionRationaleResults += results
    }

    override fun shouldShowRequestPermissionRationale(permission: String): Boolean {
        return shouldShowRequestPermissionRationaleResults.getOrElse(permission) {
            super.shouldShowRequestPermissionRationale(permission)
        }
    }
}
