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

package com.fondesa.kpermissions.request.runtime.nonce

import android.Manifest
import com.fondesa.kpermissions.request.runtime.RuntimePermissionHandler
import com.nhaarman.mockitokotlin2.mock
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertNotNull
import org.junit.Test

/**
 * Tests for [RationalePermissionNonceGenerator].
 */
class RationalePermissionNonceGeneratorTest {

    private val generator = RationalePermissionNonceGenerator()

    @Test
    fun generateNonce() {
        val handler = mock<RuntimePermissionHandler>()
        val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.SEND_SMS)

        // Provide the nonce.
        val nonce = generator.generateNonce(handler, permissions)

        assertNotNull(nonce)
        assertThat(nonce, instanceOf(PermissionNonce::class.java))
    }
}