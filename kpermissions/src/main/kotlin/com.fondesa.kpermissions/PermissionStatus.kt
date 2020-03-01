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

import com.fondesa.kpermissions.request.runtime.nonce.PermissionNonce
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

sealed class PermissionStatus(open val permission: String) {

    data class Accepted(override val permission: String) : PermissionStatus(permission)

    sealed class Denied(permission: String) : PermissionStatus(permission) {

        data class Permanently(override val permission: String) : Denied(permission)

        data class ShouldShowRationale(
            override val permission: String,
            val nonce: PermissionNonce
        ) : Denied(permission)
    }

    data class Unknown(override val permission: String) : PermissionStatus(permission)
}

@UseExperimental(ExperimentalContracts::class)
fun PermissionStatus.Denied.shouldShowRationale(): Boolean {
    contract {
        returns(true) implies (this@shouldShowRationale is PermissionStatus.Denied.ShouldShowRationale)
    }
    return this is PermissionStatus.Denied.ShouldShowRationale
}

