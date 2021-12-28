package com.fondesa.kpermissions.testing.fakes

import com.fondesa.kpermissions.request.runtime.RuntimePermissionHandler
import com.fondesa.kpermissions.request.runtime.RuntimePermissionHandlerProvider

/**
 * Fake implementation of [RuntimePermissionHandlerProvider] used in tests.
 */
public class FakeRuntimePermissionHandlerProvider : RuntimePermissionHandlerProvider {
    override fun provideHandler(): RuntimePermissionHandler = FakeRuntimePermissionHandler()
}
