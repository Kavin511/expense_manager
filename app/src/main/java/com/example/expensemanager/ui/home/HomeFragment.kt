package com.example.expensemanager.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.expensemanager.R
import com.example.expensemanager.databinding.FragmentHomeBinding
import com.example.expensemanager.db.models.Transactions
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val emptyTransactionsIntroductionText: TextView = binding.emptyTransactionsIntroductionText
        viewLifecycleOwner.lifecycleScope.launch {
            homeViewModel.text.collect {
                emptyTransactionsIntroductionText.text = it
            }
        }
        lifecycleScope.launch {
            val expenseSet = homeViewModel.transactions()
            if (expenseSet.isNotEmpty()) {
                emptyTransactionsIntroductionText.visibility = View.GONE
            }
            expenseSet.apply {
                val transactionListAdapter = TransactionListAdapter(this)
                binding.transactionsList.adapter = transactionListAdapter
                binding.transactionsList.layoutManager = GridLayoutManager(context,1)
            }
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val addTransactions: ExtendedFloatingActionButton = binding.addTransaction
        addTransactions.setOnClickListener {
            onPrimaryNavigationFragmentChanged(true)
            findNavController().navigate(R.id.transactionFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    internal class TransactionListAdapter(var expenseSet: List<Transactions>) :
        RecyclerView.Adapter<TransactionListAdapter.ExpenseViewHolder>() {
        class ExpenseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val expenseAmount: TextView

            init {
                expenseAmount = view.findViewById(R.id.transaction_amount)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.transactions_item, parent, false)
            return ExpenseViewHolder(view)
        }

        override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
            holder.expenseAmount.text = expenseSet[position].amount.toString()
        }

        override fun getItemCount(): Int {
            return expenseSet.size
        }
    }
}