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

package com.fondesa.kpermissions.builder

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.fondesa.kpermissions.request.PermissionRequest
import com.fondesa.kpermissions.request.runtime.RuntimePermissionHandlerProvider
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.mock

/**
 * Tests for [BasePermissionRequestBuilder].
 */
@RunWith(AndroidJUnit4::class)
class BasePermissionRequestBuilderTest {
    private val builder: BasePermissionRequestBuilder = MockBuilder()
    private val provider = mock<RuntimePermissionHandlerProvider> {
        on(it.provideHandler()).thenReturn(mock())
    }

    @Test(expected = IllegalArgumentException::class)
    fun throwsExceptionWithoutPermissions() {
        builder.build()
    }

    @Test(expected = IllegalArgumentException::class)
    fun throwsExceptionWithoutRuntimeHandler() {
        builder.permissions("example")
            .build()
    }

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
        val request = builder.permissions("example")
            .runtimeHandlerProvider(provider)
            .build()
        assertNotNull(request)
        assertThat(request, instanceOf(PermissionRequest::class.java))
    }

    class MockBuilder : BasePermissionRequestBuilder() {
        override fun createRequest(
            permissions: Array<out String>,
            runtimeHandlerProvider: RuntimePermissionHandlerProvider
        ) = mock<PermissionRequest>()
    }
}
