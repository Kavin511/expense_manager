package com.devstudio.expensemanager.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.devstudio.expensemanager.R
import com.devstudio.expensemanager.databinding.FragmentHomeBinding
import com.devstudio.expensemanager.db.models.Transactions
import com.devstudio.expensemanager.ui.transaction.TransactionActivity
import com.devstudio.expensemanager.ui.transaction.adapter.TransactionListAdapter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch

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
        viewLifecycleOwner.lifecycleScope.launch {
            val expenseSet = homeViewModel.transactions()
            expenseSet.observe(viewLifecycleOwner) { transactions ->
                binding.emptyTransactionsIntroductionText.visibility = if (transactions.isNotEmpty()) {
                        View.GONE
                    } else {
                        View.VISIBLE
                    }
                initialiseTransactionAdapter(transactions)
            }
        }
    }

    private fun initialiseTransactionAdapter(transactions: List<Transactions>) {
        val transactionListAdapter =
            TransactionListAdapter(requireContext(), transactions, onClick = { transaction ->
                editTransaction(transaction)
            }, onLongClick = {
                showTransactionLongPressOptions(it)
            })
        binding.transactionsList.adapter = transactionListAdapter
        binding.transactionsList.layoutManager = GridLayoutManager(context, 1)
    }

    private fun showTransactionLongPressOptions(it: Transactions) {
        val builder = MaterialAlertDialogBuilder(requireContext())
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
        val intent = Intent(requireContext(), TransactionActivity::class.java).apply {
            putExtra("id", transaction.id)
        }
        startActivity(intent)
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

}