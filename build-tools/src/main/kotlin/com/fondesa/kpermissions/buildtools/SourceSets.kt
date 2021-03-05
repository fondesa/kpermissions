/*
 * Copyright (c) 2021 Giorgio Antonioli
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
import org.gradle.api.Project
import java.io.File

/**
 * Gets the source sets from the given variant.
 * The source sets are returned as files.
 */
@Suppress("UnstableApiUsage")
internal fun ComponentIdentity.sourceSets(project: Project): Collection<File> {
    val buildType = requireNotNull(buildType) { "The build type is required." }
    val sourceSets = mutableSetOf("main", buildType)
    if (flavorName.isNotBlank()) {
        sourceSets += productFlavors.map { it.second }
        sourceSets += flavorName
        sourceSets += name
    }
    return sourceSets.map { project.file("src/$it/java") } +
        sourceSets.map { project.file("src/$it/kotlin") }
}
