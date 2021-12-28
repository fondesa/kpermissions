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

package com.fondesa.kpermissions.extension

import android.Manifest
import androidx.fragment.app.Fragment
import androidx.fragment.app.testing.launchFragment
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.fondesa.kpermissions.builder.BasePermissionRequestBuilder
import com.fondesa.kpermissions.builder.PermissionRequestBuilder
import com.fondesa.kpermissions.request.PermissionRequest
import com.fondesa.kpermissions.request.runtime.ResultLauncherRuntimePermissionHandlerProvider
import com.fondesa.kpermissions.testing.fakes.FakeFragmentActivity
import com.fondesa.kpermissions.testing.activity
import com.fondesa.kpermissions.testing.fragment
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Tests for PermissionsBuilder.kt file.
 */
@RunWith(AndroidJUnit4::class)
class PermissionsBuilderKtTest {
    @get:Rule
    internal val scenarioRule = ActivityScenarioRule(FakeFragmentActivity::class.java)

    @Test
    fun `When permissionsBuilder() is invoked with an Activity instance, the PermissionRequest is built successfully`() {
        val builder = scenarioRule.activity.permissionsBuilder(
            Manifest.permission.SEND_SMS,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        assertNotNull(builder)
        assertThat(builder, instanceOf(BasePermissionRequestBuilder::class.java))
        builder as BasePermissionRequestBuilder
        assertThat(builder.runtimeHandlerProvider, instanceOf(ResultLauncherRuntimePermissionHandlerProvider::class.java))

        val request = builder.build()
        assertNotNull(request)
        assertThat(request, instanceOf(PermissionRequest::class.java))
    }

    @Test
    fun `When permissionsBuilder() is invoked with a Fragment instance, the PermissionRequest is built successfully`() {
        val scenario = launchFragment<Fragment>()
        val builder = scenario.fragment.permissionsBuilder(
            Manifest.permission.SEND_SMS,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        assertNotNull(builder)
        assertThat(builder, instanceOf(PermissionRequestBuilder::class.java))
        builder as BasePermissionRequestBuilder
        assertThat(builder.runtimeHandlerProvider, instanceOf(ResultLauncherRuntimePermissionHandlerProvider::class.java))

        val request = builder.build()
        assertNotNull(request)
        assertThat(request, instanceOf(PermissionRequest::class.java))
    }
}
