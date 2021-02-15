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

package com.fondesa.kpermissions.request

import android.app.Activity
import com.fondesa.kpermissions.PermissionStatus

/**
 * Used to check the status of a permission.
 *
 * The result of the check is notified on the listeners attached to the [PermissionRequest].
 * To avoid memory leaks, it's recommended to remove the attached listeners when the
 * instance that holds this [PermissionRequest] is destroyed or to build the [PermissionRequest]
 * in a method always executed during the instance's lifecycle.
 *
 * E.g. considering an [Activity] that wants to check the status of a permission when
 * an action is done by the user, it's recommended to build the request at [Activity.onCreate]
 * and send it when the action is done instead of build and send it at the same moment.
 * This could avoid a potential memory leak when the [Activity] is rotated.
 */
public interface PermissionRequest {

    /**
     * Adds a [Listener] which will be notified when the permissions request sent with [send] ends.
     *
     * @param listener the [Listener] which should be added.
     */
    public fun addListener(listener: Listener)

    /**
     * Removes a [Listener] added with [addListener] which should not be notified anymore.
     *
     * @param listener the [Listener] which should be removed.
     */
    public fun removeListener(listener: Listener)

    /**
     * Removes all the listeners added to this request.
     */
    public fun removeAllListeners()

    /**
     * Checks the status of permissions of this request without sending it.
     * Below API 23, this method and [send] should have the same behavior since the permissions status
     * can be checked without sending a runtime request.
     * Below API 23, the possible status are:
     * - [PermissionStatus.Granted] -> the permission is present in the manifest.
     * - [PermissionStatus.Denied.Permanently] -> the permission is not present in the manifest.
     * Above API 23, this method checks the permissions status without sending a runtime request.
     * Above API 23, the possible status are:
     * - [PermissionStatus.Granted] -> the permission was granted by the user before.
     * - [PermissionStatus.Denied.ShouldShowRationale] -> the permission was denied by the user before.
     * - [PermissionStatus.RequestRequired] -> the permission status can't be retrieved without sending
     * a runtime request. It can be either [PermissionStatus.Denied.Permanently] or a runtime request wasn't
     * ever sent yet.
     *
     * @return the status of each permission.
     */
    public fun checkStatus(): List<PermissionStatus>

    /**
     * Sends the [PermissionRequest] and performs the checks on its status.
     * The result will be returned to the attached listeners.
     */
    public fun send()

    /**
     * Listener notified when a permissions request ends.
     */
    public fun interface Listener {

        /**
         * Notifies when the status of each permission can be established.
         * Below API 23, this method and [checkStatus] have the same behavior since the permissions status
         * can be checked without sending a runtime request.
         * Below API 23, the possible status are:
         * - [PermissionStatus.Granted] -> the permission is present in the manifest.
         * - [PermissionStatus.Denied.Permanently] -> the permission is not present in the manifest.
         * Above API 23, this method checks the permissions status sending a runtime request.
         * Above API 23, the possible status are:
         * - [PermissionStatus.Granted] -> the permission is granted
         * - [PermissionStatus.Denied.ShouldShowRationale] -> the permission is denied by the user and it can be useful to
         * show a rationale explaining the motivation of this permission request
         * - [PermissionStatus.Denied.Permanently] -> the permission is permanently denied by the user using the
         * "never ask again" button on the permissions dialog.
         *
         * @param result the status of each permission.
         */
        public fun onPermissionsResult(result: List<PermissionStatus>)
    }
}
