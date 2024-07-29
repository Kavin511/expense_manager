package com.devstudio.designSystem.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetDefaults
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun MaterialAlert(
    content: String,
    positiveText: String,
    negativeText: String,
    positiveCallback: () -> Unit,
    negativeCallback: () -> Unit,
) {
    AlertDialog(
//        icon = {
//            Icon(icon, contentDescription = "Example Icon")
//        },
        text = {
            Text(text = content)
        },
//        text = {
//            Text(text = dialogText)
//        },
        onDismissRequest = {
        },
        confirmButton = {
            TextButton(
                onClick = {
                    positiveCallback()
                }
            ) {
                Text(positiveText)
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    negativeCallback()
                }
            ) {
                Text(negativeText)
            }
        }
    )
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
    val contentBottomPadding = if (WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding().value.toDouble() == 0.0) {
        32.dp
    } else {
        WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    }
    ModalBottomSheet(
        onDismissRequest = { onDismissRequest.invoke() },
        properties = properties,
        sheetState = sheetState,
        modifier = modifier.padding(bottom = contentBottomPadding),
    ) {
        Column(modifier = Modifier.padding(bottom = contentBottomPadding)) {
            content()
        }
    }
}