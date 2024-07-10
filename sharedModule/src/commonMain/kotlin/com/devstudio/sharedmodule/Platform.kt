package com.devstudio.sharedmodule

import androidx.compose.runtime.Composable

expect fun saveTransactions(transactions: List<List<String>>)

@Composable
expect fun FilePicker(
    show: Boolean,
    initialDirectory: String? = null,
    fileExtensions: Array<String> = emptyArray(),
    title: String? = null,
    onFileSelected: (List<List<String>>?) -> Unit,
)

expect object AppContext
class SharedModuleConfig(
    val appContext: AppContext,
)

object SharedModule {
    fun initialize(config: SharedModuleConfig) {
        val commonContext = config.appContext
    }
}
