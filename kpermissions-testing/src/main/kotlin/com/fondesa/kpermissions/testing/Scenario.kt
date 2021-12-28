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

package com.fondesa.kpermissions.testing

import android.app.Activity
import androidx.fragment.app.Fragment
import androidx.fragment.app.testing.FragmentScenario
import androidx.test.ext.junit.rules.ActivityScenarioRule

/**
 * Returns the activity of type [T] launched by this scenario.
 */
public val <T : Activity> ActivityScenarioRule<T>.activity: T
    get() {
        lateinit var activity: T
        scenario.onActivity { activity = it }
        return activity
    }

/**
 * Returns the fragment of type [T] launched by this scenario.
 */
public val <T : Fragment> FragmentScenario<T>.fragment: T
    get() {
        lateinit var fragment: T
        onFragment { fragment = it }
        return fragment
    }
