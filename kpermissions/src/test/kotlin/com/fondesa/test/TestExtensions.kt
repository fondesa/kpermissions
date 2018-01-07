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

package com.fondesa.test

import android.app.Activity
import android.app.Application
import android.app.Fragment
import org.robolectric.Robolectric
import org.robolectric.RuntimeEnvironment
import org.robolectric.shadows.support.v4.SupportFragmentController

/**
 * Get the application's instance returned by Robolectric.
 */
val context: Application get() = RuntimeEnvironment.application

/**
 * Create an [Activity] of type [T] using Robolectric.
 */
inline fun <reified T : Activity> createActivity(): T = Robolectric.buildActivity(T::class.java)
        .create()
        .get()

/**
 * Create a [Fragment] of type [T] using Robolectric.
 */
inline fun <reified T : Fragment> createFragment(): T = Robolectric.buildFragment(T::class.java)
        .create()
        .get()

/**
 * Create a [android.support.v4.app.Fragment] of type [T] using Robolectric.
 */
inline fun <reified T : android.support.v4.app.Fragment> createSupportFragment(): T {
    // Create the Fragment instance using the reflection.
    val fragment = T::class.java.newInstance()
            ?: throw NullPointerException("It was impossible to create an instance of the class " +
                    T::class.java.name + " using the reflection.")

    return SupportFragmentController.of(fragment)
            .create()
            .get()
}