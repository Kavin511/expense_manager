package com.devstudio.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Divider
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.devstudio.designSystem.appColors
import com.devstudio.model.models.ExpressWalletAppState
import com.devstudio.profile.composables.Label
import com.devstudio.utils.formatters.DateFormatter
import java.util.Calendar

@Composable
fun BudgetScreen() {
    Column {
        var budget by remember {
            mutableStateOf("")
        }
        OutlinedTextField(
            value = budget,
            onValueChange = {
                budget = it
            },
            keyboardOptions = KeyboardOptions(
                autoCorrect = false,
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Decimal,
            ),
        )

        FilledTonalButton(
            onClick = { /*TODO*/ },
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = appColors.material.primaryContainer,
                    shape = RoundedCornerShape(10.dp),
                ),
        ) {
            Text(text = "Add")
        }
    }
}

@Composable
private fun BudgetPanel(navController: NavHostController) {
    Column {
        Label(
            "Budget",
        )
        Text(
            text = "${
                DateFormatter.monthNames[
                    Calendar.getInstance().get(
                        Calendar.MONTH,
                    ),
                ]
            } month budget",
        )
        Row {
            Text(text = "Expense")
            Text(text = "0 Left")
        }
        TextButton(onClick = { navController.navigate(ExpressWalletAppState.BudgetScreen.route) }) {
            Text(text = "Set up", color = appColors.material.inversePrimary)
        }
        Divider()
    }
}
