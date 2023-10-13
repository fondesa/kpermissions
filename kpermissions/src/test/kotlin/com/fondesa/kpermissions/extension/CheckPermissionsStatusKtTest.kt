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
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.fondesa.kpermissions.PermissionStatus
import com.fondesa.kpermissions.testing.activity
import com.fondesa.kpermissions.testing.denyPermissions
import com.fondesa.kpermissions.testing.fakes.FakeFragmentActivity
import com.fondesa.kpermissions.testing.grantPermissions
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

/**
 * Tests for CheckPermissionsStatus.kt file.
 */
@RunWith(AndroidJUnit4::class)
class CheckPermissionsStatusKtTest {
    @get:Rule
    internal val scenarioRule = ActivityScenarioRule(FakeFragmentActivity::class.java)
    private val activity get() = scenarioRule.activity

    @Config(maxSdk = 22)
    @Test
    fun `With SDK minor than 23, the status returned with checkPermissionsStatus(vararg) is on the manifest status`() {
        activity.grantPermissions(Manifest.permission.SEND_SMS)
        activity.denyPermissions(Manifest.permission.ACCESS_FINE_LOCATION)

        val result = activity.checkPermissionsStatus(
            Manifest.permission.SEND_SMS,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val expected = listOf(
            PermissionStatus.Granted(Manifest.permission.SEND_SMS),
            PermissionStatus.Denied.Permanently(Manifest.permission.ACCESS_FINE_LOCATION)
        )
        assertEquals(expected, result)
    }

    @Config(maxSdk = 22)
    @Test
    fun `With SDK minor than 23, the status returned with checkPermissionsStatus(list) is on the manifest status`() {
        activity.grantPermissions(Manifest.permission.SEND_SMS)
        activity.denyPermissions(Manifest.permission.ACCESS_FINE_LOCATION)

        val result = activity.checkPermissionsStatus(
            listOf(
                Manifest.permission.SEND_SMS,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        )
        val expected = listOf(
            PermissionStatus.Granted(Manifest.permission.SEND_SMS),
            PermissionStatus.Denied.Permanently(Manifest.permission.ACCESS_FINE_LOCATION)
        )
        assertEquals(expected, result)
    }

    @Config(minSdk = 23)
    @Test
    fun `With SDK since 23, the status returned with checkPermissionsStatus(vararg) is on the runtime status`() {
        activity.grantPermissions(Manifest.permission.SEND_SMS)
        activity.denyPermissions(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CALL_PHONE
        )
        activity.overrideShouldShowRequestPermissionRationale(
            Manifest.permission.ACCESS_FINE_LOCATION to true,
            Manifest.permission.CALL_PHONE to false
        )

        val result = activity.checkPermissionsStatus(
            Manifest.permission.SEND_SMS,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CALL_PHONE
        )
        val expected = listOf(
            PermissionStatus.Granted(Manifest.permission.SEND_SMS),
            PermissionStatus.Denied.ShouldShowRationale(Manifest.permission.ACCESS_FINE_LOCATION),
            PermissionStatus.RequestRequired(Manifest.permission.CALL_PHONE)
        )
        assertEquals(expected, result)
    }

    @Config(minSdk = 23)
    @Test
    fun `With SDK since 23, the status returned with checkPermissionsStatus(list) is on the runtime status`() {
        activity.grantPermissions(Manifest.permission.SEND_SMS)
        activity.denyPermissions(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CALL_PHONE
        )
        activity.overrideShouldShowRequestPermissionRationale(
            Manifest.permission.ACCESS_FINE_LOCATION to true,
            Manifest.permission.CALL_PHONE to false
        )

        val result = activity.checkPermissionsStatus(
            listOf(
                Manifest.permission.SEND_SMS,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.CALL_PHONE
            )
        )
        val expected = listOf(
            PermissionStatus.Granted(Manifest.permission.SEND_SMS),
            PermissionStatus.Denied.ShouldShowRationale(Manifest.permission.ACCESS_FINE_LOCATION),
            PermissionStatus.RequestRequired(Manifest.permission.CALL_PHONE)
        )
        assertEquals(expected, result)
    }
}
