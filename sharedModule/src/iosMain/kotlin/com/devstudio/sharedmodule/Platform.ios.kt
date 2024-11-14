package com.devstudio.sharedmodule

import androidx.compose.runtime.Composable
import com.devstudio.sharedmodule.model.CSVRow
import com.devstudio.sharedmodule.model.TransactionMapResult

actual suspend fun saveTransactions(transactions: List<List<String>>): TransactionMapResult {
    return TransactionMapResult(emptyList(), emptyList())
}

@Composable
public actual fun FilePicker(
    show: Boolean,
    initialDirectory: String?,
    fileExtensions: Array<String>,
    title: String?,
    onFileSelected: @Composable (List<CSVRow>?) -> Unit,
) {

}
