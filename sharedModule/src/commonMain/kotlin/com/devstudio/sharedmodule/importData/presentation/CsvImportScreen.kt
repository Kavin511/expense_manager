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
import com.devstudio.sharedmodule.commonMain.composeResources.successfully_imported_transactions
import com.devstudio.sharedmodule.importData.model.MappingStatus
import com.devstudio.sharedmodule.importData.model.MetaInformation
import com.devstudio.sharedmodule.importData.model.TransactionField
import com.devstudio.sharedmodule.importData.model.TransactionFieldType
import com.devstudio.sharedmodule.importData.model.TransactionFieldType.TransactionModeField
import com.devstudio.sharedmodule.importData.presentation.CsvImportIntent.SaveTransactions
import com.devstudio.sharedmodule.showToastAlert
import com.devstudio.utils.formatters.format
import com.devstudio.utils.utils.TransactionMode
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
    val csvImportState = viewModel.uiState()
    val shouldImportFile = csvImportState.shouldImportFile
    if (shouldImportFile) {
        FilePicker(show = shouldImportFile) { platformFile ->
            viewModel.onEvent(CsvImportIntent.Import(platformFile))
        }
    }
    LaunchedEffect(viewModel) {
        viewModel.onEvent(CsvImportIntent.SelectFile)
    }
    Screen(
        title = { Text(text = stringResource(R.string.import_csv)) },
        navController = navController,
        shouldNavigateUp = true
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            val transactionField = remember { transactionField() }
            when (val csvUiState = csvImportState.csvUIState) {
                CsvUIState.Idle -> {}
                CsvUIState.SelectingFile -> {
                    CircularProgressIndicator()
                }

                CsvUIState.MappingSelectedFile -> {
                    FieldMappingScreen(csvImportState, transactionField, viewModel)
                }

                is CsvUIState.TransactionSaveResult -> {
                    val importedCount: Int = csvUiState.result.getOrDefault(0)
                    if (importedCount > 0) {
                        showToastAlert(
                            stringResource(R.string.successfully_imported_transactions).format(importedCount)
                        )
                    } else {
                        showToastAlert(stringResource(R.string.failed_to_import_transactions))
                    }
                    navController.popBackStack()
                }

                CsvUIState.CloseImportScreen -> {
                    navController.popBackStack()
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
                        val incomeMeta = MetaInformation(INCOME, "")
                        val expenseMeta = MetaInformation(EXPENSE, "")
                        val investmentMeta = MetaInformation(INVESTMENT, "")
                        it.additionalInfo = mutableListOf()
                        it.additionalInfo?.addAll(arrayOf(incomeMeta, expenseMeta, investmentMeta))
                        AdditionalMappingInfoRow(
                            INCOME, incomeMeta, "e.g., 'income', 'in', '+' for income transactions"
                        )
                        AdditionalMappingInfoRow(
                            EXPENSE,
                            expenseMeta,
                            "e.g., 'expense', 'ex', '-' for expense transactions"
                        )
                        AdditionalMappingInfoRow(
                            INVESTMENT,
                            investmentMeta,
                            "e.g., 'investment', 'inv' for investment transactions"
                        )
                    }
                }
            }
        }
        item {
            val shouldEnableButton =
                derivedStateOf { transactionField.all { it.isMapped } }
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
            "Amount", "Select the amount of expense or income", type = TransactionFieldType.Amount
        ), TransactionField(
            "Mode",
            "Choose the column that shows if it's expense, income, or invested (For numbers: negative = expense, positive = income)",
            type = TransactionModeField
        ),
        TransactionField("Date", "When the transaction happened", type = TransactionFieldType.DATE),
        TransactionField(
            "Category (Optional)",
            "What the transaction was for (e.g., Food, Shopping, Salary)",
            type = TransactionFieldType.Category
        ).apply { mappingStatus.value = MappingStatus.Mapped(-1) },
        TransactionField(
            "Note", "Any extra details about the transaction", type = TransactionFieldType.Note
        ),
        TransactionField(
            "Book Name (Optional)",
            "Which account to record this in (e.g., Cash, Bank)",
            type = TransactionFieldType.BookName,
        ).apply { mappingStatus.value = MappingStatus.Mapped(-1) }
    )
}
