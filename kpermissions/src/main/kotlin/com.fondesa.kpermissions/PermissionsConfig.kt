@file:Suppress("DEPRECATION")

package com.fondesa.kpermissions

import android.app.Application
import com.fondesa.kpermissions.request.PermissionRequest
import com.fondesa.kpermissions.request.runtime.FragmentRuntimePermissionHandlerProvider
import com.fondesa.kpermissions.request.runtime.ResultLauncherRuntimePermissionHandlerProvider
import com.fondesa.kpermissions.request.runtime.RuntimePermissionHandlerProvider

internal var shouldUseLegacyRuntimePermissionHandler = false

/**
 * This function should be used only to test a potential regression related to runtime permissions.
 * Specifically, it enables again the usage of [FragmentRuntimePermissionHandlerProvider] instead of
 * [ResultLauncherRuntimePermissionHandlerProvider] as the default [RuntimePermissionHandlerProvider].
 * This function MUST be called before the creation of every [PermissionRequest] (e.g. in the [Application]).
 */
@Suppress("DeprecatedCallableAddReplaceWith")
@Deprecated("This class will be removed with the legacy API.")
public fun useLegacyRuntimePermissionHandler() {
    shouldUseLegacyRuntimePermissionHandler = true
}
