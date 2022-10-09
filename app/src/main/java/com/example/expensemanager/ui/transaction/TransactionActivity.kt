package com.example.expensemanager.ui.transaction

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.devstudioworks.customChipGroup.AdditionalChipListener
import com.example.expensemanager.R
import com.example.expensemanager.databinding.ActivityTransactionBinding
import com.example.expensemanager.ui.transaction.models.TransactionMode
import com.google.android.material.chip.Chip
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class TransactionActivity : AppCompatActivity() {

    private var _binding: ActivityTransactionBinding? = null
    private lateinit var transactionViewModel: TransactionViewModel
    private val binding
        get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        transactionViewModel = TransactionViewModel()
        initialiseTransactionType()
        initialiseTransactionKeyboard()
        initialiseNavigation()
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
            binding.categoryGroup.setAdditionalChipClickListener(object : AdditionalChipListener {
                override fun onAdditionalChipClick(view: View, context: Context) {
                }
            })
            transactionViewModel.transactionMode.value
            it.categoryList.forEachIndexed { index, value ->
                val chip = Chip(this)
                chip.text = value
                chip.id = index
                chip.isCheckable = true
                if (index == 0) {
                    chip.isChecked = true
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