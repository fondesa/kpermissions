package com.fondesa.kpermissions.request.runtime

import android.Manifest
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.fragment.app.Fragment
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragment
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.fondesa.kpermissions.PermissionStatus
import com.fondesa.kpermissions.testing.context
import com.fondesa.kpermissions.testing.fakes.FakeActivityResultLauncher
import com.fondesa.kpermissions.testing.fakes.FakeRuntimePermissionHandlerListener
import com.fondesa.kpermissions.testing.fragment
import com.fondesa.kpermissions.testing.denyPermissions
import com.fondesa.kpermissions.testing.grantPermissions
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
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
    private val listener = FakeRuntimePermissionHandlerListener()
    private lateinit var scenario: FragmentScenario<TestResultLauncherRuntimePermissionHandler>
    private val fragment get() = scenario.fragment
    private val resultLauncher: FakeActivityResultLauncher<Array<String>>
        get() = fragment.resultLauncher as FakeActivityResultLauncher<Array<String>>

    @Before
    fun launchScenario() {
        scenario = launchFragment()
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

        assertEquals(listOf(permissions.map(PermissionStatus::Granted)), listener.receivedPermissionsStatus)
        assertTrue(resultLauncher.launchedInputs.isEmpty())
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

        assertTrue(listener.receivedPermissionsStatus.isEmpty())
        assertEquals(listOf(permissions), resultLauncher.launchedInputs)
    }

    @Test
    fun `When there is already a permission request pending, the second permission request is not processed`() {
        fragment.attachListener(permissions, listener)

        fragment.handleRuntimePermissions(permissions)
        fragment.handleRuntimePermissions(permissions)

        // The second resultLauncher.launch() mustn't be called.
        assertEquals(listOf(permissions), resultLauncher.launchedInputs)
    }

    @Test
    fun `When the permission request result is received, the second permission request can be processed`() {
        fragment.attachListener(permissions, listener)

        fragment.handleRuntimePermissions(permissions)
        fragment.handleRuntimePermissions(permissions)

        // The second resultLauncher.launch() mustn't be called.
        assertEquals(listOf(permissions), resultLauncher.launchedInputs)

        fragment.onPermissionsResult(firstPermission to true, secondPermission to true)
        fragment.handleRuntimePermissions(permissions)

        // Now the method resultLauncher.launch() can be called again because the Fragment
        // isn't processing the permissions anymore.
        assertEquals(listOf(permissions, permissions), resultLauncher.launchedInputs)
    }

    @Test
    fun `When permission status should be notified in onPermissionsResult and listener is not attached, nothing happens`() {
        fragment.attachListener(permissions, listener)
        fragment.handleRuntimePermissions(permissions)

        // This will detach the listener.
        scenario.recreate()

        // It shouldn't throw an exception.
        fragment.onPermissionsResult(firstPermission to true, secondPermission to true)
    }

    @Test
    fun `When state is saved after a request is sent, permissions result is delivered when state is restored`() {
        fragment.attachListener(permissions, listener)
        fragment.handleRuntimePermissions(permissions)

        scenario.recreate()

        fragment.attachListener(permissions, listener)
        fragment.onPermissionsResult(firstPermission to true, secondPermission to true)

        assertEquals(listOf(permissions.map(PermissionStatus::Granted)), listener.receivedPermissionsStatus)
    }

    @Test
    fun `When permissions result is received before permissions are requested, listener is not notified`() {
        fragment.attachListener(permissions, listener)

        fragment.onPermissionsResult(firstPermission to true, secondPermission to true)

        assertTrue(listener.receivedPermissionsStatus.isEmpty())
    }

    @Test
    fun `When permissions result is received with implicit permissions in the manifest, the listeners are notified`() {
        fragment.attachListener(permissions, listener)
        fragment.handleRuntimePermissions(permissions)
        context.grantPermissions(firstPermission, secondPermission)

        fragment.onPermissionsResult()

        assertEquals(listOf(permissions.map(PermissionStatus::Granted)), listener.receivedPermissionsStatus)
    }

    @Test
    fun `When permissions result is received with implicit permissions not in the manifest, the listeners are notified`() {
        fragment.attachListener(permissions, listener)
        fragment.handleRuntimePermissions(permissions)
        fragment.overrideShouldShowRequestPermissionRationale(
            firstPermission to true,
            secondPermission to false
        )

        fragment.onPermissionsResult()

        assertEquals(
            listOf(
                listOf(
                    PermissionStatus.Denied.ShouldShowRationale(firstPermission),
                    PermissionStatus.Denied.Permanently(secondPermission)
                )
            ),
            listener.receivedPermissionsStatus
        )
    }

    @Test
    fun `When permissions result is received without some permissions, the listeners are notified`() {
        fragment.attachListener(permissions, listener)
        fragment.handleRuntimePermissions(permissions)
        fragment.overrideShouldShowRequestPermissionRationale(firstPermission to true)
        context.grantPermissions(secondPermission)

        fragment.onPermissionsResult(firstPermission to false)

        assertEquals(
            listOf(
                listOf(
                    PermissionStatus.Denied.ShouldShowRationale(firstPermission),
                    PermissionStatus.Granted(secondPermission)
                )
            ),
            listener.receivedPermissionsStatus
        )
    }

    @Test
    fun `When permissions result is received with granted permissions, the listeners are notified`() {
        fragment.attachListener(permissions, listener)
        fragment.handleRuntimePermissions(permissions)

        fragment.onPermissionsResult(firstPermission to true, secondPermission to true)

        assertEquals(listOf(permissions.map(PermissionStatus::Granted)), listener.receivedPermissionsStatus)
    }

    @Test
    fun `When permissions result is received with denied permissions, the listeners are notified`() {
        fragment.attachListener(permissions, listener)
        fragment.handleRuntimePermissions(permissions)
        fragment.overrideShouldShowRequestPermissionRationale(
            firstPermission to true,
            secondPermission to true,
        )

        fragment.onPermissionsResult(firstPermission to false, secondPermission to false)

        assertEquals(listOf(permissions.map(PermissionStatus.Denied::ShouldShowRationale)), listener.receivedPermissionsStatus)

        fragment.handleRuntimePermissions(permissions)
        fragment.overrideShouldShowRequestPermissionRationale(
            firstPermission to false,
            secondPermission to true,
        )

        fragment.onPermissionsResult(firstPermission to false, secondPermission to false)

        assertEquals(
            listOf(
                permissions.map(PermissionStatus.Denied::ShouldShowRationale),
                listOf(
                    PermissionStatus.Denied.Permanently(firstPermission),
                    PermissionStatus.Denied.ShouldShowRationale(secondPermission)
                )
            ),
            listener.receivedPermissionsStatus
        )

        fragment.handleRuntimePermissions(permissions)
        fragment.overrideShouldShowRequestPermissionRationale(
            firstPermission to false,
            secondPermission to false,
        )

        fragment.onPermissionsResult(firstPermission to false, secondPermission to false)

        assertEquals(
            listOf(
                permissions.map(PermissionStatus.Denied::ShouldShowRationale),
                listOf(
                    PermissionStatus.Denied.Permanently(firstPermission),
                    PermissionStatus.Denied.ShouldShowRationale(secondPermission)
                ),
                permissions.map(PermissionStatus.Denied::Permanently),
            ),
            listener.receivedPermissionsStatus
        )
    }

    @Test
    fun `When permissions result is received with denied permissions and they are granted later by the user, the listeners are notified`() {
        fragment.attachListener(permissions, listener)
        fragment.handleRuntimePermissions(permissions)
        fragment.overrideShouldShowRequestPermissionRationale(secondPermission to true)

        fragment.onPermissionsResult(firstPermission to true, secondPermission to false)

        assertEquals(
            listOf(
                listOf(
                    PermissionStatus.Granted(firstPermission),
                    PermissionStatus.Denied.ShouldShowRationale(secondPermission)
                )
            ),
            listener.receivedPermissionsStatus
        )

        fragment.handleRuntimePermissions(permissions)
        fragment.onPermissionsResult(firstPermission to true, secondPermission to true)

        assertEquals(
            listOf(
                listOf(
                    PermissionStatus.Granted(firstPermission),
                    PermissionStatus.Denied.ShouldShowRationale(secondPermission)
                ),
                permissions.map(PermissionStatus::Granted)
            ),
            listener.receivedPermissionsStatus
        )
    }

    @Test
    fun `When Fragment is not added yet and the permissions are granted, the permissions are handled when it will be attached`() {
        fragment.attachListener(permissions, listener)
        context.grantPermissions(*permissions)
        fragment.changeIsAddedValue()

        fragment.handleRuntimePermissions(permissions)

        assertTrue(listener.receivedPermissionsStatus.isEmpty())

        fragment.onAttach(context)
        fragment.onCreate(null)

        assertEquals(listOf(permissions.map(PermissionStatus::Granted)), listener.receivedPermissionsStatus)
    }

    @Test
    fun `When Fragment is not added yet and the permissions are not granted, the permissions are handled when it will be attached`() {
        fragment.attachListener(permissions, listener)
        fragment.changeIsAddedValue()

        fragment.handleRuntimePermissions(permissions)

        assertTrue(listener.receivedPermissionsStatus.isEmpty())

        fragment.onAttach(context)
        fragment.onCreate(null)
        fragment.onPermissionsResult(firstPermission to true, secondPermission to true)

        assertEquals(listOf(permissions.map(PermissionStatus::Granted)), listener.receivedPermissionsStatus)
    }

    @Test
    fun `When multiple different listeners are attached and result is received, all the listeners are notified`() {
        val firstListener = FakeRuntimePermissionHandlerListener()
        val secondListener = FakeRuntimePermissionHandlerListener()
        fragment.attachListener(permissions, firstListener)
        fragment.attachListener(permissions, secondListener)
        fragment.handleRuntimePermissions(permissions)

        fragment.onPermissionsResult(firstPermission to true, secondPermission to true)

        val expectedResult = permissions.map(PermissionStatus::Granted)
        assertEquals(listOf(expectedResult), firstListener.receivedPermissionsStatus)
        assertEquals(listOf(expectedResult), secondListener.receivedPermissionsStatus)
    }

    @Test
    fun `When same listener is attached multiple times and result is received, listener is notified once`() {
        fragment.attachListener(permissions, listener)
        fragment.attachListener(permissions, listener)
        fragment.handleRuntimePermissions(permissions)

        fragment.onPermissionsResult(firstPermission to true, secondPermission to true)

        val expectedResult = permissions.map(PermissionStatus::Granted)
        assertEquals(listOf(expectedResult), listener.receivedPermissionsStatus)
    }

    internal class TestResultLauncherRuntimePermissionHandler : ResultLauncherRuntimePermissionHandler() {
        private val shouldShowRequestPermissionRationaleResults = mutableMapOf<String, Boolean>()

        init {
            resultLauncher = FakePermissionsLauncher(resultLauncher)
        }

        fun overrideShouldShowRequestPermissionRationale(vararg results: Pair<String, Boolean>) {
            shouldShowRequestPermissionRationaleResults.clear()
            shouldShowRequestPermissionRationaleResults += results
        }

        fun changeIsAddedValue() {
            val mAddedField = Fragment::class.java.getDeclaredField("mAdded")
            mAddedField.isAccessible = true
            mAddedField.set(this, false)
            mAddedField.isAccessible = false
        }

        fun onPermissionsResult(vararg permissions: Pair<String, Boolean>) {
            onPermissionsResult(permissions.toMap())
        }

        override fun shouldShowRequestPermissionRationale(permission: String): Boolean {
            return shouldShowRequestPermissionRationaleResults.getOrElse(permission) {
                super.shouldShowRequestPermissionRationale(permission)
            }
        }

        private class FakePermissionsLauncher(
            private val originalLauncher: ActivityResultLauncher<Array<String>>
        ) : FakeActivityResultLauncher<Array<String>>() {
            override fun unregister() = originalLauncher.unregister()
            override fun getContract(): ActivityResultContract<Array<String>, *> = originalLauncher.contract
        }
    }
}
