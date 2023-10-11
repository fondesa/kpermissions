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

package com.fondesa.kpermissions.buildtools

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Project
import com.android.build.gradle.LibraryExtension as LegacyLibraryExtension

internal typealias AndroidCommonExtension = CommonExtension<*, *, *, *>

internal typealias AndroidApplicationExtension = ApplicationExtension

internal typealias AndroidLibraryExtension = LibraryExtension

/**
 * Executes the given action when an Android plugin is applied using the new API.
 */
internal inline fun Project.withAndroidPlugin(crossinline config: AndroidCommonExtension.() -> Unit) {
    withAndroidApplicationPlugin(config)
    withAndroidLibraryPlugin(config)
}

/**
 * Executes the given action when an Android application plugin is applied using the new API.
 */
internal inline fun Project.withAndroidApplicationPlugin(crossinline config: AndroidApplicationExtension.() -> Unit) {
    pluginManager.withPlugin("com.android.application") { androidPluginExtension<AndroidApplicationExtension>().config() }
}

/**
 * Executes the given action when an Android library plugin is applied using the new API.
 */
internal inline fun Project.withAndroidLibraryPlugin(crossinline config: AndroidLibraryExtension.() -> Unit) {
    pluginManager.withPlugin("com.android.library") { androidPluginExtension<AndroidLibraryExtension>().config() }
}

/**
 * Executes the given action when an Android library plugin is applied using the old API.
 */
internal inline fun Project.withAndroidLibraryLegacyPlugin(crossinline config: LegacyLibraryExtension.() -> Unit) {
    pluginManager.withPlugin("com.android.library") { extensions.getByType(LegacyLibraryExtension::class.java).config() }
}

private inline fun <reified T : AndroidCommonExtension> Project.androidPluginExtension() = extensions.getByType(T::class.java)
