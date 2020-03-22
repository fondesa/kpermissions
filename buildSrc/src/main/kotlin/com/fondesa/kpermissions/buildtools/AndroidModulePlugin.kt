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
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jlleitschuh.gradle.ktlint.KtlintExtension

/**
 * Applies the base configuration to all the Android modules of this project.
 */
class AndroidModulePlugin : Plugin<Project> {

    override fun apply(project: Project) = with(project) {
        plugins.apply("kotlin-android")
        plugins.apply("org.jetbrains.dokka")
        plugins.apply("org.jlleitschuh.gradle.ktlint")

        withAndroidPlugin {
            val androidProperties = readPropertiesOf("android-config.properties")
            compileSdkVersion(androidProperties.getProperty("android.config.compileSdk").toInt())
            defaultConfig.apply {
                minSdkVersion(androidProperties.getProperty("android.config.minSdk").toInt())
                targetSdkVersion(androidProperties.getProperty("android.config.targetSdk").toInt())
            }
            compileOptions {
                it.sourceCompatibility = JavaVersion.VERSION_1_8
                it.targetCompatibility = JavaVersion.VERSION_1_8
            }
            // Used by Robolectric since Android resources can be used in unit tests.
            testOptions.unitTests.isIncludeAndroidResources = true
            // Adds the Kotlin source set for each Java source set.
            sourceSets { sourceSetContainer ->
                sourceSetContainer.all { sourceSet ->
                    sourceSet.java.srcDirs("src/${sourceSet.name}/kotlin")
                }
            }
        }
        tasks.withType(DokkaTask::class.java) {
            it.outputFormat = "html"
            it.skipEmptyPackages = true
        }
        tasks.withType(KotlinCompile::class.java) {
            it.kotlinOptions {
                jvmTarget = "1.8"
                freeCompilerArgs = freeCompilerArgs + "-Xuse-experimental=kotlin.Experimental"
            }
        }
        extensions.configure(KtlintExtension::class.java) {
            it.version.set("0.35.0")
            it.enableExperimentalRules.set(true)
        }
        Unit
    }
}
