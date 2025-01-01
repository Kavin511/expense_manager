package com.devstudio.sharedmodule

import androidx.compose.runtime.Composable
import com.devstudio.database.models.Transaction
import com.devstudio.sharedmodule.importData.model.CSVRow
import com.devstudio.sharedmodule.importData.presentation.TransactionImportResult
import platform.UIKit.*
import platform.UIKit.UIUserInterfaceIdiomPad

actual suspend fun saveTransactions(transactions: List<Transaction>): TransactionImportResult {
    return TransactionImportResult.ImportFailed
}

@Composable
actual fun FilePicker(
    show: Boolean,
    onFileSelected: (List<CSVRow>?) -> Unit,
) {
    //todo kavin need to add implementation here
}


@Composable
actual fun isPortrait(): Boolean {
    return UIDevice.currentDevice.userInterfaceIdiom == UIUserInterfaceIdiomPad
}