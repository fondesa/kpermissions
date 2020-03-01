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

/**
 * Plugin used to have a common configuration between Android/Kotlin modules.
 * This plugin use the constants defined in {@code android-config.properties} file.
 */
@SuppressWarnings("GroovyUnusedDeclaration")
class KAndroidPlugin extends ConfiguredProjectPlugin {

    private final static def APP = 1
    private final static def LIB = 2

    /**
     * Applies this plugin to an Android application's project.
     *
     * @param project the application's project.
     */
    static void attachApplication(Project project) {
        attachType(project, APP)
    }

    /**
     * Applies this plugin to an Android library's project.
     *
     * @param project the library's project.
     */
    static void attachLibrary(Project project) {
        attachType(project, LIB)
    }

    private static void attachType(Project project, int type) {
        new KAndroidPlugin(type: type).apply(project)
    }

    private int type

    @Override
    void onProjectConfigured() {
        // Load Android properties.
        def androidProps = loadProps("android-config")

        if (type == APP) {
            applyPlugin('com.android.application')
        } else if (type == LIB) {
            applyPlugin('com.android.library')
        }

        // Apply the Kotlin plugins.
        applyPlugin('kotlin-android')
        applyPlugin('kotlin-android-extensions')
        applyPlugin('org.jetbrains.dokka')

        // Add Android extension.
        project.android {
            compileSdkVersion prop(androidProps, "COMPILE_SDK").toInteger()

            defaultConfig {
                minSdkVersion prop(androidProps, "MIN_SDK").toInteger()
                targetSdkVersion prop(androidProps, "TARGET_SDK").toInteger()
            }

            sourceSets {
                androidTest.java.srcDirs += "src/androidTest/kotlin"
                main.java.srcDirs += "src/main/kotlin"
                test.java.srcDirs += "src/test/kotlin"
            }
        }

        // Add the Dokka extension.
        project.dokka {
            outputFormat = "html"
            skipEmptyPackages = true
        }
    }
}