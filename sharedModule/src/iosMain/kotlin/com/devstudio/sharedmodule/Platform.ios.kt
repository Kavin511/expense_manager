package com.devstudio.sharedmodule

import androidx.compose.runtime.Composable
import com.devstudio.database.models.Books
import com.devstudio.database.models.Transaction
import com.devstudio.sharedmodule.importData.model.CSVRow
import com.devstudio.sharedmodule.importData.model.TransactionField
import com.devstudio.sharedmodule.importData.model.TransactionMapResult
import com.devstudio.sharedmodule.importData.presentation.TransactionFieldIndex
import com.devstudio.sharedmodule.importData.presentation.TransactionImportResult

actual suspend fun saveTransactions(transactions: List<Transaction>): TransactionImportResult {
    return TransactionImportResult.ImportFailed
}

@Composable
public actual fun FilePicker(
    show: Boolean,
    onFileSelected: @Composable (List<CSVRow>?) -> Unit,
) {

}
