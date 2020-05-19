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

import groovy.lang.Closure
import org.gradle.api.Project

/**
 * Creates a Groovy closure from the given lambda.
 *
 * @param closure the lambda which should be wrapped in a Groovy closure.
 * @param T the return type of the Groovy closure.
 * @return the Groovy closure which wraps the given lambda.
 */
internal inline fun <T> Project.closureOf(crossinline closure: T.() -> Unit): Closure<T> =
    object : Closure<T>(this) {
        @Suppress("unused") // This function will be invoked dynamically by Groovy.
        fun doCall(obj: T) {
            obj.closure()
        }
    }
