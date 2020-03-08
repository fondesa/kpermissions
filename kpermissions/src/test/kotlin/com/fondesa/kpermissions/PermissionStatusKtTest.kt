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

package com.fondesa.kpermissions

import android.Manifest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Tests for PermissionStatus.kt file.
 */
class PermissionStatusKtTest {

    @Test
    fun `The extension isGranted() returns true if the permission is granted`() {
        assertTrue(PermissionStatus.Granted(Manifest.permission.SEND_SMS).isGranted())
    }

    @Test
    fun `The extension isGranted() returns false if the permission is not granted`() {
        assertFalse(PermissionStatus.Denied.Permanently(Manifest.permission.SEND_SMS).isGranted())
    }

    @Test
    fun `The extension isDenied() returns true if the permission is permanently denied`() {
        assertTrue(PermissionStatus.Denied.Permanently(Manifest.permission.SEND_SMS).isDenied())
    }

    @Test
    fun `The extension isDenied() returns true if the permission should show rationale`() {
        assertTrue(PermissionStatus.Denied.ShouldShowRationale(Manifest.permission.SEND_SMS).isDenied())
    }

    @Test
    fun `The extension isDenied() returns false if the permission is not permanently denied and should not show rationale`() {
        assertFalse(PermissionStatus.Granted(Manifest.permission.SEND_SMS).isDenied())
    }

    @Test
    fun `The extension isPermanentlyDenied() returns true if the permission is permanently denied`() {
        assertTrue(PermissionStatus.Denied.Permanently(Manifest.permission.SEND_SMS).isPermanentlyDenied())
    }

    @Test
    fun `The extension isPermanentlyDenied() returns false if the permission is not permanently denied`() {
        assertFalse(PermissionStatus.Denied.ShouldShowRationale(Manifest.permission.SEND_SMS).isPermanentlyDenied())
    }

    @Test
    fun `The extension shouldShowRationale() returns true if the permission should show rationale`() {
        assertTrue(PermissionStatus.Denied.ShouldShowRationale(Manifest.permission.SEND_SMS).shouldShowRationale())
    }

    @Test
    fun `The extension shouldShowRationale() returns false if the permission should not show rationale`() {
        assertFalse(PermissionStatus.Denied.Permanently(Manifest.permission.SEND_SMS).shouldShowRationale())
    }

    @Test
    fun `The extension isRequestRequired() returns true if the permission needs a runtime request`() {
        assertTrue(PermissionStatus.RequestRequired(Manifest.permission.SEND_SMS).isRequestRequired())
    }

    @Test
    fun `The extension isRequestRequired() returns false if the permission does not need a runtime request`() {
        assertFalse(PermissionStatus.Granted(Manifest.permission.SEND_SMS).isRequestRequired())
    }

    @Test
    fun `The extension allGranted() returns true if all the permissions are granted`() {
        assertTrue(
            listOf(
                PermissionStatus.Granted(Manifest.permission.SEND_SMS),
                PermissionStatus.Granted(Manifest.permission.CALL_PHONE)
            ).allGranted()
        )
    }

    @Test
    fun `The extension allGranted() returns false if not all the permissions are granted`() {
        assertFalse(
            listOf(
                PermissionStatus.Granted(Manifest.permission.SEND_SMS),
                PermissionStatus.Denied.Permanently(Manifest.permission.CALL_PHONE)
            ).allGranted()
        )
    }

    @Test
    fun `The extension allDenied() returns true if all the permissions are denied`() {
        assertTrue(
            listOf(
                PermissionStatus.Denied.Permanently(Manifest.permission.SEND_SMS),
                PermissionStatus.Denied.ShouldShowRationale(Manifest.permission.CALL_PHONE)
            ).allDenied()
        )
    }

    @Test
    fun `The extension allDenied() returns false if not all the permissions are denied`() {
        assertFalse(
            listOf(
                PermissionStatus.Denied.Permanently(Manifest.permission.SEND_SMS),
                PermissionStatus.Granted(Manifest.permission.CALL_PHONE)
            ).allDenied()
        )
    }

    @Test
    fun `The extension allPermanentlyDenied() returns true if all the permissions are permanently denied`() {
        assertTrue(
            listOf(
                PermissionStatus.Denied.Permanently(Manifest.permission.SEND_SMS),
                PermissionStatus.Denied.Permanently(Manifest.permission.CALL_PHONE)
            ).allPermanentlyDenied()
        )
    }

    @Test
    fun `The extension allPermanentlyDenied() returns false if not all the permissions are permanently denied`() {
        assertFalse(
            listOf(
                PermissionStatus.Denied.Permanently(Manifest.permission.SEND_SMS),
                PermissionStatus.Denied.ShouldShowRationale(Manifest.permission.CALL_PHONE)
            ).allPermanentlyDenied()
        )
        assertFalse(
            listOf(
                PermissionStatus.Denied.Permanently(Manifest.permission.SEND_SMS),
                PermissionStatus.Granted(Manifest.permission.CALL_PHONE)
            ).allPermanentlyDenied()
        )
    }

    @Test
    fun `The extension allShouldShowRationale() returns true if all the permissions should show rationale`() {
        assertTrue(
            listOf(
                PermissionStatus.Denied.ShouldShowRationale(Manifest.permission.SEND_SMS),
                PermissionStatus.Denied.ShouldShowRationale(Manifest.permission.CALL_PHONE)
            ).allShouldShowRationale()
        )
    }

    @Test
    fun `The extension allShouldShowRationale() returns false if not all the permissions should show rationale`() {
        assertFalse(
            listOf(
                PermissionStatus.Denied.Permanently(Manifest.permission.SEND_SMS),
                PermissionStatus.Denied.ShouldShowRationale(Manifest.permission.CALL_PHONE)
            ).allShouldShowRationale()
        )
        assertFalse(
            listOf(
                PermissionStatus.Granted(Manifest.permission.SEND_SMS),
                PermissionStatus.Denied.ShouldShowRationale(Manifest.permission.CALL_PHONE)
            ).allShouldShowRationale()
        )
    }

    @Test
    fun `The extension allRequestRequired() returns true if all the permissions need a runtime request`() {
        assertTrue(
            listOf(
                PermissionStatus.RequestRequired(Manifest.permission.SEND_SMS),
                PermissionStatus.RequestRequired(Manifest.permission.CALL_PHONE)
            ).allRequestRequired()
        )
    }

    @Test
    fun `The extension allRequestRequired() returns false if not all the permissions need a runtime request`() {
        assertFalse(
            listOf(
                PermissionStatus.Denied.Permanently(Manifest.permission.SEND_SMS),
                PermissionStatus.RequestRequired(Manifest.permission.CALL_PHONE)
            ).allRequestRequired()
        )
    }

    @Test
    fun `The extension anyGranted() returns true if some permissions are granted`() {
        assertTrue(
            listOf(
                PermissionStatus.Granted(Manifest.permission.SEND_SMS),
                PermissionStatus.RequestRequired(Manifest.permission.CALL_PHONE)
            ).anyGranted()
        )
    }

    @Test
    fun `The extension anyGranted() returns false if no permissions are granted`() {
        assertFalse(
            listOf(
                PermissionStatus.RequestRequired(Manifest.permission.SEND_SMS),
                PermissionStatus.Denied.Permanently(Manifest.permission.CALL_PHONE)
            ).anyGranted()
        )
    }

    @Test
    fun `The extension anyDenied() returns true if some permissions are denied`() {
        assertTrue(
            listOf(
                PermissionStatus.Denied.Permanently(Manifest.permission.SEND_SMS),
                PermissionStatus.RequestRequired(Manifest.permission.CALL_PHONE)
            ).anyDenied()
        )
    }

    @Test
    fun `The extension anyDenied() returns false if no permissions are denied`() {
        assertFalse(
            listOf(
                PermissionStatus.RequestRequired(Manifest.permission.SEND_SMS),
                PermissionStatus.Granted(Manifest.permission.CALL_PHONE)
            ).anyDenied()
        )
    }

    @Test
    fun `The extension anyPermanentlyDenied() returns true if some permissions are permanently denied`() {
        assertTrue(
            listOf(
                PermissionStatus.Denied.Permanently(Manifest.permission.SEND_SMS),
                PermissionStatus.Denied.ShouldShowRationale(Manifest.permission.CALL_PHONE)
            ).anyPermanentlyDenied()
        )
    }

    @Test
    fun `The extension anyPermanentlyDenied() returns false if no permissions are permanently denied`() {
        assertFalse(
            listOf(
                PermissionStatus.Denied.ShouldShowRationale(Manifest.permission.SEND_SMS),
                PermissionStatus.Granted(Manifest.permission.CALL_PHONE)
            ).anyPermanentlyDenied()
        )
    }

    @Test
    fun `The extension anyShouldShowRationale() returns true if some permissions should show rationale`() {
        assertTrue(
            listOf(
                PermissionStatus.Denied.Permanently(Manifest.permission.SEND_SMS),
                PermissionStatus.Denied.ShouldShowRationale(Manifest.permission.CALL_PHONE)
            ).anyShouldShowRationale()
        )
    }

    @Test
    fun `The extension anyShouldShowRationale() returns false if no permissions should show rationale`() {
        assertFalse(
            listOf(
                PermissionStatus.Denied.Permanently(Manifest.permission.SEND_SMS),
                PermissionStatus.Granted(Manifest.permission.CALL_PHONE)
            ).anyShouldShowRationale()
        )
    }

    @Test
    fun `The extension anyRequestRequired() returns true if some permissions need a runtime request`() {
        assertTrue(
            listOf(
                PermissionStatus.Denied.Permanently(Manifest.permission.SEND_SMS),
                PermissionStatus.RequestRequired(Manifest.permission.CALL_PHONE)
            ).anyRequestRequired()
        )
    }

    @Test
    fun `The extension anyRequestRequired() returns false if no permissions need a runtime request`() {
        assertFalse(
            listOf(
                PermissionStatus.Denied.Permanently(Manifest.permission.SEND_SMS),
                PermissionStatus.Granted(Manifest.permission.CALL_PHONE)
            ).anyRequestRequired()
        )
    }
}