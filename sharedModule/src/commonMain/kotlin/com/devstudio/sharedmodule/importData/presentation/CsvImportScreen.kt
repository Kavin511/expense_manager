package com.devstudio.sharedmodule.importData.presentation

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.devstudio.designSystem.appColors
import com.devstudio.designSystem.components.Screen
import com.devstudio.sharedmodule.FilePicker
import com.devstudio.sharedmodule.importData.model.TransactionField

/**
 * @Author: Kavin
 * @Date: 15/11/24
 */

@Composable
fun CsvImportScreen() {
    val viewModel: CsvImportViewModel = viewModel { CsvImportViewModel() }
    val uiState = viewModel.uiState()
    if (uiState.shouldImportFile) {
        FilePicker(show = uiState.shouldImportFile) { platformFile ->
            viewModel.onEvent(CsvImportEvent.Import(platformFile))
        }
    }
    LaunchedEffect(viewModel) {
        viewModel.onEvent(CsvImportEvent.Load)
    }
    Screen(title = "Import CSV", navController = rememberNavController(), shouldNavigateUp = true) {
        Column(modifier = Modifier.fillMaxSize()) {
            when (uiState.csvData) {
                CsvUIState.Idle -> {}

                CsvUIState.Loading -> {
                    CircularProgressIndicator()
                }

                CsvUIState.Result -> {
                    LazyColumn {
                        val csv = uiState.csv.orEmpty()
                        val header = csv.firstOrNull()
                        val transactionField = transactionField()
                        if (header == null) {
                            item {
                                Text(
                                    "No data found",
                                    style = MaterialTheme.typography.bodyLarge,
                                )
                            }
                            return@LazyColumn
                        }
                        item {
                            val rowSubset = remember { csv.take(6) }
                            Column(
                                modifier = Modifier.fillMaxWidth().horizontalScroll(
                                    rememberScrollState()
                                )
                            ) {
                                rowSubset.forEachIndexed { index, row ->
                                    Row {
                                        row.values.forEach {
                                            CSVCell(
                                                it, header = index == 0, even = index % 2 == 0
                                            )
                                        }
                                    }
                                }
                            }
                        }
                        item {
                            transactionField.forEach {
                                CsvColumnToTransactionFieldMapping(header, it)
                            }
                        }
                        item {
                            val shouldEnableButton =
                                derivedStateOf { transactionField.all { it.selectedFieldIndex.value != -1 } }
                            Button(
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                enabled = shouldEnableButton.value,
                                colors = ButtonDefaults.filledTonalButtonColors(),
                                onClick = { viewModel.onEvent(CsvImportEvent.VALIDATE_MAPPING(csv)) }) {
                                Text(
                                    "Continue",
                                    style = MaterialTheme.typography.bodyLarge,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

fun transactionField() = listOf(
    TransactionField(
        "Mode",
        "Type of transaction to identify if the transaction is an expense, income, or investment"
    ),
    TransactionField("Amount", "The amount of the transaction"),
    TransactionField("Book Name", "The name of the book where the transaction is recorded"),
    TransactionField("Date", "The date of the transaction"),
    TransactionField("Category", "The category of the transaction"),
    TransactionField("Note", "Additional notes about the transaction")
)
