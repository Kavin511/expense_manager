package com.devstudio.designSystem.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.sp


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
    properties: ModalBottomSheetProperties = ModalBottomSheetDefaults.properties,
    content: @Composable ColumnScope.() -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = { onDismissRequest.invoke() },
        modifier = modifier,
        properties = properties,
        sheetState = sheetState,
    ) {
        content()
    }
}
