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

import com.android.build.gradle.BaseExtension
import com.android.build.gradle.BasePlugin
import org.gradle.api.Project

/**
 * Applies the given [block] to the Android's extension.
 *
 * @param block the Android configuration which should be applied.
 */
internal inline fun Project.withAndroidPlugin(crossinline block: BaseExtension.() -> Unit) {
    plugins.withType(BasePlugin::class.java) {
        androidExtension.apply(block)
    }
}

private val Project.androidExtension: BaseExtension get() = extensions.getByName("android") as BaseExtension
