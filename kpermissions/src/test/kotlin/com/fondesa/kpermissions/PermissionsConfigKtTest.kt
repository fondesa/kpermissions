@file:Suppress("DEPRECATION")

package com.fondesa.kpermissions

import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Tests of PermissionsConfig.kt file.
 */
class PermissionsConfigKtTest {
    @After
    fun reset() {
        shouldUseLegacyRuntimePermissionHandler = false
    }

    @Test
    fun `By default, shouldUseLegacyRuntimePermissionHandler is false`() {
        assertFalse(shouldUseLegacyRuntimePermissionHandler)
    }

    @Test
    fun `When useLegacyRuntimePermissionHandler, shouldUseLegacyRuntimePermissionHandler is true`() {
        useLegacyRuntimePermissionHandler()

        assertTrue(shouldUseLegacyRuntimePermissionHandler)
    }
}
