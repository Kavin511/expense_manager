import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ImportExport
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.devstudio.expensemanager.ui.viewmodel.HomeViewModel
import com.devstudio.utils.formatters.DateFormatter
import com.devstudio.utils.formatters.StringFormatter.roundOffDecimal
import com.devstudioworks.ui.theme.appColors
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.*

@Preview(name = "summary", backgroundColor = 0xFFFFFFFF)
@Composable
fun TransactionSummary() {
    val paddingValues = PaddingValues(7.dp)
    val homeViewModel: HomeViewModel = viewModel()
    val expense = homeViewModel.totalExpenseAmount.collectAsState()
    val income = homeViewModel.totalIncomeAmount.collectAsState()
    val textColor = appColors.material.onTertiaryContainer
    Card(
        modifier = Modifier
            .padding(7.dp)
    ) {
        Column(
            Modifier
                .background(color = appColors.material.tertiaryContainer)
                .fillMaxWidth(1f)
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${
                    DateFormatter().monthNames[Calendar.getInstance().get(Calendar.MONTH)]
                } month summary",
                color = textColor,
                style = androidx.compose.material3.Typography().bodyMedium
            )
            Row(
                Modifier
                    .fillMaxWidth(1f),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = (Alignment.CenterHorizontally)) {
                    Icon(
                        imageVector = Icons.Rounded.ImportExport,
                        contentDescription = "Expenses",
                        tint = appColors.transactionExpenseColor,
                        modifier = Modifier.padding(dimensionResource(id = com.devstudioworks.core.ui.R.dimen.default_padding))
                    )
                    Text(text = "Total expense ${roundOffDecimal(expense.value)}", color = textColor)
                }
                Column(horizontalAlignment = (Alignment.CenterHorizontally)) {
                    Icon(
                        imageVector = Icons.Rounded.ImportExport,
                        contentDescription = "Income",
                        tint = appColors.transactionIncomeColor,
                        modifier = Modifier.padding(dimensionResource(id = com.devstudioworks.core.ui.R.dimen.default_padding))
                    )
                    Text(
                        text = "Total income : ${roundOffDecimal(income.value)}",
                        style = androidx.compose.material3.Typography().bodyMedium,
                        color = textColor
                    )
                }
            }
        }
    }
}
