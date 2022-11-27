package com.devstudio.expensemanager

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.AbsoluteCutCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.devstudio.expensemanager.databinding.ActivityMainBinding
import com.devstudio.expensemanager.db.models.Transactions
import com.devstudio.expensemanager.ui.home.Home
import com.devstudio.expensemanager.ui.home.HomeViewModel
import com.devstudio.expensemanager.ui.transaction.TransactionActivity
import com.devstudio.utils.DateFormatter
import com.devstudioworks.uiComponents.theme.MaterialTheme
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MainActivity : ComponentActivity() {

    private var transactions: List<Transactions> = listOf()
    private lateinit var binding: ActivityMainBinding
    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Home()
            }
        }
    }

    @OptIn(ExperimentalFoundationApi::class)
    @Preview
    @Composable
    fun TransactionPreview() {
        val transaction = Transactions(1, "2", 1.0, "222", "22", "111")
        val blockColor = if (transaction.transactionMode != "EXPENSE") {
            Color(0XFFE7FBE8)
        } else {
            Color(0XFFFCEEED)
        }
        Card(
            modifier = Modifier
                .padding(4.dp)
                .background(blockColor),
            shape = AbsoluteCutCornerShape(4.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(1F)
                    .padding(horizontal = dimensionResource(id = R.dimen.default_padding))
                    .combinedClickable(onClick = {
                        editTransaction(transaction)
                    }, onLongClick = {
                        showTransactionLongPressOptions(transaction)
                    })
                    .background(blockColor),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column() {
                    Text(text = transaction.amount.toString())
                    Text(text = transaction.category)
                }
                Column {
                    Text(text = DateFormatter().convertLongToDate(transaction.transactionDate.toLong()))
                    Text(text = transaction.note)
                }
            }
        }
    }

    private fun showTransactionLongPressOptions(it: Transactions) {
        val builder = MaterialAlertDialogBuilder(this)
        val options = arrayOf("Edit Transaction", "Delete Transaction")
        builder.setItems(options) { dialog, which ->
            if (which == 0) {
                editTransaction(it)
            } else if (which == 1) {
                homeViewModel.deleteTransaction(it)
            }
            dialog.dismiss()
        }
        builder.show()
    }

    private fun editTransaction(transaction: Transactions) {
        val intent = Intent(this, TransactionActivity::class.java).apply {
            putExtra("id", transaction.id)
        }
        startActivity(intent)
    }
}