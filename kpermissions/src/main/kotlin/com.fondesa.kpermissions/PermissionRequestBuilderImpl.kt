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

package com.fondesa.kpermissions

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager

/**
 * Created by antoniolig on 05/01/18.
 */
class PermissionRequestBuilderImpl : PermissionRequestBuilder {

    private val context: Context
    private val fragmentManager: FragmentManager
    private var permissions: Array<out String>? = null

    constructor(fragment: Fragment) : this(fragment.activity?.applicationContext
            ?: throw NullPointerException("The activity mustn't be null."), fragment.childFragmentManager)

    constructor(activity: FragmentActivity) : this(activity.applicationContext, activity.supportFragmentManager)

    private constructor(context: Context, fragmentManager: FragmentManager) {
        this.context = context
        this.fragmentManager = fragmentManager
    }

    override fun permissions(vararg permissions: String): PermissionRequestBuilder = apply {
        this.permissions = permissions
    }

    override fun build(): PermissionRequest {
        val permissions = permissions
        if (permissions == null || permissions.isEmpty()) {
            // Throw an exception if there isn't any permission specified.
            throw IllegalArgumentException("You have to specify at least one permission.")
        }

        return PermissionRequestImpl(context, fragmentManager, permissions)
    }

    override fun send(): PermissionRequest {
        // Build the request.
        val request = build()
        // Send it directly.
        request.send()
        return request
    }
}