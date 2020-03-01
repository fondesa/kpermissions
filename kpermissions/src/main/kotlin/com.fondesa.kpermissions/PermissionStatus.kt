/*
 * Copyright (c) 2019 Fondesa
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

sealed class PermissionStatus(open val permission: String) {

    data class Granted(override val permission: String) : PermissionStatus(permission)

    sealed class Denied(permission: String) : PermissionStatus(permission) {

        data class Permanently(override val permission: String) : Denied(permission)

        data class ShouldShowRationale(override val permission: String) : Denied(permission)
    }

    data class RequestRequired(override val permission: String) : PermissionStatus(permission)
}

@UseExperimental(ExperimentalContracts::class)
fun PermissionStatus.isDenied(): Boolean {
    contract {
        returns(true) implies (this@isDenied is PermissionStatus.Denied)
    }
    return this is PermissionStatus.Denied
}

@UseExperimental(ExperimentalContracts::class)
fun PermissionStatus.isPermanentlyDenied(): Boolean {
    contract {
        returns(true) implies (this@isPermanentlyDenied is PermissionStatus.Denied.Permanently)
    }
    return this is PermissionStatus.Denied.Permanently
}

@UseExperimental(ExperimentalContracts::class)
fun PermissionStatus.shouldShowRationale(): Boolean {
    contract {
        returns(true) implies (this@shouldShowRationale is PermissionStatus.Denied.ShouldShowRationale)
    }
    return this is PermissionStatus.Denied.ShouldShowRationale
}

@UseExperimental(ExperimentalContracts::class)
fun PermissionStatus.isRequestRequired(): Boolean {
    contract {
        returns(true) implies (this@isRequestRequired is PermissionStatus.RequestRequired)
    }
    return this is PermissionStatus.RequestRequired
}


fun List<PermissionStatus>.allGranted(): Boolean = all { it.isGranted() }

fun List<PermissionStatus>.allDenied(): Boolean = all { it.isDenied() }

fun List<PermissionStatus>.allShouldShowRationale(): Boolean =
    all { it.isDenied() && it.shouldShowRationale() }

fun List<PermissionStatus>.allPermanentlyDenied(): Boolean =
    all { it.isDenied() && it.isPermanentlyDenied() }

fun List<PermissionStatus>.allRequestRequired(): Boolean =
    all { it.isRequestRequired() }

fun List<PermissionStatus>.anyGranted(): Boolean = any { it.isGranted() }

fun List<PermissionStatus>.anyDenied(): Boolean = any { it.isDenied() }

fun List<PermissionStatus>.anyShouldShowRationale(): Boolean =
    any { it.isDenied() && it.shouldShowRationale() }

fun List<PermissionStatus>.anyPermanentlyDenied(): Boolean =
    any { it.isDenied() && it.isPermanentlyDenied() }

fun List<PermissionStatus>.anyRequestRequired(): Boolean =
    any { it.isRequestRequired() }

@UseExperimental(ExperimentalContracts::class)
fun PermissionStatus.isGranted(): Boolean {
    contract {
        returns(true) implies (this@isGranted is PermissionStatus.Granted)
    }
    return this is PermissionStatus.Granted
}
