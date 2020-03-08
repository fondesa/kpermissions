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

package com.fondesa.test

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import org.robolectric.Robolectric
import org.robolectric.android.controller.ActivityController
import org.robolectric.android.controller.ComponentController

/**
 * Version of Robolectric's FragmentController which supports AndroidX fragments.
 */
internal class AndroidXFragmentController<F : Fragment> @JvmOverloads constructor(
    private val fragment: F,
    activityClass: Class<out FragmentActivity>,
    intent: Intent? = null
) : ComponentController<AndroidXFragmentController<F>, F>(fragment, intent) {

    private val activityController: ActivityController<out FragmentActivity> =
        Robolectric.buildActivity(activityClass, intent)

    override fun destroy(): AndroidXFragmentController<F> = apply {
        shadowMainLooper.runPaused {
            activityController.destroy()
        }
    }

    override fun create(): AndroidXFragmentController<F> = apply {
        shadowMainLooper.runPaused {
            activityController
                .create()
                .get()
                .supportFragmentManager
                .beginTransaction()
                .add(1, fragment)
                .commitNow()
        }
    }

    private class FragmentControllerActivity : FragmentActivity() {

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            val view = LinearLayout(this)
            view.id = 1
            setContentView(view)
        }
    }

    companion object {

        fun <F : Fragment> of(fragment: F): AndroidXFragmentController<F> =
            AndroidXFragmentController(fragment, FragmentControllerActivity::class.java)

        fun <F : Fragment> of(
            fragment: F,
            activityClass: Class<out FragmentActivity>
        ): AndroidXFragmentController<F> = AndroidXFragmentController(fragment, activityClass)

        fun <F : Fragment> of(
            fragment: F,
            activityClass: Class<out FragmentActivity>,
            intent: Intent
        ): AndroidXFragmentController<F> =
            AndroidXFragmentController(fragment, activityClass, intent)
    }
}
