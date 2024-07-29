package com.devstudio.expensemanager.presentation.home.composables

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import com.devstudio.designSystem.DEFAULT_CARD_CORNER_RADIUS
import com.devstudio.designSystem.appColors

@Preview(name = "")
@Composable
fun HomeSnackBar(
    snackBarHostState: SnackbarHostState = SnackbarHostState(),
) {
    Snackbar(
        modifier = Modifier.padding(DEFAULT_CARD_CORNER_RADIUS).fillMaxWidth().wrapContentSize(),
        action = {
            TextButton(onClick = {
                snackBarHostState.currentSnackbarData?.performAction()
            }) {
                Text(text = snackBarHostState.currentSnackbarData?.visuals?.actionLabel ?: "", color = appColors.material.inversePrimary)
            }
        },
    ) {
        Text(
            text = snackBarHostState.currentSnackbarData?.visuals?.message ?: "",
        )
    }
}
