package com.fondesa.kpermissions.extension

import android.Manifest
import com.fondesa.kpermissions.PermissionStatus
import com.fondesa.kpermissions.request.PermissionRequest
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Tests for SendPermissionRequestWithListener.kt file.
 */
class SendPermissionRequestWithListenerKtTest {
    private val request = mock<PermissionRequest>()
    private val listenerCaptor = argumentCaptor<PermissionRequest.Listener>()

    @Test
    fun `When send { } is invoked and listener is notified, callback is invoked`() {
        val results = mutableListOf<List<PermissionStatus>>()

        request.send { results += it }

        assertTrue(results.isEmpty())
        verify(request).addListener(listenerCaptor.capture())

        listenerCaptor.lastValue.onPermissionsResult(
            listOf(
                PermissionStatus.Granted(Manifest.permission.SEND_SMS),
                PermissionStatus.Denied.Permanently(Manifest.permission.CALL_PHONE)
            )
        )

        assertEquals(1, results.size)
        assertEquals(
            listOf(
                PermissionStatus.Granted(Manifest.permission.SEND_SMS),
                PermissionStatus.Denied.Permanently(Manifest.permission.CALL_PHONE)
            ),
            results.first()
        )
    }

    @Test
    fun `When send { } is invoked and listener is notified, listener is removed`() {
        request.send {}

        verify(request).addListener(listenerCaptor.capture())
        verify(request, never()).removeListener(any())

        val listener = listenerCaptor.lastValue
        listener.onPermissionsResult(
            listOf(
                PermissionStatus.Granted(Manifest.permission.SEND_SMS),
                PermissionStatus.Denied.Permanently(Manifest.permission.CALL_PHONE)
            )
        )

        verify(request).removeListener(listener)
    }
}
