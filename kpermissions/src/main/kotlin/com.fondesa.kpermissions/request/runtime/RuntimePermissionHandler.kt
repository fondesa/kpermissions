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

package com.fondesa.kpermissions.request.runtime

import com.fondesa.kpermissions.PermissionStatus

/**
 * Used to handle the runtime permissions since Android M.
 * This component must persist across configuration changes because the
 * permission request is partially handled by the OS.
 */
public interface RuntimePermissionHandler {

    /**
     * Attaches an instance of [Listener] that will be notified about
     * changes on the set of [permissions] requested.
     * A [RuntimePermissionHandler] must handle multiple [Listener]s.
     *
     * @param permissions set of permissions to identify the attached [Listener] afterward.
     * @param listener the [Listener] that must be attached.
     */
    public fun attachListener(permissions: Array<out String>, listener: Listener)

    /**
     * Handle a group of permissions notifying the changes on the attached [Listener].
     * This method checks the state of the [permissions] before sending the request.
     *
     * @param permissions set of permissions that must be handled.
     */
    public fun handleRuntimePermissions(permissions: Array<out String>)

    /**
     * Request a group of permissions without checking the [permissions]'s state before
     * sending the request.
     * This shortcut can be useful to request the permissions again after a failure or
     * when the state of the permissions is known.
     *
     * @param permissions set of permissions that must be requested.
     */
    @Suppress("DeprecatedCallableAddReplaceWith")
    @Deprecated("Not invoked anymore by the library.")
    public fun requestRuntimePermissions(permissions: Array<out String>): Unit = Unit

    /**
     * Listener used to notify changed about the permissions' state.
     */
    public interface Listener {

        /**
         * Notified when the permissions request ended and a status for each permission is available.
         *
         * @param result the status of each permission.
         */
        public fun onPermissionsResult(result: List<PermissionStatus>)
    }
}
