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
import androidx.navigation.NavHostController
import com.devstudio.designSystem.components.Screen
import com.devstudio.sharedmodule.FilePicker
import com.devstudio.sharedmodule.commonMain.composeResources.continue_button
import com.devstudio.sharedmodule.commonMain.composeResources.expense_with_contains
import com.devstudio.sharedmodule.commonMain.composeResources.failed_to_import_transactions
import com.devstudio.sharedmodule.commonMain.composeResources.import_csv
import com.devstudio.sharedmodule.commonMain.composeResources.income_with_contains
import com.devstudio.sharedmodule.commonMain.composeResources.investment_with_contains
import com.devstudio.sharedmodule.importData.model.MetaInformation
import com.devstudio.sharedmodule.importData.model.TransactionField
import com.devstudio.sharedmodule.importData.model.TransactionFieldType
import com.devstudio.sharedmodule.importData.model.TransactionFieldType.TransactionModeField
import com.devstudio.sharedmodule.importData.presentation.CsvImportEvent.SaveTransactions
import com.devstudio.utils.utils.TransactionMode.EXPENSE
import com.devstudio.utils.utils.TransactionMode.INCOME
import com.devstudio.utils.utils.TransactionMode.INVESTMENT
import org.jetbrains.compose.resources.stringResource
import com.devstudio.sharedmodule.commonMain.composeResources.Res as R

/**
 * @Author: Kavin
 * @Date: 15/11/24
 */

@Composable
fun CsvImportScreen(navController: NavHostController) {
    val viewModel: CsvImportViewModel = viewModel { CsvImportViewModel() }
    val uiState = viewModel.uiState()
    if (uiState.shouldImportFile) {
        FilePicker(show = uiState.shouldImportFile) { platformFile ->
            viewModel.onEvent(CsvImportEvent.Import(platformFile))
        }
    }
    LaunchedEffect(viewModel) {
        viewModel.onEvent(CsvImportEvent.SelectFile)
    }
    Screen(
        title = stringResource(R.string.import_csv),
        navController = navController,
        shouldNavigateUp = true
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            val transactionField = remember { transactionField() }
            when (uiState.csvData) {
                CsvUIState.Idle -> {}

                CsvUIState.SelectingFile -> {
                    CircularProgressIndicator()
                }

                CsvUIState.MappingSelectedFile -> {
                    FieldMappingScreen(uiState, transactionField, viewModel)
                }

                is CsvUIState.TransactionSaveResult -> {
                    if (uiState.csvData.result.isSuccess) {
                        navController.popBackStack()
                    } else {
                        Column {
                            Text(stringResource(R.string.failed_to_import_transactions))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FieldMappingScreen(
    uiState: CsvImportState, transactionField: List<TransactionField>, viewModel: CsvImportViewModel
) {
    LazyColumn {
        val csv = uiState.csv.orEmpty()
        val header = csv.firstOrNull()
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
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())
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
                FieldMappingItem(header, it, onEvent = viewModel::onEvent) {
                    if (it.type == TransactionModeField) {
                        val income = stringResource(R.string.income_with_contains)
                        val expense = stringResource(R.string.expense_with_contains)
                        val investment = stringResource(R.string.investment_with_contains)
                        val incomeMeta = MetaInformation(INCOME, "")
                        val expenseMeta = MetaInformation(EXPENSE, "")
                        val investmentMeta = MetaInformation(INVESTMENT, "")
                        it.additionalInfo = mutableListOf()
                        it.additionalInfo?.addAll(arrayOf(incomeMeta, expenseMeta, investmentMeta))
                        AdditionalMappingInfoRow(income, incomeMeta)
                        AdditionalMappingInfoRow(expense, expenseMeta)
                        AdditionalMappingInfoRow(investment, investmentMeta)
                    }
                }
            }
        }
        item {
            val shouldEnableButton =
                derivedStateOf { transactionField.all { it.selectedFieldIndex.value != -1 } }
            Button(modifier = Modifier.fillMaxWidth().height(48.dp),
                enabled = shouldEnableButton.value,
                colors = ButtonDefaults.filledTonalButtonColors(),
                onClick = {
                    viewModel.onEvent(SaveTransactions(csv, transactionField))
                }) {
                Text(
                    stringResource(R.string.continue_button),
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }
    }
}

fun transactionField(): List<TransactionField> {
    return listOf(
        TransactionField(
            "Mode",
            "Type of transaction to identify if the transaction is an expense, income, or investment",
            type = TransactionModeField
        ),
        TransactionField(
            "Amount", "The amount of the transaction", type = TransactionFieldType.Amount
        ),
        TransactionField(
            "Book Name",
            "The name of the book where the transaction is recorded",
            type = TransactionFieldType.BookName
        ),
        TransactionField("Date", "The date of the transaction", type = TransactionFieldType.DATE),
        TransactionField(
            "Category", "The category of the transaction", type = TransactionFieldType.Category
        ),
        TransactionField(
            "Note", "Additional notes about the transaction", type = TransactionFieldType.Note
        )
    )
}
