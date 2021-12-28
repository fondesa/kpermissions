package com.fondesa.kpermissions.testing.fakes

import com.fondesa.kpermissions.PermissionStatus
import com.fondesa.kpermissions.request.PermissionRequest

/**
 * Fake implementation of [PermissionRequest.Listener] used in tests.
 */
public class FakePermissionRequestListener : PermissionRequest.Listener {
    public val receivedPermissionsResults: List<List<PermissionStatus>> get() = mutableReceivedPermissionsResults.toList()
    private val mutableReceivedPermissionsResults = mutableListOf<List<PermissionStatus>>()

    override fun onPermissionsResult(result: List<PermissionStatus>) {
        mutableReceivedPermissionsResults += result
    }
}
