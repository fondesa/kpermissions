package com.fondesa.kpermissions.testing.fakes

import com.fondesa.kpermissions.request.runtime.RuntimePermissionHandler

/**
 * Fake implementation of [RuntimePermissionHandler] used in tests.
 */
public class FakeRuntimePermissionHandler : RuntimePermissionHandler {
    public val handledRuntimePermissions: List<Array<out String>> get() = mutableHandledRuntimePermissions.toList()
    private val mutableHandledRuntimePermissions = mutableListOf<Array<out String>>()
    private val listeners = mutableMapOf<Array<out String>, RuntimePermissionHandler.Listener>()

    override fun attachListener(permissions: Array<out String>, listener: RuntimePermissionHandler.Listener) {
        listeners[permissions] = listener
    }

    override fun handleRuntimePermissions(permissions: Array<out String>) {
        if (permissions in listeners) {
            mutableHandledRuntimePermissions += permissions
        }
    }
}
