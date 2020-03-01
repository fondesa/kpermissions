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

package com.fondesa.kpermissions.alias

import com.fondesa.kpermissions.request.runtime.nonce.PermissionNonce

/**
 * Alias used to define the lambda used to identify a request's callback invoked
 * when some permission are accepted.
 */
@Deprecated("LYRA_DEPRECATED")
typealias AcceptedCallback = (Array<out String>) -> Unit

/**
 * Alias used to define the lambda used to identify a request's callback invoked
 * when some permission are denied.
 */
@Deprecated("LYRA_DEPRECATED")
typealias DeniedCallback = (Array<out String>) -> Unit

/**
 * Alias used to define the lambda used to identify a request's callback invoked
 * when some permission are permanently denied.
 */
@Deprecated("LYRA_DEPRECATED")
typealias PermanentlyDeniedCallback = (Array<out String>) -> Unit

/**
 * Alias used to define the lambda used to identify a request's callback invoked
 * when some permission should show a rationale.
 */
@Deprecated("LYRA_DEPRECATED")
typealias RationaleCallback = (Array<out String>, PermissionNonce) -> Unit