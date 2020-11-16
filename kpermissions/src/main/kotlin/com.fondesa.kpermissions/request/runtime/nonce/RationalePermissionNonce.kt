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

@file:Suppress("DEPRECATION", "OverridingDeprecatedMember")

package com.fondesa.kpermissions.request.runtime.nonce

import com.fondesa.kpermissions.request.runtime.RuntimePermissionHandler

/**
 * Implementation of [PermissionNonce] used to request the permissions again when
 * the rationale related to these permissions was displayed to the user.
 * If this [PermissionNonce] is used more than one time, it throws a [PermissionNonceUsedException].
 *
 * @property handler the [RuntimePermissionHandler] used to request the permissions again.
 * @property permissions the permissions that must be requested again.
 */
@Deprecated("If you are using the status API, use PermissionRequest.send() sending a new request instead.")
public class RationalePermissionNonce(
    private val handler: RuntimePermissionHandler,
    private val permissions: Array<out String>
) : PermissionNonce {
    private var used: Boolean = false

    override fun use() {
        if (used) {
            throw PermissionNonceUsedException("This nonce was used before.")
        }
        // The nonce is now considered as "used"
        used = true
        // The checks must be avoided to not show the rationale explanation again.
        handler.requestRuntimePermissions(permissions)
    }
}
