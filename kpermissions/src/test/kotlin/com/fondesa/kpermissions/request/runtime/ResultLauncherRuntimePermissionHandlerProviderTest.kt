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

package com.fondesa.kpermissions.request.runtime

import androidx.fragment.app.Fragment
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.fondesa.kpermissions.testing.fakes.FakeFragmentActivity
import com.fondesa.kpermissions.testing.activity
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

/**
 * Tests for [ResultLauncherRuntimePermissionHandlerProvider].
 */
@RunWith(AndroidJUnit4::class)
@Config(minSdk = 23)
class ResultLauncherRuntimePermissionHandlerProviderTest {
    @get:Rule
    internal val scenarioRule = ActivityScenarioRule(FakeFragmentActivity::class.java)
    private lateinit var provider: ResultLauncherRuntimePermissionHandlerProvider

    @Before
    fun createProvider() {
        provider = ResultLauncherRuntimePermissionHandlerProvider(scenarioRule.activity.supportFragmentManager)
    }

    @Test
    fun handlerProvided() {
        // Provide the handler.
        val handler = provider.provideHandler()
        waitForIdleSync()

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
        waitForIdleSync()
        val secondFragment = provider.provideHandler()
        waitForIdleSync()
        // They must point to the same Fragment.
        assertEquals(fragment, secondFragment)
    }

    private fun waitForIdleSync() {
        InstrumentationRegistry.getInstrumentation().waitForIdleSync()
    }
}
