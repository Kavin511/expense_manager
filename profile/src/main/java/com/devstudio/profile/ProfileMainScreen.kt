package com.devstudio.account

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Category
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalLayoutApi::class)
@Preview
@Composable
fun ProfileMainScreen() {

    FlowRow(modifier = Modifier.fillMaxWidth()) {
        Column {
            Image(imageVector = Icons.TwoTone.Category, contentDescription = "Category")
            Text(text = "")
            Text(text = "Category")
        }
    }
}

