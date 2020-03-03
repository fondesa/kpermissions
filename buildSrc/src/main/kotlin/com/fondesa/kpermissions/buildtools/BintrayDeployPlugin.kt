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
import com.android.build.gradle.internal.tasks.factory.dependsOn
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.PublishArtifact
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPom
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.TaskProvider
import org.gradle.jvm.tasks.Jar
import org.jetbrains.dokka.gradle.DokkaTask
import java.util.*

class BintrayDeployPlugin : Plugin<Project> {

    override fun apply(project: Project) = with(project) {
        plugins.apply("maven-publish")
        plugins.apply("com.jfrog.bintray")

        val bintrayDeployProperties = readPropertiesOf("bintray-deploy.properties")
        val sourcesJarTask = registerSourcesJarTask()
        val javadocJarTask = registerJavadocJarTask()
        val sourcesJarArchive = artifacts.add("archives", sourcesJarTask)
        val javadocJarArchive = artifacts.add("archives", javadocJarTask)
        configureMavenPublication(bintrayDeployProperties, javadocJarArchive, sourcesJarArchive)
        Unit
    }

    private fun Project.registerSourcesJarTask(): TaskProvider<out Task> {
        val sourcesJarTask = tasks.register("sourcesJar", Jar::class.java)
        plugins.withType(BasePlugin::class.java) { androidPlugin ->
            androidPlugin.extension.sourceSets.named("main") { sourceSet ->
                sourcesJarTask.configure { task ->
                    task.archiveClassifier.set("sources")
                    task.from(sourceSet.java.srcDirs)
                }
            }
        }
        return sourcesJarTask
    }

    private fun Project.registerJavadocJarTask(): TaskProvider<out Task> {
        val javadocJarTask = tasks.register("javadocJar", Jar::class.java)
        javadocJarTask.dependsOn("dokka")
        tasks.withType(DokkaTask::class.java) { dokkaTask ->
            javadocJarTask.configure { task ->
                task.archiveClassifier.set("javadoc")
                task.from(dokkaTask.outputDirectory)
            }
        }
        return javadocJarTask
    }

    private fun Project.configureMavenPublication(
        bintrayDeployProperties: Properties,
        javadocJarArchive: PublishArtifact,
        sourcesJarArchive: PublishArtifact
    ) {
        extensions.configure(PublishingExtension::class.java) { publishingExtension ->
            publishingExtension.publications { publicationContainer ->
                publicationContainer.create(
                    "libraryPublication",
                    MavenPublication::class.java
                ) { publication ->
                    publication.artifact(javadocJarArchive)
                    publication.artifact(sourcesJarArchive)
                    publication.artifact("$buildDir/outputs/aar/$name-release.aar")
                    publication.groupId = getProperty("BINTRAY_COMMON_GROUP_ID")
                    publication.artifactId =
                        bintrayDeployProperties.getProperty("BINTRAY_LIB_ARTIFACT_ID")
                    publication.version = bintrayDeployProperties.getProperty("BINTRAY_LIB_VERSION")
                    publication.pom { pom -> configurePom(pom, bintrayDeployProperties) }
                }
            }
        }
    }

    @Suppress("UnstableApiUsage")
    private fun Project.configurePom(pom: MavenPom, bintrayDeployProperties: Properties) {
        pom.name.set(bintrayDeployProperties.getProperty("BINTRAY_LIB_NAME"))
        pom.description.set(bintrayDeployProperties.getProperty("BINTRAY_LIB_DESCRIPTION"))
        pom.url.set(bintrayDeployProperties.getProperty("BINTRAY_LIB_URL"))
        pom.licenses { licenseSpec ->
            licenseSpec.license { license ->
                license.url.set(getProperty("BINTRAY_COMMON_LICENSE_URL"))
            }
        }
        pom.developers { developerSpec ->
            developerSpec.developer { developer ->
                developer.id.set(getProperty("BINTRAY_COMMON_DEV_ID"))
                developer.name.set(getProperty("BINTRAY_COMMON_DEV_NAME"))
                developer.email.set(getProperty("BINTRAY_COMMON_DEV_MAIL"))
            }
        }
        pom.scm { scmSpec ->
            scmSpec.connection.set(getProperty("BINTRAY_LIB_GIT_URL"))
            scmSpec.developerConnection.set(getProperty("BINTRAY_LIB_GIT_URL"))
            scmSpec.url.set(getProperty("BINTRAY_LIB_SITE_URL"))
        }
        configurePomDependencies(pom)
    }

    private fun Project.configurePomDependencies(pom: MavenPom) {
        pom.withXml { xmlProvider ->
            val dependenciesNode = xmlProvider.asNode().appendNode("dependencies")
            val exportedConfigurationsNames = setOf("compile", "implementation", "api")
            val addedDependencies = mutableMapOf<String, Dependency>()
            configurations.configureEach { configuration ->
                if (configuration.name !in exportedConfigurationsNames) {
                    return@configureEach
                }
                val configDependencies = configuration.allDependencies
                    .filter { dependency -> dependency.group != null && dependency.version != null }
                    .filter { dependency -> addedDependencies[dependency.name] == null }
                    .map { dependency -> dependency.name to dependency }
                    .toMap()

                val localDependencies = configDependencies
                    .filter { (_, dependency) -> dependency.version == "unspecified" }

                if (localDependencies.isNotEmpty()) {
                    throw GradleException("Can't publish artifacts depending on local dependencies: ${localDependencies.keys.joinToString()}")
                }
                configDependencies.values.forEach { dependency ->
                    val dependencyNode = dependenciesNode.appendNode("dependency")
                    dependencyNode.appendNode("groupId", dependency.group)
                    dependencyNode.appendNode("artifactId", dependency.name)
                    dependencyNode.appendNode("version", dependency.version)
                }
                addedDependencies += configDependencies
            }
        }
    }

    private fun Project.getProperty(propertyName: String): String =
        if (project.hasProperty(propertyName)) project.property(propertyName) as String else ""
}