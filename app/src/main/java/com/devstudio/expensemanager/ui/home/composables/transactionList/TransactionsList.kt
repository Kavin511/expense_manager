import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.devstudio.expensemanager.R
import com.devstudio.expensemanager.db.models.Transactions
import com.devstudio.expensemanager.ui.home.composables.transactionList.TransactionItem
import com.devstudio.expensemanager.ui.transaction.acivity.TransactionActivity
import com.devstudio.expensemanager.ui.viewmodel.HomeViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

@Composable
fun TransactionsList(
    paddingValues: PaddingValues,
    homeViewModel: HomeViewModel = viewModel()
) {
    val transactions: List<Transactions> =
        homeViewModel.getTransactions().observeAsState().value ?: emptyList()
    LazyColumn(
        modifier = Modifier.padding(dimensionResource(id = R.dimen.default_padding)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(items = transactions) {
            Row {
                TransactionItem(transaction = it)
            }
        }
    }

}

fun showTransactionLongPressOptions(
    context: Context,
    it: Transactions,
    homeViewModel: HomeViewModel
) {
    val builder = MaterialAlertDialogBuilder(context)
    val options = arrayOf("Edit Transaction", "Delete Transaction")
    builder.setItems(options) { dialog, which ->
        if (which == 0) {
            editTransaction(context, it)
        } else if (which == 1) {
            homeViewModel.deleteTransaction(it)
        }
        dialog.dismiss()
    }
    builder.show()
}

fun editTransaction(context: Context, transaction: Transactions) {
    val intent = Intent(context, TransactionActivity::class.java).apply {
        putExtra("id", transaction.id)
    }
    context.startActivity(intent)
}
