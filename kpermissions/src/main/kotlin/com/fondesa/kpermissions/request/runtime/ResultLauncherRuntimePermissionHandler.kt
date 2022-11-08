package com.fondesa.kpermissions.request.runtime

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.annotation.VisibleForTesting
import androidx.fragment.app.Fragment
import com.fondesa.kpermissions.PermissionStatus
import com.fondesa.kpermissions.allGranted
import com.fondesa.kpermissions.extension.checkRuntimePermissionsStatus
import com.fondesa.kpermissions.extension.isPermissionGranted

/**
 * Implementation of [RuntimePermissionHandler] which used the new [ActivityResultContracts] API to manage the permissions' request.
 * It can process maximum one permissions' request at the same time to avoid the overlap of multiple permissions' dialogs.
 */
@RequiresApi(23)
internal open /* open for testing */ class ResultLauncherRuntimePermissionHandler : Fragment(), RuntimePermissionHandler {
    @VisibleForTesting var resultLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions(),
        ::onPermissionsResult
    )
    private val listeners = mutableMapOf<Set<String>, RuntimePermissionHandler.Listener>()
    private var pendingHandleRuntimePermissions: (() -> Unit)? = null
    private var pendingPermissions: Array<out String>? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        pendingHandleRuntimePermissions?.invoke()
        pendingHandleRuntimePermissions = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (pendingPermissions == null) {
            pendingPermissions = savedInstanceState?.getStringArray(KEY_PENDING_PERMISSIONS)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putStringArray(KEY_PENDING_PERMISSIONS, pendingPermissions)
    }

    override fun attachListener(
        permissions: Array<out String>,
        listener: RuntimePermissionHandler.Listener
    ) {
        listeners[permissions.toSet()] = listener
    }

    override fun handleRuntimePermissions(permissions: Array<out String>) {
        if (isAdded) {
            handleRuntimePermissionsWhenAdded(permissions)
        } else {
            pendingHandleRuntimePermissions = { handleRuntimePermissionsWhenAdded(permissions) }
        }
    }

    private fun handleRuntimePermissionsWhenAdded(permissions: Array<out String>) {
        // Get the listener for this set of permissions.
        // If it's null, the permissions can't be notified.
        val listener = listeners[permissions.toSet()] ?: return
        val activity = requireActivity()
        val currentStatus = activity.checkRuntimePermissionsStatus(permissions.toList())
        val areAllGranted = currentStatus.allGranted()
        if (!areAllGranted) {
            if (pendingPermissions != null) {
                // The Fragment can process only one request at the same time.
                return
            }
            // Request the permissions.
            requestRuntimePermissions(permissions)
        } else {
            listener.onPermissionsResult(currentStatus)
        }
    }

    private fun requestRuntimePermissions(permissions: Array<out String>) {
        // The Fragment is now processing some permissions.
        pendingPermissions = permissions
        Log.d(TAG, "requesting permissions: ${permissions.joinToString()}")
        // Do an unchecked cast to avoid to introduce breaking changes in the public APIs of this class.
        // This is safe to do since nothing can extend String anyway.
        @Suppress("UNCHECKED_CAST")
        resultLauncher.launch(permissions as Array<String>)
    }

    @VisibleForTesting
    fun onPermissionsResult(permissionsResult: Map<String, Boolean>) {
        val pendingPermissions = pendingPermissions ?: return
        // Now the Fragment is not processing the permissions anymore.
        this.pendingPermissions = null
        // Get the listener for this set of permissions.
        // If it's null, the permissions can't be notified.
        val listener = listeners[pendingPermissions.toSet()] ?: return
        val context = requireContext()
        val result = pendingPermissions.map { permission ->
            val isGranted = permissionsResult.getOrElse(permission) { context.isPermissionGranted(permission) }
            when {
                isGranted -> PermissionStatus.Granted(permission)
                shouldShowRequestPermissionRationale(permission) -> PermissionStatus.Denied.ShouldShowRationale(permission)
                else -> PermissionStatus.Denied.Permanently(permission)
            }
        }
        listener.onPermissionsResult(result)
    }

    companion object {
        private val TAG = ResultLauncherRuntimePermissionHandler::class.java.simpleName
        private const val KEY_PENDING_PERMISSIONS = "pending_permissions"
    }
}
