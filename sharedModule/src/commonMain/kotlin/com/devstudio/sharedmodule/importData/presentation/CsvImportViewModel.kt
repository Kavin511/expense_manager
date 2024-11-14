package com.devstudio.sharedmodule.importData.presentation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.devstudio.sharedmodule.model.CSVRow

/**
 * @Author: Kavin
 * @Date: 15/11/24
 */
class CsvImportViewModel : ViewModel() {
    private var columns by mutableStateOf(CSVRow(emptyList()))
    private var csv by mutableStateOf<List<CSVRow>>(emptyList())
    private var shouldImportFile by mutableStateOf(false)
    private var csvUIData by mutableStateOf<CsvUIState>(CsvUIState.Idle)

    fun uiState(): CsvImportState {
        return CsvImportState(
            csvData = csvUIData,
            columns = columns,
            csv = csv,
            shouldImportFile = shouldImportFile
        )
    }

    fun onEvent(event: CsvImportEvent) {
        when (event) {
            is CsvImportEvent.Load -> {
                shouldImportFile = true
            }

            is CsvImportEvent.Import -> {
                shouldImportFile = false
                csv = event.csv ?: emptyList()
                columns = event.csv?.firstOrNull() ?: CSVRow(emptyList())
                csvUIData = CsvUIState.Result
            }
        }
    }

}

interface CsvImportEvent {
    data object Load : CsvImportEvent
    data class Import(val csv:List<CSVRow>?) : CsvImportEvent
}

data class CsvImportState(
    val csvData: CsvUIState,
    val columns: CSVRow?,
    val csv: List<CSVRow>?,
    val shouldImportFile: Boolean,
)

sealed interface CsvUIState {
    data object Idle : CsvUIState
    data object Loading : CsvUIState
    data object Result : CsvUIState
}