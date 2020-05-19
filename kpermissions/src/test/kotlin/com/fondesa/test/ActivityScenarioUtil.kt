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

package com.fondesa.test

import android.app.Activity
import androidx.test.core.app.ActivityScenario

/**
 * Executes the given action on the [Activity] launched by this scenario.
 *
 * @param action the action which should be executed.
 * @param T the type of the [Activity] launched by this scenario.
 * @param R the type of the result which should be returned.
 * @return the action's result.
 */
internal inline fun <T : Activity, R> ActivityScenario<T>.letActivity(crossinline action: (T) -> R): R {
    @Suppress("UNCHECKED_CAST")
    var r: R = null as R
    onActivity { activity ->
        r = action(activity)
    }
    return r
}
