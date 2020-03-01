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

package com.fondesa.kpermissions.request.runtime.nonce

import com.fondesa.kpermissions.request.runtime.RuntimePermissionHandler

/**
 * Used to generate a [PermissionNonce] that can request the permissions one time again.
 */
@Deprecated("If you are using the status API, use PermissionRequest.send() sending a new request instead.")
interface PermissionNonceGenerator {

    /**
     * Generates a [PermissionNonce] that can be used one time.
     *
     * @param handler the [RuntimePermissionHandler] used to request the permissions again.
     * @param permissions the permissions that must be requested again.
     * @return unique instance of [PermissionNonce].
     */
    @Deprecated("If you are using the status API, use PermissionRequest.send() sending a new request instead.")
    fun generateNonce(
        handler: RuntimePermissionHandler,
        permissions: Array<out String>
    ): PermissionNonce
}