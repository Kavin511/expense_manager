package com.devstudio.designSystem.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.devstudio.designSystem.model.AppColor

@Composable
fun ExpressWalletFab(
    appColors: AppColor,
    contentDescription: String,
    function: () -> Unit,
) {
    FloatingActionButton(
        onClick = { function.invoke() },
        modifier = Modifier.padding(start = 6.dp, end = 6.dp, top = 4.dp, bottom = 4.dp),
        containerColor = appColors.material.secondary,
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = contentDescription,
            tint = appColors.material.onSecondary,
        )
    }
}
