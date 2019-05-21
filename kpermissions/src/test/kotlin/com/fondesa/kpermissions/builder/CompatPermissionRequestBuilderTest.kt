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

package com.fondesa.kpermissions.builder

import com.fondesa.kpermissions.request.PermissionRequest
import com.fondesa.kpermissions.request.runtime.RuntimePermissionHandlerProvider
import com.fondesa.kpermissions.request.runtime.nonce.PermissionNonceGenerator
import com.fondesa.test.context
import com.nhaarman.mockitokotlin2.mock
import junit.framework.Assert.assertNotNull
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Tests for [CompatPermissionRequestBuilderTest].
 */
@RunWith(RobolectricTestRunner::class)
class CompatPermissionRequestBuilderTest {

    private val builder: CompatPermissionRequestBuilder = CompatPermissionRequestBuilder(context)

    private val provider = mock<RuntimePermissionHandlerProvider> {
        on(it.provideHandler()).thenReturn(mock())
    }
    private val nonceGenerator = mock<PermissionNonceGenerator>()

    @Test
    fun verifyMinimumBuildInstance() {
        // Build the request.
        val request = builder.permissions("example")
                .runtimeHandlerProvider(provider)
                .build()
        assertNotNull(request)
        assertThat(request, instanceOf(PermissionRequest::class.java))
    }

    @Test
    fun verifyFullBuildInstance() {
        // Build the request.
        val request = builder.permissions("example", "example_2")
                .runtimeHandlerProvider(provider)
                .nonceGenerator(nonceGenerator)
                .build()
        assertNotNull(request)
        assertThat(request, instanceOf(PermissionRequest::class.java))
    }
}