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

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Gradle plugin used to configure a module and to expose some common Project API.
 */
@SuppressWarnings("GroovyUnusedDeclaration")
abstract class ConfiguredProjectPlugin implements Plugin<Project> {
    Project project

    @Override
    void apply(Project project) {
        this.project = project
        project.configure(project) {
            onProjectConfigured()
        }
    }

    /**
     * Called when the project is configured in the {@link #apply(Project)} method.
     */
    abstract void onProjectConfigured()

    /**
     * Load the properties from descending files' tree.
     * This method will search a file with the name passed as parameter in the project dir and in the root dir.
     * The output properties will be the merge of the two files giving a major priority to a property
     * found in the project directory.
     *
     * @param fileName name of the properties file without extension
     * @return instance of {@code Properties}
     */
    Properties loadProps(String fileName) {
        File[] files = new File[2]
        files[0] = new File(project.rootDir.path + File.separator + "${fileName}.properties")
        files[1] = new File(project.projectDir.path + File.separator + "${fileName}.properties")
        return loadProps(files)
    }

    /**
     * Load multiple files in the same {@code Properties} instance.
     *
     * @param files varargs of files to load
     * @return instance of {@code Properties}
     */
    static Properties loadProps(File... files) {
        Properties props = new Properties()
        files.each {
            if (it.exists()) {
                props.load(new FileInputStream(it))
            }
        }
        return props
    }

    /**
     * Get the value of a project property (gradle.properties file).
     *
     * @param propName name of the property
     * @return value of the property in String format or empty String if not found
     */
    String prop(String propName) {
        project.hasProperty(propName) ? project.property(propName) : ""
    }

    /**
     * Get the value of a property.
     *
     * @param properties instance of {@code Properties} after a File, or an XML is loaded into it.
     * @param propName name of the property
     * @return value of the property in String format or empty String if not found
     */
    static String prop(Properties properties, String propName) {
        properties.getProperty(propName, "")
    }

    /**
     * Apply a plugin to the project only if it wasn't applied before.
     *
     * @param pluginName name of the plugin to apply
     */
    void applyPlugin(String pluginName) {
        if (!project.plugins.hasPlugin(pluginName)) {
            project.apply plugin: pluginName
        }
    }
}