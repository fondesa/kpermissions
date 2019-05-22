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

package com.fondesa.kpermissions.extension

import android.Manifest
import androidx.fragment.app.Fragment
import com.fondesa.kpermissions.builder.PermissionRequestBuilder
import com.fondesa.kpermissions.request.PermissionRequest
import com.fondesa.test.createFragment
import junit.framework.Assert.assertNotNull
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Tests for FragmentExtensions.kt extensions.
 */
@RunWith(RobolectricTestRunner::class)
class FragmentExtensionsTest {

    @Test(expected = IllegalArgumentException::class)
    fun fragPermissionBuilderWithZeroPermissions() {
        val fragment = createFragment<Fragment>()
        val builder = fragment.permissionsBuilder()

        assertNotNull(builder)
        assertThat(builder, instanceOf(PermissionRequestBuilder::class.java))

        // This must throw IllegalArgumentException.
        builder.build()
    }

    @Test
    fun fragPermissionBuilderWithSomePermissions() {
        val fragment = createFragment<Fragment>()
        val builder = fragment.permissionsBuilder(
            Manifest.permission.SEND_SMS,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        assertNotNull(builder)
        assertThat(builder, instanceOf(PermissionRequestBuilder::class.java))

        val request = builder.build()
        assertNotNull(request)
        assertThat(request, instanceOf(PermissionRequest::class.java))
    }
}