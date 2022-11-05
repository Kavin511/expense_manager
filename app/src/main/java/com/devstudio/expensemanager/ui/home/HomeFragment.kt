package com.devstudio.expensemanager.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.devstudio.expensemanager.R
import com.devstudio.expensemanager.databinding.FragmentHomeBinding
import com.devstudio.expensemanager.db.models.Transactions
import com.devstudio.expensemanager.ui.transaction.TransactionActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import java.lang.String
import java.util.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var homeViewModel:HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
         homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        initialiseEmptyTransactionsMessage()
        initialiseTransactionList()
        return root
    }

    fun initialiseEmptyTransactionsMessage() {
        viewLifecycleOwner.lifecycleScope.launch {
            homeViewModel.text.collect {
                binding.emptyTransactionsIntroductionText.text = it
            }
        }
    }

    fun initialiseTransactionList() {
        lifecycleScope.launch {
            val expenseSet = homeViewModel.transactions()
            expenseSet.observe(viewLifecycleOwner) {
                if (it.isNotEmpty()) {
                    binding.emptyTransactionsIntroductionText.visibility = View.GONE
                }
                val transactionListAdapter =
                    TransactionListAdapter(requireContext(), it) { transaction ->
                        val intent =
                            Intent(requireContext(), TransactionActivity::class.java).apply {
                                putExtra("id", transaction.id)
                            }
                        startActivity(intent)
                    }
                binding.transactionsList.adapter = transactionListAdapter
                binding.transactionsList.layoutManager = GridLayoutManager(context, 1)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val addTransactions: FloatingActionButton = binding.addTransaction
        addTransactions.setOnClickListener {
            onPrimaryNavigationFragmentChanged(true)
            findNavController().navigate(R.id.transactionActivity)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    internal class TransactionListAdapter(
        val context: Context,
        var expenseSet: List<Transactions>,
        private val onClick: (Transactions) -> Unit
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
            holder.date.text = convertLongToDate(transactions.transactionDate.toLong())
            holder.category.text = transactions.category
            holder.transactionWrapper.setOnClickListener {
                onClick(expenseSet[position])
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
}