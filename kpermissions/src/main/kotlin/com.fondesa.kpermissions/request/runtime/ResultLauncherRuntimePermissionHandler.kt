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

/**
 * Implementation of [RuntimePermissionHandler] which used the new [ActivityResultContracts] API to manage the permissions' request.
 * It can process maximum one permissions' request at the same time to avoid the overlap of multiple permissions' dialogs.
 */
@RequiresApi(23)
internal class ResultLauncherRuntimePermissionHandler : Fragment(), RuntimePermissionHandler {
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
        pendingPermissions = savedInstanceState?.getStringArray(KEY_PENDING_PERMISSIONS)
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

    @Suppress("OverridingDeprecatedMember")
    override fun requestRuntimePermissions(permissions: Array<out String>) {
        // The Fragment is now processing some permissions.
        pendingPermissions = permissions
        Log.d(TAG, "requesting permissions: ${permissions.joinToString()}")
        resultLauncher.launch(permissions)
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
            @Suppress("DEPRECATION")
            requestRuntimePermissions(permissions)
        } else {
            listener.onPermissionsResult(currentStatus)
        }
    }

    @VisibleForTesting
    fun onPermissionsResult(permissionsResult: Map<String, Boolean>) {
        val pendingPermissions = pendingPermissions ?: return
        // Now the Fragment is not processing the permissions anymore.
        this.pendingPermissions = null
        // Get the listener for this set of permissions.
        // If it's null, the permissions can't be notified.
        val listener = listeners[pendingPermissions.toSet()] ?: return
        // The result does not contain the already granted permissions.
        // This behavior will be fixed in future versions, see: https://issuetracker.google.com/issues/180884668.
        val normalizedResult = pendingPermissions.map { permission -> permission to true }.toMap() + permissionsResult
        val result = normalizedResult.map { (permission, isGranted) ->
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
