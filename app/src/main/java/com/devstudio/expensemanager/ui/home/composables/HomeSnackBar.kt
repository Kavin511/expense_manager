package com.devstudio.expensemanager.ui.home.composables

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

@Preview(name = "")
@Composable
fun HomeSnackBar(
    snackBarHostState: SnackbarHostState = SnackbarHostState()
) {
    Snackbar(
        modifier = Modifier.padding(dimensionResource(id = com.devstudioworks.core.ui.R.dimen.default_padding)).fillMaxWidth().wrapContentSize(),
        action = {
            TextButton(onClick = {
                snackBarHostState.currentSnackbarData?.performAction()
            }) {
                Text(text = snackBarHostState.currentSnackbarData?.visuals?.actionLabel ?: "")
            }
        },
    ) {
        Text(
            text = snackBarHostState.currentSnackbarData?.visuals?.message ?: ""
        )
    }

}