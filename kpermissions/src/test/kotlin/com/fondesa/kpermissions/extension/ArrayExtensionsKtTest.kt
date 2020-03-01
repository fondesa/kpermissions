/*
 * Copyright (c) 2018 Fondesa
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

import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Tests for ArrayExtensions.kt file.
 */
@RunWith(RobolectricTestRunner::class)
class ArrayExtensionsKtTest {

    @Test
    fun emptyArrayReturnsEmptyString() {
        val array = arrayOf<String>()

        val resultWithoutSeparator = array.flatString()
        assertEquals("", resultWithoutSeparator)

        val resultWithSeparator = array.flatString(';')
        assertEquals("", resultWithSeparator)
    }

    @Test
    fun arrayWithOneElementReturnsFirst() {
        val first = "first"
        val array = arrayOf(first)

        val resultWithoutSeparator = array.flatString()
        assertEquals(first, resultWithoutSeparator)

        val resultWithSeparator = array.flatString(';')
        assertEquals(first, resultWithSeparator)
    }

    @Test
    fun populatedArrayReturnsFlatString() {
        val first = "first"
        val second = "second"
        val third = "third"
        val array = arrayOf(first, second, third)

        val resultWithoutSeparator = array.flatString()
        assertEquals("$first,$second,$third", resultWithoutSeparator)

        val separator = ';'
        val resultWithSeparator = array.flatString(separator)
        assertEquals("$first$separator$second$separator$third", resultWithSeparator)
    }
}