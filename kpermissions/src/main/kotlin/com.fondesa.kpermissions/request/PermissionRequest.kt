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

package com.fondesa.kpermissions.request

import android.app.Activity
import com.fondesa.kpermissions.PermissionStatus
import com.fondesa.kpermissions.request.runtime.nonce.PermissionNonce

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
interface PermissionRequest {

    /**
     * Adds a [Listener] which will be notified when the permissions request sent with [send] ends.
     *
     * @param listener the [Listener] which should be added.
     */
    fun addListener(listener: Listener)

    /**
     * Removes a [Listener] added with [addListener] which should not be notified anymore.
     *
     * @param listener the [Listener] which should be removed.
     */
    fun removeListener(listener: Listener)

    /**
     * Removes all the listeners added to this request.
     */
    fun removeAllListeners()

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
    fun checkStatus(): List<PermissionStatus>

    /**
     * Sends the [PermissionRequest] and performs the checks on its status.
     * The result will be returned to the attached listeners.
     */
    fun send()

    /**
     * Attaches an instance of [AcceptedListener] that will be notified when the permissions
     * requested with this [PermissionRequest] are accepted.
     *
     * @param listener [AcceptedListener] that must be attached.
     */
    @Deprecated("Use the method PermissionRequest.addListener(PermissionRequest.Listener) instead.")
    fun acceptedListener(listener: AcceptedListener)

    /**
     * Attaches an instance of [DeniedListener] that will be notified when the permissions
     * requested with this [PermissionRequest] are denied.
     *
     * @param listener [DeniedListener] that must be attached.
     */
    @Deprecated("Use the method PermissionRequest.addListener(PermissionRequest.Listener) instead.")
    fun deniedListener(listener: DeniedListener)

    /**
     * Attaches an instance of [PermanentlyDeniedListener] that will be notified when the permissions
     * requested with this [PermissionRequest] are permanently denied.
     *
     * @param listener [PermanentlyDeniedListener] that must be attached.
     */
    @Deprecated("Use the method PermissionRequest.addListener(PermissionRequest.Listener) instead.")
    fun permanentlyDeniedListener(listener: PermanentlyDeniedListener)

    /**
     * Attaches an instance of [RationaleListener] that will be notified when the permissions
     * requested with this [PermissionRequest] needs a rationale.
     *
     * @param listener [RationaleListener] that must be attached.
     */
    @Deprecated("Use the method PermissionRequest.addListener(PermissionRequest.Listener) instead.")
    fun rationaleListener(listener: RationaleListener)

    /**
     * Detaches the current attached instance of [AcceptedListener], if any.
     */
    @Deprecated("Use the method PermissionRequest.removeListener(PermissionRequest.Listener) instead.")
    fun detachAcceptedListener()

    /**
     * Detaches the current attached instance of [DeniedListener], if any.
     */
    @Deprecated("Use the method PermissionRequest.removeListener(PermissionRequest.Listener) instead.")
    fun detachDeniedListener()

    /**
     * Detaches the current attached instance of [PermanentlyDeniedListener], if any.
     */
    @Deprecated("Use the method PermissionRequest.removeListener(PermissionRequest.Listener) instead.")
    fun detachPermanentlyDeniedListener()

    /**
     * Detaches the current attached instance of [RationaleListener], if any.
     */
    @Deprecated("Use the method PermissionRequest.removeListener(PermissionRequest.Listener) instead.")
    fun detachRationaleListener()

    /**
     * Detaches all the current attached listeners.
     */
    @Deprecated("Use the method PermissionRequest.removeAllListeners() instead.")
    fun detachAllListeners()

    /**
     * Listener notified when a permissions request ends.
     */
    interface Listener {

        /**
         * Notifies when the status of each permission can be established.
         * Below API 23, this method and [checkStatus] should have the same behavior since the permissions status
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
        fun onPermissionsResult(result: List<PermissionStatus>)
    }

    /**
     * Listener used to receive information about the accepted status of some permissions.
     */
    @Deprecated("Use the listener PermissionRequest.Listener instead.")
    interface AcceptedListener {

        /**
         * Notified when some permissions are accepted.
         *
         * @param permissions set of accepted permissions.
         */
        @Deprecated("Use the method [PermissionRequest.Listener.onPermissionsResult(List<PermissionStatus>) instead.")
        fun onPermissionsAccepted(permissions: Array<out String>)
    }

    /**
     * Listener used to receive information about the denied status of some permissions.
     */
    @Deprecated("Use the listener PermissionRequest.Listener instead.")
    interface DeniedListener {

        /**
         * Notified when some permissions are denied.
         *
         * @param permissions set of denied permissions.
         */
        @Deprecated("Use the method [PermissionRequest.Listener.onPermissionsResult(List<PermissionStatus>) instead.")
        fun onPermissionsDenied(permissions: Array<out String>)
    }

    /**
     * Listener used to receive information about the permanently denied status of some permissions.
     */
    @Deprecated("Use the listener PermissionRequest.Listener instead.")
    interface PermanentlyDeniedListener {

        /**
         * Notified when some permissions are permanently denied.
         * The status of this permissions can't be changed anymore by the application and
         * the user must change it manually.
         *
         * @param permissions set of permanently denied permissions.
         */
        @Deprecated("Use the method [PermissionRequest.Listener.onPermissionsResult(List<PermissionStatus>) instead.")
        fun onPermissionsPermanentlyDenied(permissions: Array<out String>)
    }

    /**
     * Listener used to receive information about the rationale of some permissions.
     */
    @Deprecated("Use the listener PermissionRequest.Listener instead.")
    interface RationaleListener {

        /**
         * Notified when some permissions needs a rationale that must be displayed to the user.
         * This method permits also to use a [PermissionNonce] to request the same
         * set of permissions again.
         *
         * @param permissions set of permissions that needs a rationale.
         * @param nonce instance of [PermissionNonce] that can be used one time.
         */
        @Deprecated("Use the method [PermissionRequest.Listener.onPermissionsResult(List<PermissionStatus>) instead.")
        fun onPermissionsShouldShowRationale(permissions: Array<out String>, nonce: PermissionNonce)
    }
}