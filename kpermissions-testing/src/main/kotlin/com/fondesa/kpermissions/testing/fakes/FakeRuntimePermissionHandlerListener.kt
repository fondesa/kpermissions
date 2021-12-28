package com.fondesa.kpermissions.testing.fakes

import com.fondesa.kpermissions.PermissionStatus
import com.fondesa.kpermissions.request.runtime.RuntimePermissionHandler

/**
 * Fake implementation of [RuntimePermissionHandler.Listener] used in tests.
 */
public class FakeRuntimePermissionHandlerListener : RuntimePermissionHandler.Listener {
    public val receivedPermissionsStatus: List<List<PermissionStatus>> get() = mutableReceivedPermissionsStatus.toList()
    private val mutableReceivedPermissionsStatus = mutableListOf<List<PermissionStatus>>()

    override fun onPermissionsResult(result: List<PermissionStatus>) {
        mutableReceivedPermissionsStatus += result
    }
}
