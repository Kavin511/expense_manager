package com.devstudio.sharedmodule

import androidx.compose.runtime.Composable
import com.devstudio.sharedmodule.importData.model.CSVRow
import com.devstudio.sharedmodule.importData.model.TransactionMapResult

@Composable
expect fun FilePicker(
    show: Boolean,
    onFileSelected: (List<CSVRow>?) -> Unit,
)


expect suspend fun saveTransactions(transactions: List<List<String>>) : TransactionMapResult

