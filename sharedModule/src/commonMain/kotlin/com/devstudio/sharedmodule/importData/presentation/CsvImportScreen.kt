package com.devstudio.sharedmodule.importData.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import com.devstudio.designSystem.components.BottomSheet
import com.devstudio.sharedmodule.FilePicker

/**
 * @Author: Kavin
 * @Date: 15/11/24
 */
@OptIn(ExperimentalMaterial3Api::class)
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
    BottomSheet({}) {
        Column {
            when (uiState.csvData) {
                CsvUIState.Idle -> {
                    Button(onClick = { viewModel.onEvent(CsvImportEvent.Load) }) {
                        Text("Load")
                    }
                }

                CsvUIState.Loading -> {
                    CircularProgressIndicator()
                }

                CsvUIState.Result -> {
                    LazyColumn {
                        val csv = uiState.csv.orEmpty()
                        items(csv.take(5).orEmpty().size) { item ->
                            LazyRow {
                                items(csv[item].values.size) { index ->
                                    Text(csv[item].values[index])
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}