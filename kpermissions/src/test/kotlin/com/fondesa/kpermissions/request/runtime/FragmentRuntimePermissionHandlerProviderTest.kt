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

package com.fondesa.kpermissions.request.runtime

import android.os.Build
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.fondesa.test.createActivity
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Tests for [FragmentRuntimePermissionHandlerProvider].
 */
@RunWith(RobolectricTestRunner::class)
@Config(minSdk = Build.VERSION_CODES.M)
class FragmentRuntimePermissionHandlerProviderTest {
    private val activity = createActivity<FragmentActivity>()
    private val provider = FragmentRuntimePermissionHandlerProvider(activity.supportFragmentManager)

    @Test
    fun handlerProvided() {
        // Provide the handler.
        val handler = provider.provideHandler()

        assertNotNull(handler)
        assertThat(handler, instanceOf(RuntimePermissionHandler::class.java))
        // The handler must be a Fragment.
        assertThat(handler, instanceOf(Fragment::class.java))

        val fragment = handler as Fragment
        // The Fragment must be attached and not visible.
        assertTrue(fragment.isAdded)
        assertFalse(fragment.isVisible)
    }

    @Test
    fun sameFragmentProvidedMultipleTimes() {
        // Provide the handler.
        val fragment = provider.provideHandler()
        val secondFragment = provider.provideHandler()
        // They must point to the same Fragment.
        assertEquals(fragment, secondFragment)
    }
}