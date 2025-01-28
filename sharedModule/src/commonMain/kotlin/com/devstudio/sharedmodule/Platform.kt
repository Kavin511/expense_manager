package com.devstudio.sharedmodule

import androidx.compose.runtime.Composable
import com.devstudio.database.models.Transaction
import com.devstudio.sharedmodule.importData.model.CSVRow

@Composable
expect fun FilePicker(
    show: Boolean,
    onFileSelected: (List<CSVRow>?) -> Unit,
)

expect suspend fun saveTransactions(transactions: List<Transaction>): Result<Boolean>

@Composable
expect fun isPortrait(): Boolean

expect fun parseDateToTimestamp(dateStr: String, format: String): Long?