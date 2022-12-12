import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ImportExport
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.devstudio.expensemanager.R
import com.devstudio.expensemanager.ui.viewmodel.HomeViewModel
import com.devstudio.utils.DateFormatter
import com.devstudioworks.uiComponents.theme.appColors
import java.util.Calendar

@Composable
fun TransactionSummary(paddingValues: PaddingValues) {
    val homeViewModel: HomeViewModel = viewModel()
    val expense = homeViewModel.totalExpenseAmount.collectAsState()
    val income = homeViewModel.totalIncomeAmount.collectAsState()
    Card(
        modifier = Modifier
            .padding(
                top = paddingValues.calculateTopPadding(),
                bottom = dimensionResource(id = R.dimen.default_padding),
                start = dimensionResource(id = R.dimen.default_padding),
                end = dimensionResource(id = R.dimen.default_padding)
            ),
    ) {
        Column(
            Modifier
                .fillMaxWidth(1f)
                .background(color = appColors.material.surfaceVariant)
                .padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "${DateFormatter().monthNames[Calendar.getInstance().get(Calendar.MONTH)-1]} month summary")
            Row(
                Modifier
                    .fillMaxWidth(1f),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = (Alignment.CenterHorizontally)) {
                    Icon(
                        imageVector = Icons.Rounded.ImportExport,
                        contentDescription = "Expenses",
                        tint = appColors.expenseIconTint,
                        modifier = Modifier.padding(dimensionResource(id = R.dimen.default_padding))
                    )
                    Text(text = "Total expense ${expense.value}")
                }
                Column(horizontalAlignment = (Alignment.CenterHorizontally)) {
                    Icon(
                        imageVector = Icons.Rounded.ImportExport,
                        contentDescription = "Income",
                        tint = appColors.incomeIconTint,
                        modifier = Modifier.padding(dimensionResource(id = R.dimen.default_padding))
                    )
                    Text(text = "Total income : ${income.value}")
                }
            }
        }
    }
}
