package com.devstudio.designSystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetDefaults
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devstudio.designSystem.appColors


@Composable
fun MaterialAlert(
    title: String,
    icon: ImageVector,
    content: String,
    positiveText: String,
    negativeText: String,
    positiveCallback: () -> Unit,
    negativeCallback: () -> Unit,
) {
    Box {
        AlertDialog(icon = {
            Icon(icon, contentDescription = "Example Icon")
        }, text = {
            Text(text = content)
        }, title = {
            Text(
                text = title,
                modifier = Modifier.fillMaxWidth().align(Alignment.CenterStart),
                fontSize = 20.sp,
                style = MaterialTheme.typography.headlineMedium
            )
        }, onDismissRequest = {}, confirmButton = {
            TextButton(onClick = {
                positiveCallback()
            }) {
                Text(positiveText)
            }
        }, dismissButton = {
            TextButton(onClick = {
                negativeCallback()
            }) {
                Text(negativeText)
            }
        })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    properties: ModalBottomSheetProperties = ModalBottomSheetDefaults.properties(),
    content: @Composable ColumnScope.() -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = { onDismissRequest.invoke() },
        properties = properties,
        sheetState = sheetState,
    ) {
        Column(modifier = Modifier.padding(bottom = getContentBottomPadding())) {
            content()
        }
    }
}

@Composable
private fun getContentBottomPadding(): Dp {
    val bottomPadding =
        WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding().value.toDouble()
    return if (bottomPadding == 0.0) {
        32.dp
    } else {
        WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    }
}