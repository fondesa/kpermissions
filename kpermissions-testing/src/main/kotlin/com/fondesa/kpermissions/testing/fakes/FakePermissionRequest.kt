package com.fondesa.kpermissions.testing.fakes

import com.fondesa.kpermissions.PermissionStatus
import com.fondesa.kpermissions.request.PermissionRequest

/**
 * Fake implementation of [PermissionRequest] used in tests.
 */
public class FakePermissionRequest : PermissionRequest {
    public val listeners: List<PermissionRequest.Listener> get() = mutableListeners.toList()
    private val mutableListeners = mutableListOf<PermissionRequest.Listener>()

    override fun addListener(listener: PermissionRequest.Listener) {
        mutableListeners += listener
    }

    override fun removeListener(listener: PermissionRequest.Listener) {
        mutableListeners -= listener
    }

    override fun removeAllListeners() {
        mutableListeners.clear()
    }

    override fun checkStatus(): List<PermissionStatus> = emptyList()

    override fun send() {}
}
