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

import com.android.build.gradle.internal.api.BaseVariantOutputImpl
import com.github.breadmoirai.githubreleaseplugin.GithubReleaseExtension
import com.vanniktech.maven.publish.MavenPublishBaseExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.TaskProvider
import org.gradle.jvm.tasks.Jar
import org.gradle.plugins.signing.SigningExtension

/**
 * Deploys this library to Maven Central.
 * The public deploy properties are defined in the file "gradle.properties".
 * The private deploy properties aren't versioned.
 * The version which should be deployed is defined through [VersionPlugin].
 * To deploy a version manually, you should provide the following properties:
 * - VERSION_NAME -> the version of the library which should be deployed (e.g. 3.1.5)
 * - signingKeyId (or the env variable ORG_GRADLE_PROJECT_signingKeyId) -> the last 8 chars of your secret GPG key
 * - signingPassword (or the env variable ORG_GRADLE_PROJECT_signingPassword) -> the password of your GPG key
 * - signingKey (or the env variable ORG_GRADLE_PROJECT_signingKey) -> the private GPG key
 */
class DeployPlugin : Plugin<Project> {
    override fun apply(project: Project) = with(project) {
        plugins.apply("com.vanniktech.maven.publish")
        changeOutputsFileNames()
        configureGitHubReleaseExtension()
        registerPublishLibraryTask()
        configureMavenPublish()
        configureSigning()
    }

    private val Project.versionName: String get() = version as String

    private val Project.archiveName: String get() = "$name-$versionName"

    private fun Project.changeOutputsFileNames() {
        val copySourceReleaseJar = registerCopyJarTask(
            sourceTaskName = "sourceReleaseJar",
            outputName = "$archiveName-sources.jar"
        )
        val copyJavaDocReleaseJar = registerCopyJarTask(
            sourceTaskName = "javaDocReleaseJar",
            outputName = "$archiveName-javadoc.jar"
        )
        tasks.withType(Jar::class.java) { task ->
            val copyTask = when (task.name) {
                "sourceReleaseJar" -> copySourceReleaseJar
                "javaDocReleaseJar" -> copyJavaDocReleaseJar
                else -> null
            }
            copyTask?.from(task)
        }

        withAndroidLibraryLegacyPlugin {
            libraryVariants.all { variant ->
                variant.outputs.all { output ->
                    (output as BaseVariantOutputImpl).outputFileName = "$archiveName.aar"
                }
            }
        }
    }

    private fun Project.registerCopyJarTask(
        sourceTaskName: String,
        outputName: String
    ): TaskProvider<Copy> {
        val copyTaskName = "copy${sourceTaskName.replaceFirstChar { it.uppercase() }}"
        return tasks.register(copyTaskName, Copy::class.java) { copyTask ->
            copyTask.into("$buildDir/libs")
            copyTask.rename { outputName }
            copyTask.dependsOn(sourceTaskName)
        }
    }

    private fun TaskProvider<Copy>.from(task: Task) {
        configure { copyTask -> copyTask.from(task) }
    }

    private fun Project.configureGitHubReleaseExtension() {
        rootProject.extensions.configure(GithubReleaseExtension::class.java) { gitHubRelease ->
            gitHubRelease.releaseAssets.from(
                "$buildDir/outputs/aar/$archiveName.aar",
                "$buildDir/libs/$archiveName-javadoc.jar",
                "$buildDir/libs/$archiveName-sources.jar"
            )
        }
    }

    private fun Project.registerPublishLibraryTask() {
        val githubReleaseTask = rootProject.tasks.named("githubRelease") { task ->
            task.mustRunAfter(tasks.named("copySourceReleaseJar"), tasks.named("copyJavaDocReleaseJar"))
        }
        tasks.register("publishLibrary").configure { task ->
            task.dependsOn("clean")
            task.dependsOn("publish")
            task.finalizedBy("copySourceReleaseJar", "copyJavaDocReleaseJar", githubReleaseTask)
        }
        tasks.named("publish") { task ->
            task.mustRunAfter("clean")
        }
    }

    @Suppress("UnstableApiUsage")
    private fun Project.configureMavenPublish() {
        extensions.configure(MavenPublishBaseExtension::class.java) { mavenPublish ->
            mavenPublish.publishToMavenCentral()
            mavenPublish.signAllPublications()
        }
    }

    private fun Project.configureSigning() {
        extensions.configure(SigningExtension::class.java) { signing ->
            // Populated from the environment variable ORG_GRADLE_PROJECT_signingKeyId.
            val signingKeyId = findProperty("signingKeyId") ?: return@configure
            // Populated from the environment variable ORG_GRADLE_PROJECT_signingKey.
            val signingKey = findProperty("signingKey") ?: return@configure
            // Populated from the environment variable ORG_GRADLE_PROJECT_signingPassword.
            val signingPassword = findProperty("signingPassword") ?: return@configure
            @Suppress("UnstableApiUsage")
            signing.useInMemoryPgpKeys(signingKeyId.toString(), signingKey.toString(), signingPassword.toString())
        }
    }
}
