package com.fondesa.kpermissions.extension

import com.fondesa.kpermissions.PermissionStatus
import com.fondesa.kpermissions.request.PermissionRequest

/**
 * Sends the [PermissionRequest] and performs the checks on its status.
 * The result will be returned in the given [callback].
 *
 * @param callback the callback which should be invoked when the result of the permissions' request is returned.
 */
inline fun PermissionRequest.send(crossinline callback: (List<PermissionStatus>) -> Unit) {
    addListener(object : PermissionRequest.Listener {
        override fun onPermissionsResult(result: List<PermissionStatus>) {
            callback(result)
            removeListener(this)
        }
    })
    send()
}
