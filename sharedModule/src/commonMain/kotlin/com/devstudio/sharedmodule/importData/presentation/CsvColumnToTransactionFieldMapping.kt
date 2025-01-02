package com.devstudio.sharedmodule.importData.presentation

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devstudio.designSystem.appColors
import com.devstudio.sharedmodule.importData.model.CSVRow
import com.devstudio.sharedmodule.importData.model.TransactionField
import com.devstudio.sharedmodule.importData.model.TransactionFieldType.*
import com.devstudio.sharedmodule.utils.getWidthRatio
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun FieldMappingRow(
    header: CSVRow = CSVRow(listOf()),
    transactionField: TransactionField = TransactionField("", "", type = Amount),
    metaData: (@Composable () -> Unit)? = null,
) {
    val shape = RoundedCornerShape(8.dp)
    Column(
        modifier = Modifier.padding(vertical = 8.dp).then(
            if (transactionField.selectedFieldIndex.value != -1) {
                Modifier.border(width = 2.dp, color = appColors.material.primary, shape = shape)
            } else {
                Modifier.border(width = 2.dp, color = appColors.material.error, shape = shape)
            }
        ).shadow(2.dp, shape).padding(8.dp)
    ) {
        val defaultModifier = Modifier.padding(bottom = 8.dp)
        Text(
            text = transactionField.name,
            modifier = defaultModifier,
            lineHeight = 24.sp,
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            text = transactionField.description, modifier = defaultModifier,
            style = MaterialTheme.typography.bodyLarge,
        )
        Text(
            text = "Choose suitable column:", modifier = defaultModifier,
            style = MaterialTheme.typography.titleSmall,
        )
        LazyRow {
            itemsIndexed(header.values) { index, value ->
                FilterChip(
                    selected = transactionField.selectedFieldIndex.value == index,
                    onClick = {
                        transactionField.selectedFieldIndex.value = index
                        transactionField.csvHeader = value
                    },
                    modifier = Modifier.padding(end = 8.dp),
                    label = {
                        Text(
                            value,
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    })
            }
        }
        metaData?.invoke()
    }
}

@Composable
fun AdditionalMappingInfoRow(transactionField: TransactionField, type: String) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth(getWidthRatio()).padding(8.dp),
    ) {
        val inputValue = TextFieldValue("")
        Text(
            type,
            modifier = Modifier
                .weight(0.3f)
                .wrapContentHeight()
        )
        TextField(
            value = inputValue,
            onValueChange = {
                transactionField.additionalInfo = it.text
            },
            modifier = Modifier
                .weight(0.7f)
                .wrapContentHeight()
        )
    }
}

@Preview
@Composable
fun CsvFileMapperPreview() {
    FieldMappingRow()
}