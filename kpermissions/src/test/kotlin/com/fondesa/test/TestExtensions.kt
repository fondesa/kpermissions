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

package com.fondesa.test

import android.app.Activity
import android.app.Application
import androidx.fragment.app.Fragment
import org.robolectric.Robolectric
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.util.ReflectionHelpers

/**
 * Get the application's instance returned by Robolectric.
 */
internal val context: Application get() = RuntimeEnvironment.application

/**
 * Create an [Activity] of type [T] using Robolectric.
 */
internal inline fun <reified T : Activity> createActivity(): T =
    Robolectric.buildActivity(T::class.java)
        .create()
        .get()

/**
 * Create a [Fragment] of type [T] using Robolectric.
 */
internal inline fun <reified T : Fragment> createFragment(): T =
    AndroidXFragmentController.of(ReflectionHelpers.callConstructor(T::class.java))
        .create()
        .get()

/**
 * Grants the given permissions.
 */
internal fun Application.grantPermissions(vararg permissions: String) {
    shadowOf(this).grantPermissions(*permissions)
}

/**
 * Grants the given permissions.
 */
internal fun Activity.grantPermissions(vararg permissions: String) {
    shadowOf(this).grantPermissions(*permissions)
}

/**
 * Denies the given permissions.
 */
internal fun Application.denyPermissions(vararg permissions: String) {
    shadowOf(this).denyPermissions(*permissions)
}

/**
 * Denies the given permissions.
 */
internal fun Activity.denyPermissions(vararg permissions: String) {
    shadowOf(this).denyPermissions(*permissions)
}
