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

package com.fondesa.kpermissions.extension

/**
 * Creates a [String] using this array's elements.
 * The elements will be appended to each other separated by the [separator].
 *
 * @param separator character used to separate the elements of the array.
 * By default the [separator] is a comma.
 * @return result [String] with the elements of the array or an empty [String]
 * if the array was empty.
 */
@Deprecated(
    "This method is no longer needed since the Kotlin stdlib contains it.",
    ReplaceWith("joinToString(separator = separator.toString())")
)
public fun Array<out String>.flatString(separator: Char = ','): String =
    joinToString(separator = separator.toString())
