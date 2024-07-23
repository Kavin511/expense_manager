package com.devstudio.sharedmodule

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.devstudio.sharedmodule.model.TransactionMapResult
import com.devstudio.sharedmodule.model.TransactionWithIndex
import com.devstudio.sharedmodule.utils.shimmerEffect
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExcelFileUpload(
    onClose: () -> Unit, modifyTransaction: (TransactionWithIndex) -> Result<Unit>
) {
    var showFilePicker by remember { mutableStateOf(true) }
    val fileType = arrayOf(
        "text/csv",
        "text/comma-separated-values",
        "application/csv",
        "application/excel",
        "application/vnd.ms-excel",
        "application/vnd.msexcel",
        "text/anytext",
        "application/octet-stream"
    )
    val transactionMapResultState = remember {
        mutableStateOf(
            TransactionMapResult(
                emptyList(), emptyList()
            )
        )
    }
    var showConflictResolvingScreen by remember { mutableStateOf(false) }
    FilePicker(show = showFilePicker, fileExtensions = fileType) { platformFile ->
        CoroutineScope(Dispatchers.Main).launch {
            if (platformFile != null) {
                transactionMapResultState.value = saveTransactions(platformFile)
                showConflictResolvingScreen = true
            }
            showFilePicker = false
        }
    }
    if (showConflictResolvingScreen) {
        ModalBottomSheet(
            onDismissRequest = {
                showConflictResolvingScreen = false
                onClose.invoke()
            },
        ) {
            TransactionImportSuccessScreen(transactionMapResultState.value, modifyTransaction)
        }
    }
}

