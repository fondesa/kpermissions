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
import com.fondesa.kpermissions.testing.fakes.FakePermissionRequest
import com.fondesa.kpermissions.testing.fakes.FakeRuntimePermissionHandlerProvider
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Tests for [BasePermissionRequestBuilder].
 */
@RunWith(AndroidJUnit4::class)
class BasePermissionRequestBuilderTest {
    private val builder: BasePermissionRequestBuilder = BasePermissionRequestBuilderImpl()
    private val provider = FakeRuntimePermissionHandlerProvider()

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
    fun verifyBuildInstanceWithVararg() {
        // Build the request.
        val request = builder.permissions("example")
            .runtimeHandlerProvider(provider)
            .build()
        assertNotNull(request)
        assertThat(request, instanceOf(PermissionRequest::class.java))
    }

    @Test
    fun verifyBuildInstanceWithList() {
        // Build the request.
        val request = builder.permissions(listOf("example"))
            .runtimeHandlerProvider(provider)
            .build()
        assertNotNull(request)
        assertThat(request, instanceOf(PermissionRequest::class.java))
    }

    private class BasePermissionRequestBuilderImpl : BasePermissionRequestBuilder() {
        override fun createRequest(
            permissions: Array<out String>,
            runtimeHandlerProvider: RuntimePermissionHandlerProvider
        ) = FakePermissionRequest()
    }
}
