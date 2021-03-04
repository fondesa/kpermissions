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

import com.android.build.api.component.ComponentIdentity
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.FileTree
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension
import org.gradle.testing.jacoco.plugins.JacocoTaskExtension
import org.gradle.testing.jacoco.tasks.JacocoReport
import java.util.*

/**
 * Enables the unit tests coverage in an Android project.
 */
@Suppress("UnstableApiUsage")
class AndroidCoveragePlugin : Plugin<Project> {
    override fun apply(project: Project) = with(project) {
        plugins.apply("jacoco")

        extensions.configure(JacocoPluginExtension::class.java) {
            it.toolVersion = JACOCO_VERSION
            it.reportsDirectory.set(file("$buildDir/coverageReport"))
        }
        withAndroidPlugin {
            fixRobolectricCoverage()
            onVariants { configureCoverageTasks(this) }
        }
    }

    private fun Project.configureCoverageTasks(variant: ComponentIdentity) {
        val testTaskName = "test${variant.name.capitalize(Locale.getDefault())}UnitTest"
        val coverageTaskName = "${testTaskName}Coverage"
        tasks.register(coverageTaskName, JacocoReport::class.java).configure { coverageTask ->
            coverageTask.group = COVERAGE_TASKS_GROUP
            coverageTask.description = "Calculates the coverage and generates the reports for the variant \"${variant.name}\"."
            coverageTask.reports.apply {
                html.isEnabled = true
                xml.isEnabled = true
                csv.isEnabled = false
            }
            val javaClassDirectories = fileTreeOf("$buildDir/intermediates/javac/${variant.name}/classes")
            val kotlinClassDirectories = fileTreeOf("$buildDir/tmp/kotlin-classes/${variant.name}")
            coverageTask.classDirectories.from(javaClassDirectories, kotlinClassDirectories)
            coverageTask.executionData.from("$buildDir/jacoco/$testTaskName.exec")
            variant.sourceSets(this).forEach { sourceSet ->
                coverageTask.sourceDirectories.from(sourceSet)
            }
        }
        tasks.matching { task -> task.name == testTaskName }.configureEach { task ->
            task.finalizedBy(coverageTaskName)
        }
    }

    private fun AndroidCommonExtension.fixRobolectricCoverage() {
        testOptions.unitTests.all { test ->
            test.extensions.configure(JacocoTaskExtension::class.java) {
                // It must be set to true otherwise the coverage of Robolectric tests is not calculated.
                it.isIncludeNoLocationClasses = true
            }
        }
    }

    private fun Project.fileTreeOf(dir: String): FileTree = fileTree(mapOf("dir" to dir, "excludes" to COVERAGE_EXCLUSIONS))

    companion object {
        private const val JACOCO_VERSION = "0.8.6"
        private const val COVERAGE_TASKS_GROUP = "Coverage"
    }
}
