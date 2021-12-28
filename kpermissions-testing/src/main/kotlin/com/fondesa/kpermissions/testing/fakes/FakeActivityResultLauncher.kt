package com.fondesa.kpermissions.testing.fakes

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.app.ActivityOptionsCompat

/**
 * Fake implementation of [ActivityResultLauncher] used in tests.
 */
public open class FakeActivityResultLauncher<T> : ActivityResultLauncher<T>() {
    public val launchedInputs: List<T> get() = mutableLaunchedInputs.toList()
    private val mutableLaunchedInputs = mutableListOf<T>()

    override fun getContract(): ActivityResultContract<T, *> = FakeContract()
    override fun unregister() {}
    override fun launch(input: T, options: ActivityOptionsCompat?) {
        mutableLaunchedInputs += input
    }

    private class FakeContract<T> : ActivityResultContract<T, Unit>() {
        override fun createIntent(context: Context, input: T): Intent = Intent()
        override fun parseResult(resultCode: Int, intent: Intent?) {}
    }
}
