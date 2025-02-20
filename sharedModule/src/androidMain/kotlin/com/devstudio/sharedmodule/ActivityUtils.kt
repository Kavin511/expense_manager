package com.devstudio.sharedmodule

import android.content.Context
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.runtime.Composable

/**
 * @Author: Kavin
 * @Date: 14/11/24
 */
@Composable
fun simpleActivityForResultLauncher(
    intent: Intent,
    onActivityResult: (resultCode: Int, data: Intent?) -> Unit
): ActivityResultLauncher<Unit> {
    return activityForResultLauncher(
        createIntent = { _, _ -> intent },
        onActivityResult = onActivityResult
    )
}

@Composable
fun <I> activityForResultLauncher(
    createIntent: (context: Context, input: I) -> Intent,
    onActivityResult: (resultCode: Int, data: Intent?) -> Unit
): ActivityResultLauncher<I> {
    return rememberLauncherForActivityResult(
        activityResultContract(
            createIntent = createIntent,
            onActivityResult = onActivityResult
        )
    ) {
    }
}

private fun <I> activityResultContract(
    createIntent: (context: Context, input: I) -> Intent,
    onActivityResult: (resultCode: Int, data: Intent?) -> Unit
): ActivityResultContract<I, Unit> {
    return object : ActivityResultContract<I, Unit>() {
        override fun createIntent(
            context: Context,
            input: I
        ): Intent {
            return createIntent(context, input)
        }

        override fun parseResult(
            resultCode: Int,
            data: Intent?
        ) {
            onActivityResult(resultCode, data)
        }
    }
}