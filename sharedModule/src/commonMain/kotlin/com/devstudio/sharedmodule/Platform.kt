package com.devstudio.sharedmodule

import androidx.compose.runtime.Composable
import com.devstudio.sharedmodule.model.TransactionMapResult

@Composable
expect fun FilePicker(
    show: Boolean,
    initialDirectory: String? = null,
    fileExtensions: Array<String> = emptyArray(),
    title: String? = null,
    onFileSelected: (List<List<String>>?) -> Unit,
)


expect suspend fun saveTransactions(transactions: List<List<String>>) : TransactionMapResult

