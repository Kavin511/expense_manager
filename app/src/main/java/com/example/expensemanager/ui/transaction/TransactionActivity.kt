package com.example.expensemanager.ui.transaction

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.example.expensemanager.ExpenseManagerApplication
import com.example.expensemanager.R
import com.example.expensemanager.databinding.ActivityTransactionBinding
import com.example.expensemanager.db.models.Transactions
import com.example.expensemanager.ui.transaction.models.TransactionMode
import com.example.expensemanager.ui.transaction.viewmodels.TransactionViewModel
import com.example.expensemanager.ui.transaction.viewmodels.TransactionViewModelFactory
import com.google.android.material.chip.Chip
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*

class TransactionActivity : AppCompatActivity() {

    private var _binding: ActivityTransactionBinding? = null
    private val transactionViewModel by viewModels<TransactionViewModel> {
        TransactionViewModelFactory(
            (application as ExpenseManagerApplication).repository
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
        initialiseNavigation()

        binding.keyboard.saveTransaction.setOnClickListener {
            lifecycleScope.launch {
                val transaction = Transactions(
                    id = Calendar.getInstance().time.time,
                    amount = binding.amountText.text.toString().toDouble(),
                    note = binding.noteText.text.toString(),
                    transactionMode = transactionViewModel.transactionMode.value.toString(),
                    transactionDate = Calendar.getInstance().time.time.toString(),
                    category = transactionViewModel.transactionMode.value.categoryList[selectedCategoryIndex]
                )
                transactionViewModel.insertTransaction(transaction)
            }
            finish()
        }
        binding.amountText.setOnFocusChangeListener { view, b ->
            val imm: InputMethodManager =
                getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
        binding.amountText.showSoftInputOnFocus = false
    }

    private fun initialiseTransactionType() {
        binding.transactionMode.addOnButtonCheckedListener { group, checkedId, isChecked ->
            when (checkedId) {
                R.id.expense_mode -> {
                    if (isChecked) {
                        transactionViewModel.transactionMode.value = TransactionMode.EXPENSE
                    }
                }
                R.id.income_mode -> {
                    if (isChecked) {
                        transactionViewModel.transactionMode.value = TransactionMode.INCOME
                    }
                }
            }
        }
        lifecycleScope.launch {
            initialiseTransactionCategory()
        }
    }

    private suspend fun initialiseTransactionCategory() {
        transactionViewModel.transactionMode.collectLatest {
            binding.categoryGroup.removeAllViews()
            transactionViewModel.transactionMode.value
            it.categoryList.forEachIndexed { index, value ->
                val chip = Chip(this)
                chip.text = value
                chip.id = index
                chip.isCheckable = true
                if (index == 0) {
                    chip.isChecked = true
                    selectedCategoryIndex = 0
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
            TransactionKeyboard(baseContext, binding.amountText.editableText, binding.keyboard)
        transactionKeyboard.initialiseListeners()
        transactionViewModel.viewModelScope.launch {
            binding.amountText.selectionPosition.collect {
                transactionKeyboard.selectionPosition = it
            }
        }
        binding.amountText.requestFocus()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}

class CustomEditText(context: Context, attrs: AttributeSet) :
    TextInputEditText(context, attrs) {
    private var _selectionPosition: MutableStateFlow<Int>? = null
    val selectionPosition: StateFlow<Int>

    init {
        _selectionPosition = MutableStateFlow(0)
        selectionPosition = _selectionPosition as MutableStateFlow<Int>
    }

    override fun onSelectionChanged(selStart: Int, selEnd: Int) {
        super.onSelectionChanged(selStart, selEnd)
        runBlocking {
            launch {
                if (_selectionPosition != null) {
                    _selectionPosition?.value = selStart
                }
            }
        }
    }
}