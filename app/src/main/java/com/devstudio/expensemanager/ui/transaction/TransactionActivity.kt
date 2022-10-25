package com.devstudio.expensemanager.ui.transaction

import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.devstudio.expensemanager.ExpenseManagerApplication
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
    val binding
        get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initialiseTransactionType()
        initialiseTransactionKeyboard()
        lifecycleScope.launchWhenCreated {
            val id = intent.getLongExtra("id", 0)
            transactionViewModel.getTransactionById(id)
        }
        transactionViewModel.isEditingOldTransaction.observe(this) {
            binding.keyboard.saveTransaction.setText(getString(R.string.update_transaction))
        }
        initialiseNavigation()
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
        binding.keyboard.saveTransaction.setOnClickListener {
            lifecycleScope.launch {
                val oldTransaction = transactionViewModel.transaction.value
                if (transactionViewModel.isEditingOldTransaction.value == true && oldTransaction !=null) {
                    oldTransaction.apply {
                        amount = TransactionInputFormula().calculate(binding.keyboard.amountText.text.toString())
                                .toDouble()
                        note = binding.noteText.text.toString()
                        transactionMode = transactionViewModel.transactionType.value.toString()
                        transactionDate = Calendar.getInstance().time.time.toString()
                        category =
                            transactionViewModel.transactionType.value.categoryList[selectedCategoryIndex]
                    }
                    transactionViewModel.updateTransaction(oldTransaction)
                } else {
                    val transaction = Transactions(
                        id = Calendar.getInstance().time.time,
                        amount = binding.keyboard.amountText.text.toString().toDouble(),
                        note = binding.noteText.text.toString(),
                        transactionMode = transactionViewModel.transactionType.value.toString(),
                        transactionDate = Calendar.getInstance().time.time.toString(),
                        category = transactionViewModel.transactionType.value.categoryList[selectedCategoryIndex]
                    )
                    transactionViewModel.insertTransaction(transaction)
                }
            }
            finish()
        }
        binding.keyboard.amountText.setOnFocusChangeListener { view, b ->
            val imm: InputMethodManager =
                getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
        binding.keyboard.amountTextWrapper.setEndIconOnClickListener {
            binding.keyboard.amountText.dispatchKeyEvent(
                KeyEvent(
                    KeyEvent.ACTION_DOWN,
                    KeyEvent.KEYCODE_DEL
                )
            )
            binding.keyboard.amountText.editableText.apply {
                if (this.isEmpty()) {
                    this.append("0")
                }
            }
        }
        binding.keyboard.amountTextWrapper.setEndIconOnLongClickListener {
            binding.keyboard.amountText.dispatchKeyEvent(
                KeyEvent(
                    KeyEvent.ACTION_DOWN,
                    KeyEvent.KEYCODE_CLEAR
                )
            )
        }
        binding.keyboard.amountText.showSoftInputOnFocus = false
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
        val transactionKeyboard =
            TransactionKeyboard(
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