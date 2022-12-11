package com.devstudio.expensemanager.ui.transaction.acivity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.devstudio.expensemanager.R
import com.devstudio.expensemanager.databinding.ActivityTransactionBinding
import com.devstudio.expensemanager.db.models.Transactions
import com.devstudio.expensemanager.model.TransactionMode
import com.devstudio.expensemanager.ui.transaction.uicomponents.TransactionKeyboard
import com.devstudio.expensemanager.ui.transaction.viewmodels.TransactionViewModel
import com.devstudio.expensemanager.ui.transaction.viewmodels.TransactionViewModelFactory
import com.devstudio.utils.DateFormatter
import com.devstudio.utils.formulas.TransactionInputFormula
import com.google.android.material.chip.Chip
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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
        setSupportActionBar(binding.topAppBar)
        setTheme(R.style.AppTheme)
        initialiseTransactionType()
        initialiseTransactionKeyboard()
        fetchAndUpdateTransactionToBeEdited()
        initialiseNavigation()
        initialiseSaveTransactionFlow()
        hideKeyboardOnFocusChange()
        initialiseTransactionDateClickListener()
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

    companion object {
        var selectedDate = Calendar.getInstance().time.time.toString()
    }

    private suspend fun createNewTransaction() {
        val transaction = Transactions(
            id = Calendar.getInstance().time.time,
            amount = getTransactionAmount(),
            note = binding.noteText.text.toString(),
            transactionMode = transactionViewModel.transactionType.value.toString(),
            transactionDate = selectedDate,
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
            transactionDate = selectedDate
            category =
                transactionViewModel.transactionType.value.categoryList[selectedCategoryIndex]
        }
        transactionViewModel.updateTransaction(oldTransaction)
    }

    private fun hideKeyboardOnFocusChange() {
        binding.keyboard.amountText.setOnFocusChangeListener { view, _ ->
            val imm: InputMethodManager =
                getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun fetchAndUpdateTransactionToBeEdited() {
        lifecycleScope.launchWhenCreated {
            val id = intent.getLongExtra("id", 0)
            transactionViewModel.getAndUpdateTransactionById(id)
            updateCategoryBasedOnTransactionTypeSelection()
        }
        transactionViewModel.isEditingOldTransaction.observe(this) {
            if (it) {
                binding.keyboard.saveTransaction.text = getString(R.string.update_transaction)
            }
        }
    }

    private suspend fun updateCategoryBasedOnTransactionTypeSelection() {
        transactionViewModel.transaction.collectLatest {
            if (it != null) {
                binding.keyboard.amountText.editableText.insert(0, it.amount.toString())
                binding.noteText.setText(it.note)
                binding.transactionDate.text =
                    DateFormatter().convertLongToDate(it.transactionDate.toLong())
                selectedDate = it.transactionDate
                if (it.transactionMode == "EXPENSE") {
                    transactionViewModel.transactionType.value = TransactionMode.EXPENSE
                    binding.transactionMode.check(R.id.expense_mode)
                    selectedCategoryIndex =
                        TransactionMode.EXPENSE.categoryList.indexOf(it.category)
                    binding.categoryGroup.check(selectedCategoryIndex)
                } else {
                    transactionViewModel.transactionType.value = TransactionMode.INCOME
                    binding.transactionMode.check(R.id.income_mode)
                    selectedCategoryIndex = TransactionMode.INCOME.categoryList.indexOf(it.category)
                    binding.categoryGroup.check(selectedCategoryIndex)
                }
            }
        }
    }

    private fun initialiseTransactionType() {
        binding.transactionMode.addOnButtonCheckedListener { _, checkedId, isChecked ->
            when (checkedId) {
                R.id.expense_mode -> {
                    if (isChecked) {
                        selectedCategoryIndex = 0
                        transactionViewModel.transactionType.value = TransactionMode.EXPENSE
                    }
                }
                R.id.income_mode  -> {
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
            binding.keyboard.amountText.selectionPosition.flowWithLifecycle(lifecycle).collect {
                transactionKeyboard.selectionPosition = it
            }
        }
        binding.keyboard.amountText.requestFocus()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun initialiseTransactionDateClickListener() {
        binding.transactionDate.text = DateFormatter().convertLongToDate(selectedDate.toLong())
        binding.transactionDate.setOnClickListener {
            val calendarConstraintsBuilder = CalendarConstraints.Builder()
                .setValidator(DateValidatorPointBackward.now())
            val datePickerBuilder = MaterialDatePicker.Builder.datePicker()
            datePickerBuilder.setCalendarConstraints(calendarConstraintsBuilder.build())
            datePickerBuilder.setSelection(Calendar.getInstance().timeInMillis)
            val datePicker = datePickerBuilder.build()
            datePicker.addOnPositiveButtonClickListener {
                binding.transactionDate.text = DateFormatter().convertLongToDate(it)
                selectedDate = it.toString()
            }
            datePicker.show(supportFragmentManager, "")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.transaction_menu, menu)
        lifecycleScope.launch {
            transactionViewModel.transaction.collectLatest {
                menu?.findItem(R.id.transaction_delete)?.isVisible = it != null
            }
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.transaction_delete -> {
                deleteTransactionAlert()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun deleteTransactionAlert() {
        val materialAlertDialogBuilder = MaterialAlertDialogBuilder(this@TransactionActivity)
        materialAlertDialogBuilder.setTitle("Are you sure to delete this transaction")
            .setPositiveButton("Delete") { dialog, _ ->
                transactionViewModel.deleteTransaction()
                dialog.dismiss()
                this.finish()
            }.setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
        materialAlertDialogBuilder.show()
    }
}