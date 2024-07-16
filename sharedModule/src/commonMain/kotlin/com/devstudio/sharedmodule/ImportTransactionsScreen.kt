package com.devstudio.sharedmodule

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.devstudio.sharedmodule.model.TransactionMapResult
import com.devstudio.sharedmodule.utils.ShimmerBrush
import com.devstudio.sharedmodule.utils.shimmerEffect
import com.devstudio.theme.Greeting
import com.devstudio.theme.appColors
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExcelFileUpload() {
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
    Column(modifier = Modifier.shimmerEffect(showFilePicker)) {
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
            ModalBottomSheet(onDismissRequest = {
                showConflictResolvingScreen = false
            }) {
                TransactionImportSuccessScreen(transactionMapResultState.value, {}, {})
            }
        }
    }
}

