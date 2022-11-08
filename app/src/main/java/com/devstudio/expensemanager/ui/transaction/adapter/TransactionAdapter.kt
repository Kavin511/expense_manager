package com.devstudio.expensemanager.ui.transaction.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.devstudio.expensemanager.R
import com.devstudio.expensemanager.db.models.Transactions
import com.devstudio.utils.TransactionUtils
import java.lang.String
import java.util.*

class TransactionListAdapter(
    val context: Context,
    var expenseSet: List<Transactions>,
    private val onClick: (Transactions) -> Unit,
    private val onLongClick: (Transactions) -> Unit,
) :
    RecyclerView.Adapter<TransactionListAdapter.ExpenseViewHolder>() {
    class ExpenseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val amount: TextView
        val transactionWrapper: CardView
        val category: TextView
        val date: TextView

        init {
            amount = view.findViewById(R.id.amount)
            category = view.findViewById(R.id.category)
            date = view.findViewById(R.id.date)
            transactionWrapper = view.findViewById(R.id.transaction_wrapper)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.transactions_item, parent, false)
        return ExpenseViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val amount = holder.amount
        val transactions = expenseSet[position]
        if (transactions.transactionMode == "EXPENSE") {
            amount.text = "- ₹".plus(transactions.amount.toString())
            holder.transactionWrapper.setCardBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.pink
                )
            )
        } else {
            amount.text = "₹".plus(transactions.amount.toString())
            holder.transactionWrapper.setCardBackgroundColor(
                ContextCompat.getColor(
                    context,
                    R.color.green
                )
            )
        }
        holder.date.text = TransactionUtils().convertLongToDate(transactions.transactionDate.toLong())
        holder.category.text = transactions.category
        holder.transactionWrapper.setOnClickListener {
            onClick(expenseSet[position])
        }
        holder.transactionWrapper.setOnLongClickListener {
            onLongClick(expenseSet[position])
            return@setOnLongClickListener true
        }
    }

    private fun convertLongToDate(time: Long): kotlin.String? {
        val cal = Calendar.getInstance()
        val monthNames =
            listOf("Jan", "Feb", "Mar", "Apr", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
        cal.timeInMillis = time
        return String.format(
            "%s %s, %s",
            monthNames[cal[Calendar.MONTH] - 1],
            cal[Calendar.DAY_OF_MONTH],
            cal[Calendar.YEAR],
        )
    }

    override fun getItemCount(): Int {
        return expenseSet.size
    }
}
