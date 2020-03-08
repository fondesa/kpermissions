/*
 * Copyright (c) 2020 Fondesa
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

import com.android.build.gradle.BasePlugin
import com.android.build.gradle.api.AndroidSourceSet
import com.android.builder.model.BuildType
import groovy.lang.Closure
import org.gradle.api.NamedDomainObjectContainer
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
class AndroidCoveragePlugin : Plugin<Project> {
    private val excludedPatterns = setOf(
        "**/BuildConfig.*"
    )

    @ExperimentalStdlibApi
    override fun apply(project: Project) = with(project) {
        plugins.apply("jacoco")

        extensions.configure(JacocoPluginExtension::class.java) {
            it.toolVersion = "0.8.5"
            it.reportsDir = file("$buildDir/jacocoReport")
        }
        plugins.withType(BasePlugin::class.java) { androidPlugin ->
            androidPlugin.extension.apply {
                testOptions.unitTests.all(closureOf {
                    extensions.configure(JacocoTaskExtension::class.java) {
                        it.isIncludeNoLocationClasses = true
                    }
                })
                buildTypes.all { configureCoverageTask(it, sourceSets) }
            }
        }
        Unit
    }

    @ExperimentalStdlibApi
    private fun Project.configureCoverageTask(
        buildType: BuildType,
        sourceSets: NamedDomainObjectContainer<AndroidSourceSet>
    ) {
        val buildTypeName = buildType.name
        val testTaskName = "test${buildTypeName.capitalize(Locale.getDefault())}UnitTest"
        tasks.register("${testTaskName}Coverage", JacocoReport::class.java).configure { task ->
            task.group = "Coverage"
            task.description = "Generate coverage reports on the $buildTypeName build."
            task.dependsOn(testTaskName)
            @Suppress("UnstableApiUsage")
            task.reports.apply {
                html.isEnabled = true
                xml.isEnabled = true
                csv.isEnabled = false
            }
            val javaClassDirectories = fileTreeOf(
                dir = "$buildDir/intermediates/javac/$buildTypeName/classes",
                excludes = excludedPatterns
            )
            val kotlinClassDirectories = fileTreeOf(
                dir = "$buildDir/tmp/kotlin-classes/$buildTypeName",
                excludes = excludedPatterns
            )
            task.classDirectories.from(javaClassDirectories + kotlinClassDirectories)
            task.executionData(files("$buildDir/jacoco/${testTaskName}.exec"))
            sourceSets.named("main").configure { sourceSet ->
                task.sourceDirectories.from(files(sourceSet.java.srcDirs))
            }
        }
    }

    private fun Project.fileTreeOf(dir: String, excludes: Set<String>): FileTree =
        fileTree(mapOf("dir" to dir, "excludes" to excludes))

    private inline fun <T> Project.closureOf(crossinline closure: T.() -> Unit): Closure<T> =
        object : Closure<T>(this) {
            @Suppress("unused") // This function will be invoked dynamically by Groovy.
            fun doCall(obj: T) {
                obj.closure()
            }
        }
}
