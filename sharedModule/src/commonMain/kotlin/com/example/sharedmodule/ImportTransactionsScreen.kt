package com.example.sharedmodule

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.devstudio.theme.appColors

@Composable
fun ImportTransactions() {
    var skipFirstRow by remember { mutableStateOf(false) }
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = appColors.material.surface
    ) {
        LazyColumn(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { Header() }
            item { ColumnSelection() }
            item { ExcelFileUpload() }
            item { ColumnMapping() }
            item { ImportOptions(skipFirstRow) { skipFirstRow = it } }
            item { ActionButtons() }
        }
    }
}

@Composable
fun Header() {
    Text(
        text = "Import Transactions",
        style = MaterialTheme.typography.headlineMedium,
        color = Color(0xFF3F51B5), // Deep blue
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun ColumnSelection() {
    Column {
        Text(
            text = "Column Selection",
            style = MaterialTheme.typography.titleMedium,
            color = Color(0xFF212121) // Dark gray
        )
        Spacer(modifier = Modifier.height(8.dp))
        CheckboxItem("Date")
        CheckboxItem("Description")
        CheckboxItem("Amount")
        CheckboxItem("Type (Expense/Income)")
        CheckboxItem("Category (optional)")
        TextButton(onClick = { /* Handle edit columns */ }) {
            Text("Edit Columns", color = Color(0xFF2196F3)) // Blue for link
        }
    }
}

@Composable
fun CheckboxItem(text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Checkbox(
            checked = false,
            onCheckedChange = { /* Handle checkbox change */ },
            colors = CheckboxDefaults.colors(
                checkedColor = Color(0xFF3F51B5), // Deep blue
                uncheckedColor = Color(0xFF757575) // Medium gray
            )
        )
        Text(text, color = Color(0xFF212121)) // Dark gray
    }
}

@Composable
fun ExcelFileUpload() {
    Column {
        Text(
            text = "Excel File Upload",
            style = MaterialTheme.typography.titleMedium,
            color = Color(0xFF212121) // Dark gray
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = { /* Handle browse */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F51B5)) // Deep blue
            ) {
                Text("Browse...")
            }
            Button(
                onClick = { /* Handle file upload */ },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F51B5)) // Deep blue
            ) {
                Text("File Upload")
            }
        }
    }
}

@Composable
fun ColumnMapping() {
    Column {
        Text(
            text = "Column Mapping",
            style = MaterialTheme.typography.titleMedium,
            color = Color(0xFF212121) // Dark gray
        )
        Spacer(modifier = Modifier.height(8.dp))
        Table()
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { /* Handle save mapping */ },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F51B5)) // Deep blue
        ) {
            Text("Save Mapping")
        }
    }
}

@Composable
fun Table() {
    val columns = listOf("Excel Column", "System Column")
    val rows = listOf("Date", "Description", "Amount", "Type", "Category")

    Column(
        modifier = Modifier
            .background(Color.White)
            .padding(1.dp)
    ) {
        // Header
        Row(modifier = Modifier.background(Color(0xFFE0E0E0))) { // Light gray
            columns.forEach { column ->
                TableCell(text = column, weight = 1f, header = true)
            }
        }
        // Rows
        rows.forEach { row ->
            Row(modifier = Modifier.background(Color.White)) {
                TableCell(text = "(Dropdown)", weight = 1f)
                TableCell(text = row, weight = 1f)
            }
        }
    }
}

@Composable
fun TableCell(text: String, weight: Float, header: Boolean = false) {
    Text(
        text = text,
        modifier = Modifier
            .padding(8.dp),
        fontWeight = if (header) FontWeight.Bold else FontWeight.Normal,
        color = Color(0xFF212121) // Dark gray
    )
}

@Composable
fun ImportOptions(skipFirstRow: Boolean, onSkipFirstRowChanged: (Boolean) -> Unit) {
    Column {
        Text(
            text = "Import Options",
            style = MaterialTheme.typography.titleMedium,
            color = Color(0xFF212121) // Dark gray
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = skipFirstRow,
                onCheckedChange = onSkipFirstRowChanged,
                colors = CheckboxDefaults.colors(
                    checkedColor = Color(0xFF3F51B5), // Deep blue
                    uncheckedColor = Color(0xFF757575) // Medium gray
                )
            )
            Text("Skip first row", color = Color(0xFF212121)) // Dark gray
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { /* Handle show preview */ },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F51B5)) // Deep blue
        ) {
            Text("Show preview")
        }
    }
}

@Composable
fun ActionButtons() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(
            onClick = { /* Handle import */ },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)) // Green
        ) {
            Text("Import")
        }
        Button(
            onClick = { /* Handle cancel */ },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F51B5)) // Deep blue
        ) {
            Text("Cancel")
        }
    }
}        // Footer