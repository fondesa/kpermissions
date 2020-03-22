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

import org.gradle.api.Project
import java.io.File
import java.util.*

internal fun Project.readPropertiesOf(fileName: String): Properties {
    val rootPropsFile = rootProject.file(fileName)
    val propsFile = file(fileName)
    return readPropertiesFiles(rootPropsFile, propsFile)
}

private fun readPropertiesFiles(vararg files: File): Properties = Properties().apply {
    files.filter { file -> file.exists() }.forEach { file -> load(file.inputStream()) }
}
