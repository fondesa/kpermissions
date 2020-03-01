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

    fun addListener(listener: Listener)

    fun removeListener(listener: Listener)

    fun removeAllListeners()

    fun checkCurrentStatus(): List<PermissionStatus>

    /**
     * Sends the [PermissionRequest] and performs the checks on its status.
     * The result will be returned to the correct attached listener, if possible.
     */
    fun send()

    /**
     * Attaches an instance of [AcceptedListener] that will be notified when the permissions
     * requested with this [PermissionRequest] are accepted.
     *
     * @param listener [AcceptedListener] that must be attached.
     */
    @Deprecated("LYRA_DEPRECATED")
    fun acceptedListener(listener: AcceptedListener)

    /**
     * Attaches an instance of [DeniedListener] that will be notified when the permissions
     * requested with this [PermissionRequest] are denied.
     *
     * @param listener [DeniedListener] that must be attached.
     */
    @Deprecated("LYRA_DEPRECATED")
    fun deniedListener(listener: DeniedListener)

    /**
     * Attaches an instance of [PermanentlyDeniedListener] that will be notified when the permissions
     * requested with this [PermissionRequest] are permanently denied.
     *
     * @param listener [PermanentlyDeniedListener] that must be attached.
     */
    @Deprecated("LYRA_DEPRECATED")
    fun permanentlyDeniedListener(listener: PermanentlyDeniedListener)

    /**
     * Attaches an instance of [RationaleListener] that will be notified when the permissions
     * requested with this [PermissionRequest] needs a rationale.
     *
     * @param listener [RationaleListener] that must be attached.
     */
    @Deprecated("LYRA_DEPRECATED")
    fun rationaleListener(listener: RationaleListener)

    /**
     * Detaches the current attached instance of [AcceptedListener], if any.
     */
    @Deprecated("LYRA_DEPRECATED")
    fun detachAcceptedListener()

    /**
     * Detaches the current attached instance of [DeniedListener], if any.
     */
    @Deprecated("LYRA_DEPRECATED")
    fun detachDeniedListener()

    /**
     * Detaches the current attached instance of [PermanentlyDeniedListener], if any.
     */
    @Deprecated("LYRA_DEPRECATED")
    fun detachPermanentlyDeniedListener()

    /**
     * Detaches the current attached instance of [RationaleListener], if any.
     */
    @Deprecated("LYRA_DEPRECATED")
    fun detachRationaleListener()

    /**
     * Detaches all the current attached listeners.
     */
    @Deprecated("LYRA_DEPRECATED")
    fun detachAllListeners()

    /**
     * TODO: LYRA_DOC
     */
    interface Listener {

        /**
         * TODO: LYRA_DOC
         */
        fun onPermissionsResult(result: List<PermissionStatus>)
    }

    /**
     * Listener used to receive information about the accepted status of some permissions.
     */
    @Deprecated("LYRA_DEPRECATED")
    interface AcceptedListener {

        /**
         * Notified when some permissions are accepted.
         *
         * @param permissions set of accepted permissions.
         */
        @Deprecated("LYRA_DEPRECATED")
        fun onPermissionsAccepted(permissions: Array<out String>)
    }

    /**
     * Listener used to receive information about the denied status of some permissions.
     */
    @Deprecated("LYRA_DEPRECATED")
    interface DeniedListener {

        /**
         * Notified when some permissions are denied.
         *
         * @param permissions set of denied permissions.
         */
        @Deprecated("LYRA_DEPRECATED")
        fun onPermissionsDenied(permissions: Array<out String>)
    }

    /**
     * Listener used to receive information about the permanently denied status of some permissions.
     */
    @Deprecated("LYRA_DEPRECATED")
    interface PermanentlyDeniedListener {

        /**
         * Notified when some permissions are permanently denied.
         * The status of this permissions can't be changed anymore by the application and
         * the user must change it manually.
         *
         * @param permissions set of permanently denied permissions.
         */
        @Deprecated("LYRA_DEPRECATED")
        fun onPermissionsPermanentlyDenied(permissions: Array<out String>)
    }

    /**
     * Listener used to receive information about the rationale of some permissions.
     */
    @Deprecated("LYRA_DEPRECATED")
    interface RationaleListener {

        /**
         * Notified when some permissions needs a rationale that must be displayed to the user.
         * This method permits also to use a [PermissionNonce] to request the same
         * set of permissions again.
         *
         * @param permissions set of permissions that needs a rationale.
         * @param nonce instance of [PermissionNonce] that can be used one time.
         */
        @Deprecated("LYRA_DEPRECATED")
        fun onPermissionsShouldShowRationale(permissions: Array<out String>, nonce: PermissionNonce)
    }
}