package com.fondesa.kpermissions.extension

import androidx.lifecycle.LiveData
import com.fondesa.kpermissions.PermissionStatus
import com.fondesa.kpermissions.request.PermissionRequest

/**
 * Observes the [PermissionRequest]'s status.
 * The returned [LiveData] emits the list of [PermissionStatus] when the result of [PermissionRequest.send] is received.
 * The documentation of the possible values of [PermissionStatus] can be found at [PermissionRequest.Listener.onPermissionsResult].
 *
 * @return a new [LiveData] which emits a new item to the observers every time the result of [PermissionRequest.send] is received.
 * @see PermissionRequest.Listener.onPermissionsResult
 */
fun PermissionRequest.liveData(): LiveData<List<PermissionStatus>> = PermissionsLiveData(this)

private class PermissionsLiveData(private val request: PermissionRequest) : LiveData<List<PermissionStatus>>() {
    private var listener: PermissionRequest.Listener? = null

    override fun onActive() {
        super.onActive()
        listener = object : PermissionRequest.Listener {
            override fun onPermissionsResult(result: List<PermissionStatus>) {
                value = result
            }
        }.also(request::addListener)
    }

    override fun onInactive() {
        super.onInactive()
        listener?.let(request::removeListener)
        listener = null
    }
}
