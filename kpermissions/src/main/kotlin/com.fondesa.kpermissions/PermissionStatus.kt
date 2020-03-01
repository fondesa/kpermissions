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

package com.fondesa.kpermissions

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * Identifies the status of a permission.
 *
 * @param permission the permission related to this status.
 */
sealed class PermissionStatus(open val permission: String) {

    /**
     * Identifies a granted permission.
     *
     * @param permission the permission related to this status.
     */
    data class Granted(override val permission: String) : PermissionStatus(permission)

    /**
     * Identifies a denied permission.
     *
     * @param permission the permission related to this status.
     */
    sealed class Denied(permission: String) : PermissionStatus(permission) {

        /**
         * Identifies a permanently denied permission.
         * A permission can be permanently denied in the following cases:
         * - below API 23 if it's not present in the manifest
         * - since API 23 if the user checked "never ask again" in the permission dialog.
         *
         * @param permission the permission related to this status.
         */
        data class Permanently(override val permission: String) : Denied(permission)

        /**
         * Identifies a denied permission which could need a rationale explaing why the permission is needed.
         * This status can't be returned below API 23.
         *
         * @param permission the permission related to this status.
         */
        data class ShouldShowRationale(override val permission: String) : Denied(permission)
    }

    /**
     * Identifies a permission which requires a runtime request to establish its status.
     * This status can't be returned below API 23.
     *
     * @param permission the permission related to this status.
     */
    data class RequestRequired(override val permission: String) : PermissionStatus(permission)
}

/**
 * Checks if a status is [PermissionStatus.Granted].
 *
 * @return true if the given status is an instance of [PermissionStatus.Granted].
 */
@UseExperimental(ExperimentalContracts::class)
fun PermissionStatus.isGranted(): Boolean {
    contract {
        returns(true) implies (this@isGranted is PermissionStatus.Granted)
    }
    return this is PermissionStatus.Granted
}

/**
 * Checks if a status is [PermissionStatus.Denied].
 *
 * @return true if the given status is an instance of [PermissionStatus.Denied].
 */
@UseExperimental(ExperimentalContracts::class)
fun PermissionStatus.isDenied(): Boolean {
    contract {
        returns(true) implies (this@isDenied is PermissionStatus.Denied)
    }
    return this is PermissionStatus.Denied
}

/**
 * Checks if a status is [PermissionStatus.Denied.Permanently].
 *
 * @return true if the given status is an instance of [PermissionStatus.Denied.Permanently].
 */
@UseExperimental(ExperimentalContracts::class)
fun PermissionStatus.isPermanentlyDenied(): Boolean {
    contract {
        returns(true) implies (this@isPermanentlyDenied is PermissionStatus.Denied.Permanently)
    }
    return this is PermissionStatus.Denied.Permanently
}

/**
 * Checks if a status is [PermissionStatus.Denied.ShouldShowRationale].
 *
 * @return true if the given status is an instance of [PermissionStatus.Denied.ShouldShowRationale].
 */
@UseExperimental(ExperimentalContracts::class)
fun PermissionStatus.shouldShowRationale(): Boolean {
    contract {
        returns(true) implies (this@shouldShowRationale is PermissionStatus.Denied.ShouldShowRationale)
    }
    return this is PermissionStatus.Denied.ShouldShowRationale
}

/**
 * Checks if a status is [PermissionStatus.RequestRequired].
 *
 * @return true if the given status is an instance of [PermissionStatus.RequestRequired].
 */
@UseExperimental(ExperimentalContracts::class)
fun PermissionStatus.isRequestRequired(): Boolean {
    contract {
        returns(true) implies (this@isRequestRequired is PermissionStatus.RequestRequired)
    }
    return this is PermissionStatus.RequestRequired
}

/**
 * Checks if all the [PermissionStatus] in the given list are instances of [PermissionStatus.Granted].
 * This method is a convenience method even for single permission's requests.
 *
 * @return true if all the given status are instances of [PermissionStatus.Granted].
 */
fun List<PermissionStatus>.allGranted(): Boolean = all { it.isGranted() }

/**
 * Checks if all the [PermissionStatus] in the given list are instances of [PermissionStatus.Denied].
 * This method is a convenience method even for single permission's requests.
 *
 * @return true if all the given status are instances of [PermissionStatus.Denied].
 */
fun List<PermissionStatus>.allDenied(): Boolean = all { it.isDenied() }

/**
 * Checks if all the [PermissionStatus] in the given list are instances of [PermissionStatus.Denied.ShouldShowRationale].
 * This method is a convenience method even for single permission's requests.
 *
 * @return true if all the given status are instances of [PermissionStatus.Denied.ShouldShowRationale].
 */
fun List<PermissionStatus>.allShouldShowRationale(): Boolean =
    all { it.isDenied() && it.shouldShowRationale() }

/**
 * Checks if all the [PermissionStatus] in the given list are instances of [PermissionStatus.Denied.Permanently].
 * This method is a convenience method even for single permission's requests.
 *
 * @return true if all the given status are instances of [PermissionStatus.Denied.Permanently].
 */
fun List<PermissionStatus>.allPermanentlyDenied(): Boolean =
    all { it.isDenied() && it.isPermanentlyDenied() }

/**
 * Checks if all the [PermissionStatus] in the given list are instances of [PermissionStatus.RequestRequired].
 * This method is a convenience method even for single permission's requests.
 *
 * @return true if all the given status are instances of [PermissionStatus.RequestRequired].
 */
fun List<PermissionStatus>.allRequestRequired(): Boolean =
    all { it.isRequestRequired() }

/**
 * Checks if at least one [PermissionStatus] in the given list is an instance of [PermissionStatus.Granted].
 * This method is a convenience method even for single permission's requests.
 *
 * @return true if at least one given status is an instance of [PermissionStatus.Granted].
 */
fun List<PermissionStatus>.anyGranted(): Boolean = any { it.isGranted() }

/**
 * Checks if at least one [PermissionStatus] in the given list is an instance of [PermissionStatus.Denied].
 * This method is a convenience method even for single permission's requests.
 *
 * @return true if at least one given status is an instance of [PermissionStatus.Denied].
 */
fun List<PermissionStatus>.anyDenied(): Boolean = any { it.isDenied() }

/**
 * Checks if at least one [PermissionStatus] in the given list is an instance of [PermissionStatus.Denied.ShouldShowRationale].
 * This method is a convenience method even for single permission's requests.
 *
 * @return true if at least one given status is an instance of [PermissionStatus.Denied.ShouldShowRationale].
 */
fun List<PermissionStatus>.anyShouldShowRationale(): Boolean =
    any { it.isDenied() && it.shouldShowRationale() }

/**
 * Checks if at least one [PermissionStatus] in the given list is an instance of [PermissionStatus.Denied.Permanently].
 * This method is a convenience method even for single permission's requests.
 *
 * @return true if at least one given status is an instance of [PermissionStatus.Denied.Permanently].
 */
fun List<PermissionStatus>.anyPermanentlyDenied(): Boolean =
    any { it.isDenied() && it.isPermanentlyDenied() }

/**
 * Checks if at least one [PermissionStatus] in the given list is an instance of [PermissionStatus.RequestRequired].
 * This method is a convenience method even for single permission's requests.
 *
 * @return true if at least one given status is an instance of [PermissionStatus.RequestRequired].
 */
fun List<PermissionStatus>.anyRequestRequired(): Boolean =
    any { it.isRequestRequired() }
