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

import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Applies the base configuration to all the Android modules of this project.
 */
@Suppress("UnstableApiUsage")
class AndroidModulePlugin : Plugin<Project> {
    override fun apply(project: Project) = with(project) {
        plugins.apply("kotlin-android")
        plugins.apply("org.jetbrains.dokka")

        val androidProperties = readPropertiesOf("android-config.properties")
        withAndroidPlugin {
            compileSdk = androidProperties.getProperty("android.config.compileSdk").toInt()
            buildToolsVersion = androidProperties.getProperty("android.config.buildTools")
            defaultConfig.minSdk = androidProperties.getProperty("android.config.minSdk").toInt()
            compileOptions {
                sourceCompatibility = JavaVersion.VERSION_1_8
                targetCompatibility = JavaVersion.VERSION_1_8
            }
            lint.isWarningsAsErrors = true
            // Fix of the following bug: https://issuetracker.google.com/issues/206855622
            lint.ignore("ObsoleteLintCustomCheck")
            testOptions.unitTests.apply {
                // Used by Robolectric since Android resources can be used in unit tests.
                isIncludeAndroidResources = true
                all { test ->
                    test.testLogging.events("passed", "skipped", "failed")
                    test.systemProperty("robolectric.logging.enabled", true)
                }
            }
            // Adds the Kotlin source set for each Java source set.
            sourceSets.all { sourceSet ->
                sourceSet.java.srcDirs("src/${sourceSet.name}/kotlin")
            }
        }
        withAndroidApplicationPlugin {
            defaultConfig.targetSdk = androidProperties.getProperty("android.config.targetSdk").toInt()
        }
    }
}
