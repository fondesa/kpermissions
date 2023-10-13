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
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

/**
 * Applies the base configuration to all the Android modules of this project.
 */
@Suppress("UnstableApiUsage")
class AndroidModulePlugin : Plugin<Project> {
    override fun apply(project: Project) = with(project) {
        pluginManager.apply("kotlin-android")
        pluginManager.apply("org.jetbrains.dokka")
        pluginManager.apply("org.jlleitschuh.gradle.ktlint")

        val warningsAsErrors = project.properties["kpermissions.warningsAsErrors"].toString().toBooleanStrict()
        val androidProperties = readPropertiesOf("android-config.properties")
        withAndroidPlugin {
            compileSdk = androidProperties.getProperty("android.config.compileSdk").toInt()
            buildToolsVersion = androidProperties.getProperty("android.config.buildTools")
            defaultConfig.minSdk = androidProperties.getProperty("android.config.minSdk").toInt()
            compileOptions {
                sourceCompatibility = JavaVersion.VERSION_11
                targetCompatibility = JavaVersion.VERSION_11
            }
            lint.warningsAsErrors = warningsAsErrors
            testOptions.unitTests.apply {
                // Used by Robolectric since Android resources can be used in unit tests.
                isIncludeAndroidResources = true
                all { test ->
                    test.testLogging.events("passed", "skipped", "failed")
                    test.systemProperty("robolectric.logging.enabled", true)
                    findProperty("robolectric.enabledSdks")?.let { enabledRobolectricSdks ->
                        test.systemProperty("robolectric.enabledSdks", enabledRobolectricSdks)
                    }
                }
            }
        }
        tasks.withType(KotlinCompile::class.java) { task ->
            task.compilerOptions {
                jvmTarget.set(JvmTarget.JVM_11)
                allWarningsAsErrors.set(warningsAsErrors)
                if ("UnitTest" !in task.name) {
                    freeCompilerArgs.add("-Xexplicit-api=strict")
                }
            }
        }
        withAndroidApplicationPlugin {
            defaultConfig.targetSdk = androidProperties.getProperty("android.config.targetSdk").toInt()
        }
    }
}
