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

package com.fondesa.kpermissions.request.runtime

import com.fondesa.kpermissions.PermissionStatus

/**
 * Used to handle the runtime permissions since Android M.
 * This component must persist across configuration changes because the
 * permission request is partially handled by the OS.
 */
interface RuntimePermissionHandler {

    /**
     * Attaches an instance of [Listener] that will be notified about
     * changes on the set of [permissions] requested.
     * A [RuntimePermissionHandler] must handle multiple [Listener]s.
     *
     * @param permissions set of permissions to identify the attached [Listener] afterward.
     * @param listener the [Listener] that must be attached.
     */
    fun attachListener(permissions: Array<out String>, listener: Listener)

    /**
     * Handle a group of permissions notifying the changes on the attached [Listener].
     * This method checks the state of the [permissions] before sending the request.
     *
     * @param permissions set of permissions that must be handled.
     */
    fun handleRuntimePermissions(permissions: Array<out String>)

    /**
     * Request a group of permissions without checking the [permissions]'s state before
     * sending the request.
     * This shortcut can be useful to request the permissions again after a failure or
     * when the state of the permissions is known.
     *
     * @param permissions set of permissions that must be requested.
     */
    fun requestRuntimePermissions(permissions: Array<out String>)

    /**
     * Listener used to notify changed about the permissions' state.
     */
    interface Listener {

        /**
         * Notified when the permissions request ended and a status for each permission is available.
         *
         * @param result the status of each permission.
         */
        fun onPermissionsResult(result: List<PermissionStatus>)

        /**
         * Notified when some permissions are accepted.
         *
         * @param permissions set of permissions that are accepted.
         * @return true if the listener handled this permissions' state, false otherwise.
         */
        @Deprecated("Use the Listener.onPermissionsResult(List<PermissionStatus>) API instead.")
        fun permissionsAccepted(permissions: Array<out String>): Boolean

        /**
         * Notified when some permissions are denied.
         *
         * @param permissions set of permissions that are denied.
         * @return true if the listener handled this permissions' state, false otherwise.
         */
        @Deprecated("Use the Listener.onPermissionsResult(List<PermissionStatus>) API instead.")
        fun permissionsDenied(permissions: Array<out String>): Boolean

        /**
         * Notified when some permissions are permanently denied.
         *
         * @param permissions set of permissions that are permanently denied.
         * @return true if the listener handled this permissions' state, false otherwise.
         */
        @Deprecated("Use the Listener.onPermissionsResult(List<PermissionStatus>) API instead.")
        fun permissionsPermanentlyDenied(permissions: Array<out String>): Boolean

        /**
         * Notified when some permissions need a rationale.
         *
         * @param permissions set of permissions that need a rationale.
         * @return true if the listener handled this permissions' state, false otherwise.
         */
        @Deprecated("Use the Listener.onPermissionsResult(List<PermissionStatus>) API instead.")
        fun permissionsShouldShowRationale(permissions: Array<out String>): Boolean
    }
}
