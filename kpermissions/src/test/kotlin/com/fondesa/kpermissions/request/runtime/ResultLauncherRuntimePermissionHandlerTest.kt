package com.fondesa.kpermissions.request.runtime

import android.Manifest
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragment
import androidx.fragment.app.testing.withFragment
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.fondesa.kpermissions.PermissionStatus
import com.fondesa.test.context
import com.fondesa.test.denyPermissions
import com.fondesa.test.grantPermissions
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doNothing
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.verifyZeroInteractions
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

/**
 * Tests of [ResultLauncherRuntimePermissionHandler].
 */
@RunWith(AndroidJUnit4::class)
@Config(minSdk = 23)
class ResultLauncherRuntimePermissionHandlerTest {
    private val firstPermission = Manifest.permission.ACCESS_FINE_LOCATION
    private val secondPermission = Manifest.permission.SEND_SMS
    private val permissions = arrayOf(firstPermission, secondPermission)
    private val listener = mock<RuntimePermissionHandler.Listener>()
    private lateinit var scenario: FragmentScenario<ResultLauncherRuntimePermissionHandler>
    private lateinit var fragment: ResultLauncherRuntimePermissionHandler
    private lateinit var resultLauncher: ActivityResultLauncher<Array<out String>>

    @Before
    fun launchScenario() {
        scenario = launchFragment {
            ResultLauncherRuntimePermissionHandler().also { fragment ->
                fragment.resultLauncher = spy(fragment.resultLauncher) {
                    doNothing().whenever(it).launch(any())
                }.also { resultLauncher = it }
            }
        }
        fragment = scenario.withFragment(::spy)
    }

    @Before
    fun denyPermissions() {
        context.denyPermissions(*permissions)
    }

    @Test
    fun `When permissions are already granted, they are not requested and listener is notified`() {
        fragment.attachListener(permissions, listener)
        context.grantPermissions(*permissions)

        fragment.handleRuntimePermissions(permissions)

        verify(listener).onPermissionsResult(permissions.map(PermissionStatus::Granted))
        verifyNoMoreInteractions(listener)
        verifyZeroInteractions(resultLauncher)
    }

    @Test
    fun `When permissions are already granted and listener is not attached, nothing happens`() {
        context.grantPermissions(*permissions)

        // It shouldn't throw an exception.
        fragment.handleRuntimePermissions(permissions)
    }

    @Test
    fun `When permissions are not already granted, they are requested and listener is not notified`() {
        fragment.attachListener(permissions, listener)

        fragment.handleRuntimePermissions(permissions)

        verify(resultLauncher).launch(permissions)
        verifyNoMoreInteractions(resultLauncher)
        verifyZeroInteractions(listener)
    }

    @Test
    fun `When there is already a permission request pending, the second permission request is not processed`() {
        fragment.attachListener(permissions, listener)

        fragment.handleRuntimePermissions(permissions)
        fragment.handleRuntimePermissions(permissions)

        // The second resultLauncher.launch() mustn't be called.
        verify(resultLauncher).launch(permissions)
    }

    @Test
    fun `When the permission request result is received, the second permission request can be processed`() {
        fragment.attachListener(permissions, listener)

        fragment.handleRuntimePermissions(permissions)
        fragment.handleRuntimePermissions(permissions)

        // The second resultLauncher.launch() mustn't be called.
        verify(resultLauncher).launch(permissions)

        fragment.onPermissionsResult(firstPermission to true, secondPermission to true)
        fragment.handleRuntimePermissions(permissions)

        // Now the method resultLauncher.launch() can be called again because the Fragment
        // isn't processing the permissions anymore.
        verify(resultLauncher, times(2)).launch(permissions)
    }

    @Test
    fun `When permission status should be notified in onPermissionsResult and listener is not attached, nothing happens`() {
        var fragment = scenario.withFragment { this }
        fragment.attachListener(permissions, listener)
        fragment.handleRuntimePermissions(permissions)

        // This will detach the listener.
        scenario.recreate()
        fragment = scenario.withFragment { this }

        // It shouldn't throw an exception.
        fragment.onPermissionsResult(firstPermission to true, secondPermission to true)
    }

    @Test
    fun `When state is saved after a request is sent, permissions result is delivered when state is restored`() {
        var fragment = scenario.withFragment { this }
        fragment.attachListener(permissions, listener)
        fragment.handleRuntimePermissions(permissions)

        scenario.recreate()
        fragment = scenario.withFragment { this }

        fragment.attachListener(permissions, listener)
        fragment.onPermissionsResult(firstPermission to true, secondPermission to true)

        verify(listener).onPermissionsResult(permissions.map(PermissionStatus::Granted))
    }

    @Test
    fun `When permissions result is received before permissions are requested, listener is not notified`() {
        fragment.attachListener(permissions, listener)

        fragment.onPermissionsResult(firstPermission to true, secondPermission to true)

        verifyZeroInteractions(listener)
    }

    // Verifies the bug: https://issuetracker.google.com/issues/180884668.
    @Test
    fun `When permissions result is received without permissions, the listeners are notified with granted permissions`() {
        fragment.attachListener(permissions, listener)
        fragment.handleRuntimePermissions(permissions)

        fragment.onPermissionsResult()

        verify(listener).onPermissionsResult(permissions.map(PermissionStatus::Granted))
    }

    // Verifies the bug: https://issuetracker.google.com/issues/180884668.
    @Test
    fun `When permissions result is received without some permissions, the listeners are notified with granted permissions`() {
        fragment.attachListener(permissions, listener)
        fragment.handleRuntimePermissions(permissions)
        fragment.stubRationaleResult(firstPermission, true)

        fragment.onPermissionsResult(firstPermission to false)

        verify(listener).onPermissionsResult(
            listOf(
                PermissionStatus.Denied.ShouldShowRationale(firstPermission),
                PermissionStatus.Granted(secondPermission)
            )
        )
    }

    @Test
    fun `When permissions result is received with granted permissions, the listeners are notified`() {
        fragment.attachListener(permissions, listener)
        fragment.handleRuntimePermissions(permissions)

        fragment.onPermissionsResult(firstPermission to true, secondPermission to true)

        verify(listener).onPermissionsResult(permissions.map(PermissionStatus::Granted))
    }

    @Test
    fun `When permissions result is received with denied permissions, the listeners are notified`() {
        fragment.attachListener(permissions, listener)
        fragment.handleRuntimePermissions(permissions)
        fragment.stubRationaleResult(firstPermission, true)
        fragment.stubRationaleResult(secondPermission, true)

        fragment.onPermissionsResult(firstPermission to false, secondPermission to false)

        verify(listener).onPermissionsResult(permissions.map(PermissionStatus.Denied::ShouldShowRationale))

        fragment.handleRuntimePermissions(permissions)
        fragment.stubRationaleResult(firstPermission, false)

        fragment.onPermissionsResult(firstPermission to false, secondPermission to false)

        verify(listener).onPermissionsResult(
            listOf(
                PermissionStatus.Denied.Permanently(firstPermission),
                PermissionStatus.Denied.ShouldShowRationale(secondPermission)
            )
        )

        fragment.handleRuntimePermissions(permissions)
        fragment.stubRationaleResult(secondPermission, false)

        fragment.onPermissionsResult(firstPermission to false, secondPermission to false)

        verify(listener).onPermissionsResult(permissions.map(PermissionStatus.Denied::Permanently))
    }

    @Test
    fun `When permissions result is received with denied permissions and they are granted later by the user, the listeners are notified`() {
        fragment.attachListener(permissions, listener)
        fragment.handleRuntimePermissions(permissions)
        fragment.stubRationaleResult(secondPermission, true)

        fragment.onPermissionsResult(firstPermission to true, secondPermission to false)

        verify(listener).onPermissionsResult(
            listOf(
                PermissionStatus.Granted(firstPermission),
                PermissionStatus.Denied.ShouldShowRationale(secondPermission)
            )
        )

        fragment.handleRuntimePermissions(permissions)
        fragment.onPermissionsResult(firstPermission to true, secondPermission to true)

        verify(listener).onPermissionsResult(permissions.map(PermissionStatus::Granted))
    }

    @Test
    fun `When Fragment is not added yet, the permissions are handled when it will be attached`() {
        fragment.attachListener(permissions, listener)
        context.grantPermissions(*permissions)
        whenever(fragment.isAdded) doReturn false

        fragment.handleRuntimePermissions(permissions)

        verify(listener, never()).onPermissionsResult(any())

        fragment.onAttach(context)

        verify(listener).onPermissionsResult(permissions.map(PermissionStatus::Granted))
    }

    private fun ResultLauncherRuntimePermissionHandler.onPermissionsResult(vararg permissions: Pair<String, Boolean>) {
        onPermissionsResult(permissions.toMap())
    }

    private fun Fragment.stubRationaleResult(permission: String, result: Boolean) {
        whenever(shouldShowRequestPermissionRationale(permission)) doReturn result
    }
}
