package com.devstudio.sharedmodule.importData.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.devstudio.designSystem.appColors


@Composable
fun CSVCell(
    text: String,
    header: Boolean,
    even: Boolean,
) {
    val csvCellModifier =
        Modifier.width(140.dp).border(1.dp, appColors.material.onTertiaryContainer).then(
            if (header) {
                Modifier.background(appColors.material.secondaryContainer)
            } else if (even) {
                Modifier.background(appColors.material.surfaceVariant)
            } else {
                Modifier
            }
        ).padding(all = 4.dp)
    Text(
        modifier = csvCellModifier,
        text = text.ifEmpty { "" },
        color = if (header) {
            appColors.material.onSecondaryContainer
        } else if (even) {
            appColors.material.onSurfaceVariant
        } else {
            LocalContentColor.current
        },
        fontWeight = if (header) FontWeight.Bold else FontWeight.Normal,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
    )
}
