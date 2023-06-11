package com.devstudio.transactions.acivity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.devstudio.expensemanager.db.models.Category
import com.devstudio.expensemanager.db.models.Transaction
import com.devstudio.expensemanager.db.models.TransactionMode
import com.devstudio.transactions.R
import com.devstudio.transactions.databinding.ActivityTransactionBinding
import com.devstudio.transactions.uicomponents.TransactionKeyboard
import com.devstudio.transactions.viewmodel.TransactionViewModel
import com.devstudio.utils.formatters.DateFormatter
import com.devstudio.utils.formulas.TransactionInputFormula
import com.devstudio.utils.utils.AppConstants.Companion.EXPENSE
import com.devstudioworks.ui.components.MaterialAlert
import com.google.android.material.chip.Chip
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar

@AndroidEntryPoint
class TransactionActivity : AppCompatActivity() {

    private var _binding: ActivityTransactionBinding? = null
    private val transactionViewModel by viewModels<TransactionViewModel>()
    var selectedCategoryIndexList: MutableList<Int> = mutableListOf(0, 0)
    private val binding
        get() = _binding!!
    private var categoryList = listOf<Category>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.topAppBar)
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
            if (categoryList.isEmpty()) {
                categoryAdditionSnackBar()
                return@setOnClickListener
            }
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
        val transaction = Transaction(
            id = Calendar.getInstance().time.time,
            amount = getTransactionAmount(),
            note = binding.noteText.text.toString(),
            transactionMode = transactionViewModel.transactionType.value.toString(),
            transactionDate = selectedDate,
            categoryId = categoryList[getSelectedCategoryIndex()].id
        )
        transactionViewModel.insertTransaction(transaction)
    }

    private fun getTransactionAmount(): Double {
        return TransactionInputFormula().calculate(binding.keyboard.amountText.text.toString())
    }

    private suspend fun updateOldTransaction(oldTransaction: Transaction) {
        val selectedCategoryId = categoryList[getSelectedCategoryIndex()].id
        oldTransaction.apply {
            amount =
                TransactionInputFormula().calculate(binding.keyboard.amountText.text.toString())
            note = binding.noteText.text.toString()
            transactionMode = transactionViewModel.transactionType.value.toString()
            transactionDate = selectedDate
            categoryId = selectedCategoryId
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
        lifecycleScope.launch {
            val id = intent.getLongExtra("id", 0)
            transactionViewModel.getAndUpdateTransactionById(id)
            updateSelectedCategory()
        }
        transactionViewModel.isEditingOldTransaction.observe(this) {
            if (it) {
                binding.keyboard.saveTransaction.text =
                    getString(com.devstudioworks.core.ui.R.string.update_transaction)
            }
        }
    }

    private suspend fun updateSelectedCategory() {
        transactionViewModel.transaction.first().let {
            if (it != null) {
                transactionMenu?.findItem(R.id.transaction_delete)?.isVisible = true
                binding.keyboard.amountText.editableText.insert(0, it.amount.toString())
                binding.noteText.setText(it.note)
                binding.transactionDate.text =
                    DateFormatter.convertLongToDate(it.transactionDate.toLong())
                selectedDate = it.transactionDate
                if (it.transactionMode == EXPENSE) {
                    transactionViewModel.transactionType.value = TransactionMode.EXPENSE
                    binding.transactionMode.check(R.id.expense_mode)
                } else {
                    transactionViewModel.transactionType.value = TransactionMode.INCOME
                    binding.transactionMode.check(R.id.income_mode)
                }
                categoryList = transactionViewModel.getCategories(it.transactionMode).first()
                setSelectedCategoryIndex(categoryList.indexOfFirst { category ->
                    category.id == it.categoryId
                })
                binding.categoryGroup.check(getSelectedCategoryIndex())
            }
        }
    }

    private fun initialiseTransactionType() {
        binding.transactionMode.addOnButtonCheckedListener { _, checkedId, isChecked ->
            when (checkedId) {
                R.id.expense_mode -> {
                    if (isChecked) {
                        transactionViewModel.transactionType.value = TransactionMode.EXPENSE
                    }
                }

                R.id.income_mode -> {
                    if (isChecked) {
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
            with(transactionViewModel.getCategories(it.name).first()) {
                categoryList = this
                forEachIndexed { index, value ->
                    val chip = layoutInflater.inflate(R.layout.category_chip, null) as Chip
                    chip.text = value.name
                    chip.id = index
                    chip.isCheckable = true
                    if (getSelectedCategoryIndex() == index) {
                        chip.isChecked = true
                    }
                    chip.checkedIcon = null
                    chip.setOnClickListener {
                        setSelectedCategoryIndex(index)
                    }
                    binding.categoryGroup.addView(chip)
                }
                binding.categoryGroup.requestLayout()
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
        binding.keyboard.amountText.requestFocus()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun initialiseTransactionDateClickListener() {
        binding.transactionDate.text = DateFormatter.convertLongToDate(selectedDate.toLong())
        binding.transactionDate.setOnClickListener {
            val calendarConstraintsBuilder = CalendarConstraints.Builder()
                .setValidator(DateValidatorPointBackward.now())
            val datePickerBuilder = MaterialDatePicker.Builder.datePicker()
            datePickerBuilder.setCalendarConstraints(calendarConstraintsBuilder.build())
            datePickerBuilder.setSelection(selectedDate.toLong())
            val datePicker = datePickerBuilder.build()
            datePicker.addOnPositiveButtonClickListener {
                binding.transactionDate.text = DateFormatter.convertLongToDate(it)
                selectedDate = it.toString()
            }
            datePicker.show(supportFragmentManager, "")
        }
    }

    private fun getSelectedCategoryIndex(type: String = transactionViewModel.transactionType.value.name): Int {
        return if (type == TransactionMode.EXPENSE.name) {
            selectedCategoryIndexList[0]
        } else {
            selectedCategoryIndexList[1]
        }
    }

    private fun setSelectedCategoryIndex(index: Int) {
        val selectedIndex = if (categoryList.isEmpty()) {
            categoryAdditionSnackBar()
            0
        } else if (index >= 0) index else {
            Toast.makeText(
                applicationContext,
                "First category is selected as old category is not available for this transaction type",
                Toast.LENGTH_LONG
            ).show()
            0
        }
        if (transactionViewModel.transactionType.value == TransactionMode.EXPENSE) {
            selectedCategoryIndexList[0] = selectedIndex
        } else {
            selectedCategoryIndexList[1] = selectedIndex
        }
    }

    private fun categoryAdditionSnackBar() {
        Snackbar.make(
            binding.root,
            "No category available for this transaction type",
            Snackbar.LENGTH_LONG
        ).show()
    }

    var transactionMenu: Menu? = null
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        transactionMenu = menu
        menuInflater.inflate(R.menu.transaction_menu, menu)
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
        MaterialAlert(
            context = this@TransactionActivity,
            title = "Are you sure to delete this transaction",
            negativeText = "No",
            positiveText = "Delete", positiveCallback = {
                transactionViewModel.deleteTransaction(transactionViewModel.transaction.value!!)
                it.dismiss()
                this.finish()
            }, negativeCallback = {
                it.dismiss()
            }
        )
    }
}