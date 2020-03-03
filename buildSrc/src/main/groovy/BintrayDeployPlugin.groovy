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


import org.gradle.api.Project

import java.util.regex.Pattern

/**
 * Plugin used to publish the library on Bintray and Maven Central.
 * This plugin will add some sources tasks and the {@code publishLibrary} task to upload the library.
 */
@SuppressWarnings("GroovyUnusedDeclaration")
class BintrayDeployPlugin extends ConfiguredProjectPlugin {

    /**
     * Applies this plugin to a project.
     *
     * @param project the application's project.
     */
    static void attach(Project project) {
        new BintrayDeployPlugin().apply(project)
    }

    private Properties bintrayProps

    @Override
    void onProjectConfigured() {
        // Load Bintray deploy properties file.
        bintrayProps = loadProps("bintray-deploy")

        applyPlugin("bintray-deploy")

        project.group = prop("BINTRAY_COMMON_GROUP_ID")
        project.version = prop(bintrayProps, "BINTRAY_LIB_VERSION")

        // Configure the Bintray publication.
        configureBintray()

        def addTaskToMap = { taskMap, taskName ->
            taskMap.put(taskName, project.tasks.findByName(taskName) != null)
        }

        final def TASK_BINTRAY = "bintrayUpload"
        final def TASK_CLEAN = "clean"
        final def TASK_ASSEMBLE = "assembleRelease"
        final def TASK_SOURCES = "sourcesJar"
        final def TASK_JAVADOC = "javadocJar"
        final def TASK_POM = "generatePomFileForLibraryPublicationPublication"

        def taskMap = new HashMap<String, Boolean>()
        addTaskToMap(taskMap, TASK_BINTRAY)
        addTaskToMap(taskMap, TASK_CLEAN)
        addTaskToMap(taskMap, TASK_ASSEMBLE)
        addTaskToMap(taskMap, TASK_SOURCES)
        addTaskToMap(taskMap, TASK_JAVADOC)
        addTaskToMap(taskMap, TASK_POM)

        project.tasks.whenTaskAdded { task ->
            def taskName = task.name
            def mapTaskInserted = taskMap.get(taskName)
            if (mapTaskInserted != null && !mapTaskInserted) {
                taskMap.put(taskName, true)
                def allInserted = true
                for (Map.Entry<String, Boolean> entry : taskMap.entrySet()) {
                    allInserted = entry.value
                    if (!allInserted)
                        break
                }
                // Add the task only after all tasks are added.
                if (allInserted) {
                    def newTask = project.task("publishLibrary")
                    newTask.group = "publishing"

                    newTask.dependsOn(TASK_CLEAN)
                    newTask.dependsOn(TASK_ASSEMBLE)
                    newTask.dependsOn(TASK_SOURCES)
                    newTask.dependsOn(TASK_JAVADOC)
                    newTask.dependsOn(TASK_POM)

                    project.tasks.findByName(TASK_ASSEMBLE).mustRunAfter TASK_CLEAN
                    project.tasks.findByName(TASK_SOURCES).mustRunAfter TASK_ASSEMBLE
                    project.tasks.findByName(TASK_JAVADOC).mustRunAfter TASK_SOURCES
                    project.tasks.findByName(TASK_POM).mustRunAfter TASK_JAVADOC

                    newTask.finalizedBy TASK_BINTRAY
                }
            }
        }
    }

    /**
     * Closure used to create the Bintray repository's properties.
     */
    Closure configureBintray = {
//        applyPlugin('com.jfrog.bintray')
        project.bintray {
            user = prop("BINTRAY_COMMON_USERNAME")
            key = prop("BINTRAY_COMMON_API_KEY")

            publications = ['libraryPublication']
            pkg {
                repo = prop("BINTRAY_COMMON_REPO")
                name = prop(bintrayProps, "BINTRAY_LIB_NAME")
                desc = prop(bintrayProps, "BINTRAY_LIB_DESCRIPTION")
                websiteUrl = prop(bintrayProps, "BINTRAY_LIB_SITE_URL")
                issueTrackerUrl = prop(bintrayProps, "BINTRAY_LIB_ISSUE_TRACKER_URL")
                vcsUrl = prop(bintrayProps, "BINTRAY_LIB_GIT_URL")
                licenses = [prop("BINTRAY_COMMON_LICENSE_ID")]
                publish = true
                publicDownloadNumbers = true

                def tags = prop(bintrayProps, "BINTRAY_LIB_TAGS")
                labels = tags.split(Pattern.quote('|'))

                githubRepo = prop(bintrayProps, "BINTRAY_LIB_GITHUB_REPO")
                version {
                    desc = prop(bintrayProps, "BINTRAY_LIB_VERSION_DESCRIPTION")
                    released = new Date()
                    gpg {
                        sign = true
                        passphrase = prop("BINTRAY_COMMON_GPG_PASSWORD")
                    }
                }
            }
        }
    }
}