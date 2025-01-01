package com.devstudio.sharedmodule

import androidx.compose.runtime.Composable
import com.devstudio.database.models.Transaction
import com.devstudio.sharedmodule.importData.model.CSVRow
import com.devstudio.sharedmodule.importData.model.TransactionField
import com.devstudio.sharedmodule.importData.model.TransactionMapResult
import com.devstudio.sharedmodule.importData.presentation.TransactionFieldIndex
import com.devstudio.sharedmodule.importData.presentation.TransactionImportResult

@Composable
expect fun FilePicker(
    show: Boolean,
    onFileSelected: (List<CSVRow>?) -> Unit,
)

expect suspend fun saveTransactions(transactions: List<Transaction>): TransactionImportResult

@Composable
expect fun isPortrait(): Boolean
