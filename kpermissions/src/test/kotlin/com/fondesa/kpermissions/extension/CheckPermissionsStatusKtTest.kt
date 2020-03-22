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
import android.app.Activity
import com.fondesa.kpermissions.PermissionStatus
import com.fondesa.test.createActivity
import com.fondesa.test.denyPermissions
import com.fondesa.test.grantPermissions
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Tests for CheckPermissionsStatus.kt file.
 */
@RunWith(RobolectricTestRunner::class)
class CheckPermissionsStatusKtTest {
    private val activity = spy(createActivity<Activity>())

    @Config(maxSdk = 22)
    @Test
    fun `With SDK minor than 23, the status returned with checkPermissionsStatus() is on the manifest status`() {
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

    @Config(minSdk = 23)
    @Test
    fun `With SDK since 23, the status returned with checkPermissionsStatus() is on the runtime status`() {
        activity.grantPermissions(Manifest.permission.SEND_SMS)
        activity.denyPermissions(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CALL_PHONE
        )
        whenever(activity.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION))
            .thenReturn(true)
        whenever(activity.shouldShowRequestPermissionRationale(Manifest.permission.CALL_PHONE))
            .thenReturn(false)

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
}
