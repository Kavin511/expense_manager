package com.devstudio.expensemanager.ui.transaction

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.devstudio.expensemanager.R
import com.devstudio.expensemanager.databinding.ActivityTransactionBinding
import com.devstudio.expensemanager.db.models.Transactions
import com.devstudio.expensemanager.ui.transaction.models.TransactionMode
import com.devstudio.expensemanager.ui.transaction.viewmodels.TransactionViewModel
import com.devstudio.expensemanager.ui.transaction.viewmodels.TransactionViewModelFactory
import com.devstudio.expensemanager.utils.TransactionInputFormula
import com.google.android.material.chip.Chip
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*

class TransactionActivity : AppCompatActivity() {

    private var _binding: ActivityTransactionBinding? = null
    private val transactionViewModel by viewModels<TransactionViewModel> {
        TransactionViewModelFactory(
            (application as com.devstudio.expensemanager.ExpenseManagerApplication).repository
        )
    }
    var selectedCategoryIndex = 0
    private val binding
        get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initialiseTransactionType()
        initialiseTransactionKeyboard()
        fetchAndUpdateTransactionToBeEdited()
        initialiseNavigation()
        updateCategoryBasedOnTransactionTypeSelection()
        initialiseSaveTransactionFlow()
        hideKeyboardOnFocusChange()
        binding.keyboard.amountText.showSoftInputOnFocus = false
    }

    private fun initialiseSaveTransactionFlow() {
        binding.keyboard.saveTransaction.setOnClickListener {
            lifecycleScope.launch {
                val oldTransaction = transactionViewModel.transaction.value
                if (transactionViewModel.isEditingOldTransaction.value == true && oldTransaction != null) {
                    updateOldTransaction(oldTransaction)
                } else {
                    createNewTransaction()
                }
            }
            finish()
        }
    }

    private suspend fun createNewTransaction() {
        val transaction = Transactions(
            id = Calendar.getInstance().time.time,
            amount = getTransactionAmount(),
            note = binding.noteText.text.toString(),
            transactionMode = transactionViewModel.transactionType.value.toString(),
            transactionDate = Calendar.getInstance().time.time.toString(),
            category = transactionViewModel.transactionType.value.categoryList[selectedCategoryIndex]
        )
        transactionViewModel.insertTransaction(transaction)
    }

    private fun getTransactionAmount(): Double {
        return TransactionInputFormula().calculate(binding.keyboard.amountText.text.toString())
    }

    private suspend fun updateOldTransaction(oldTransaction: Transactions) {
        oldTransaction.apply {
            amount =
                TransactionInputFormula().calculate(binding.keyboard.amountText.text.toString())
            note = binding.noteText.text.toString()
            transactionMode = transactionViewModel.transactionType.value.toString()
            transactionDate = Calendar.getInstance().time.time.toString()
            category =
                transactionViewModel.transactionType.value.categoryList[selectedCategoryIndex]
        }
        transactionViewModel.updateTransaction(oldTransaction)
    }

    private fun hideKeyboardOnFocusChange() {
        binding.keyboard.amountText.setOnFocusChangeListener { view, b ->
            val imm: InputMethodManager =
                getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun updateCategoryBasedOnTransactionTypeSelection() {
        lifecycleScope.launch {
            transactionViewModel.transaction.collectLatest {
                if (it != null) {
                    binding.keyboard.amountText.editableText.insert(0, it.amount.toString())
                    binding.noteText.setText(it.note)
                    if (it.transactionMode == "EXPENSE") {
                        transactionViewModel.transactionType.value = TransactionMode.EXPENSE
                        binding.transactionMode.check(R.id.expense_mode)
                        selectedCategoryIndex =
                            TransactionMode.EXPENSE.categoryList.indexOf(it.category)
                        binding.categoryGroup.check(selectedCategoryIndex)
                    } else {
                        transactionViewModel.transactionType.value = TransactionMode.INCOME
                        binding.transactionMode.check(R.id.income_mode)
                        selectedCategoryIndex =
                            TransactionMode.INCOME.categoryList.indexOf(it.category)
                        binding.categoryGroup.check(selectedCategoryIndex)
                    }
                }
            }
        }
    }

    private fun fetchAndUpdateTransactionToBeEdited() {
        lifecycleScope.launchWhenCreated {
            val id = intent.getLongExtra("id", 0)
            transactionViewModel.getTransactionById(id)
        }
        transactionViewModel.isEditingOldTransaction.observe(this) {
            binding.keyboard.saveTransaction.text = getString(R.string.update_transaction)
        }
    }

    private fun initialiseTransactionType() {
        binding.transactionMode.addOnButtonCheckedListener { group, checkedId, isChecked ->
            when (checkedId) {
                R.id.expense_mode -> {
                    if (isChecked) {
                        selectedCategoryIndex = 0
                        transactionViewModel.transactionType.value = TransactionMode.EXPENSE
                    }
                }
                R.id.income_mode -> {
                    if (isChecked) {
                        selectedCategoryIndex = 0
                        transactionViewModel.transactionType.value = TransactionMode.INCOME
                    }
                }
            }
        }
        lifecycleScope.launch {
            initialiseTransactionCategory()
        }
    }

    private suspend fun initialiseTransactionCategory() {
        transactionViewModel.transactionType.collectLatest {
            binding.categoryGroup.removeAllViews()
            transactionViewModel.transactionType.value
            it.categoryList.forEachIndexed { index, value ->
                val chip = Chip(this)
                chip.text = value
                chip.id = index
                chip.isCheckable = true
                if (selectedCategoryIndex == index) {
                    chip.isChecked = true
                }
                chip.setOnClickListener {
                    selectedCategoryIndex = index
                }
                binding.categoryGroup.addView(chip)
            }
        }
    }

    private fun initialiseNavigation() {
        binding.topAppBar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun initialiseTransactionKeyboard() {
        val transactionKeyboard = TransactionKeyboard(
            baseContext,
            binding.keyboard.amountText.editableText,
            binding.keyboard
        )
        transactionKeyboard.initialiseListeners()
        transactionViewModel.viewModelScope.launch {
            binding.keyboard.amountText.selectionPosition.collect {
                transactionKeyboard.selectionPosition = it
            }
        }
        binding.keyboard.amountText.requestFocus()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}