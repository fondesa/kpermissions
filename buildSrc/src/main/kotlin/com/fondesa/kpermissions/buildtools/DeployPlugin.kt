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
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.plugins.signing.SigningExtension

/**
 * Deploys this library to Maven Central.
 * The public deploy properties are defined in the file "deploy.properties".
 * The private deploy properties aren't versioned.
 * The version which should be deployed is defined through [VersionPlugin].
 */
class DeployPlugin : Plugin<Project> {
    override fun apply(project: Project) = with(project) {
        plugins.apply("com.vanniktech.maven.publish")
        changeAarFileName()
        configureGitHubReleaseExtension()
        registerPublishLibraryTask()
        configureSigningOnCI()
    }

    private val Project.aarFileName: String get() = "$name-$versionName.aar"

    private val Project.versionName: String get() = version as String

    private fun Project.changeAarFileName() {
        withAndroidLibraryLegacyPlugin {
            libraryVariants.all { variant ->
                variant.outputs.all { output ->
                    (output as BaseVariantOutputImpl).outputFileName = aarFileName
                }
            }
        }
    }

    private fun Project.configureGitHubReleaseExtension() {
        rootProject.extensions.configure(GithubReleaseExtension::class.java) { gitHubRelease ->
            gitHubRelease.releaseAssets.from(
                "$buildDir/outputs/aar/$aarFileName",
                "$buildDir/libs/$name-$versionName-javadoc.jar",
                "$buildDir/libs/$name-$versionName-sources.jar"
            )
        }
    }

    private fun Project.registerPublishLibraryTask() {
        tasks.register("publishLibrary").configure { task ->
            task.dependsOn("clean")
            task.dependsOn("publish")
            task.finalizedBy(rootProject.tasks.named("githubRelease"))
        }
        tasks.named("publish") { task ->
            task.mustRunAfter("clean")
        }
    }

    private fun Project.configureSigningOnCI() {
        if (System.getenv("CI") != "true") return
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
