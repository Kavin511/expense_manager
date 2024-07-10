package com.devstudio.transactions.acivity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.devstudio.data.repository.BooksRepositoryImpl
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
import com.devstudio.utils.utils.AppConstants.Companion.INVESTMENT
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
import javax.inject.Inject

@AndroidEntryPoint
class TransactionActivity : AppCompatActivity() {

    private var _binding: ActivityTransactionBinding? = null
    private val transactionViewModel by viewModels<TransactionViewModel>()
    var selectedCategoryIndexList: MutableList<Int> = mutableListOf(0, 0, 0)
    private val binding
        get() = _binding!!
    private var categoryList = listOf<Category>()
    private var currentTransaction: Transaction? = null
    private lateinit var selectedTransactionMode: String

    @Inject
    lateinit var booksRepositoryImpl: BooksRepositoryImpl

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.topAppBar)
        initialiseTransactionType()
        initialiseTransactionKeyboard()
        fetchAndUpdateTransactionToBeEdited()
        initialiseNavigation()
        initialiseSaveClickListener()
        hideKeyboardOnFocusChange()
        initialiseListeners()
        binding.keyboard.amountText.showSoftInputOnFocus = false
    }

    private fun initialiseListeners() {
        initialiseFuturePaymentListeners()
        initialiseTransactionDateClickListener()
    }

    private fun initialiseFuturePaymentListeners() {
        binding.futurePayment.setOnCheckedChangeListener { _, isChecked ->
            if (transactionViewModel.transactionType.value == TransactionMode.EXPENSE) {
                transactionViewModel.futurePaymentModeStatus.isDebit = isChecked
            } else {
                transactionViewModel.futurePaymentModeStatus.isCredit = isChecked
            }
        }
    }

    private fun initialiseSaveClickListener() {
        binding.keyboard.saveTransaction.setOnClickListener {
            if (categoryList.isEmpty()) {
                categoryAdditionSnackBar()
                return@setOnClickListener
            }
            lifecycleScope.launch {
                val transaction = transactionViewModel.transaction.value
                    ?: Transaction(id = Calendar.getInstance().time.time)
                updateTransaction(transaction)
            }
            finish()
        }
    }

    companion object {
        var selectedDate = Calendar.getInstance().time.time.toString()
    }

    private fun getTransactionAmount(): Double {
        return TransactionInputFormula().calculate(binding.keyboard.amountText.text.toString())
    }

    private suspend fun updateTransaction(transaction: Transaction) {
        transaction.apply {
            amount = getTransactionAmount()
            note = binding.noteText.text.toString()
            transactionMode = transactionViewModel.transactionType.value.toString()
            transactionDate = selectedDate
            categoryId = categoryList[getSelectedCategoryIndex()].id
            paymentStatus = getPaymentStatus().name
            bookId = booksRepositoryImpl.getSelectedBook().first()
        }
        transactionViewModel.upsertTransaction(transaction)
    }

    private fun getPaymentStatus(): PaymentStatus =
        if (binding.futurePayment.isChecked && transactionViewModel.transactionType.value == TransactionMode.EXPENSE) {
            PaymentStatus.DEBT
        } else if (binding.futurePayment.isChecked && transactionViewModel.transactionType.value == TransactionMode.INCOME) {
            PaymentStatus.CREDIT
        } else {
            PaymentStatus.COMPLETED
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
            mapSelectedTransactionDetails()
        }
        transactionViewModel.isEditingOldTransaction.observe(this) {
            if (it) {
                binding.keyboard.saveTransaction.text =
                    getString(com.devstudio.core.designsystem.R.string.update_transaction)
            }
        }
    }

    private suspend fun mapSelectedTransactionDetails() {
        transactionViewModel.transaction.first().let {
            if (it != null) {
                currentTransaction = it
                binding.keyboard.amountText.editableText.insert(0, it.amount.toString())
                binding.noteText.setText(it.note)
                binding.transactionDate.text =
                    DateFormatter.convertLongToDate(it.transactionDate)
                selectedDate = it.transactionDate
                selectedTransactionMode = it.transactionMode
                when (selectedTransactionMode) {
                    EXPENSE -> {
                        transactionViewModel.transactionType.value = TransactionMode.EXPENSE
                        binding.futurePayment.isChecked = it.paymentStatus == PaymentStatus.DEBT.name
                        transactionViewModel.futurePaymentModeStatus.isDebit = binding.futurePayment.isChecked
                    }
                    INVESTMENT -> {
                        transactionViewModel.transactionType.value = TransactionMode.INVESTMENT
                        binding.futurePayment.isChecked = it.paymentStatus == PaymentStatus.CREDIT.name
                        transactionViewModel.futurePaymentModeStatus.isCredit = binding.futurePayment.isChecked
                    }
                    else -> {
                        transactionViewModel.transactionType.value = TransactionMode.INCOME
                        binding.futurePayment.isChecked = it.paymentStatus == PaymentStatus.CREDIT.name
                        transactionViewModel.futurePaymentModeStatus.isCredit = binding.futurePayment.isChecked
                    }
                }
                categoryList = transactionViewModel.getCategories(selectedTransactionMode).first()
                setSelectedCategoryIndex(
                    categoryList.indexOfFirst { category ->
                        category.id == it.categoryId
                    },
                )
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
                R.id.investment_mode -> {
                    if (isChecked) {
                        transactionViewModel.transactionType.value = TransactionMode.INVESTMENT
                    }
                }
            }
        }
        lifecycleScope.launch {
            observeTransactionType()
        }
    }

    private suspend fun observeTransactionType() {
        transactionViewModel.transactionType.collectLatest {
            when (it) {
                TransactionMode.EXPENSE -> {
                    updateCategoriesBasedOnTransactionType(TransactionMode.EXPENSE)
                    binding.transactionMode.check(R.id.expense_mode)
                    binding.futurePayment.text = "Mark transaction as Debt"
                    binding.futurePayment.isChecked = transactionViewModel.futurePaymentModeStatus.isDebit
                }

                TransactionMode.INVESTMENT -> {
                    updateCategoriesBasedOnTransactionType(TransactionMode.INVESTMENT)
                    binding.transactionMode.check(R.id.investment_mode)
                    binding.futurePayment.text = "Mark transaction as Credit"
                    binding.futurePayment.isChecked = transactionViewModel.futurePaymentModeStatus.isCredit
                }

                else -> {
                    updateCategoriesBasedOnTransactionType(TransactionMode.INCOME)
                    binding.transactionMode.check(R.id.income_mode)
                    binding.futurePayment.text = "Mark transaction as Credit"
                    binding.futurePayment.isChecked = transactionViewModel.futurePaymentModeStatus.isCredit
                }
            }
        }
    }

    private suspend fun updateCategoriesBasedOnTransactionType(transactionMode: TransactionMode) {
        binding.categoryGroup.removeAllViews()
        with(transactionViewModel.getCategories(transactionMode.name).first()) {
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

    private fun initialiseNavigation() {
        binding.topAppBar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun initialiseTransactionKeyboard() {
        val transactionKeyboard = TransactionKeyboard(
            baseContext,
            binding.keyboard.amountText.editableText,
            binding.keyboard,
        )
        transactionKeyboard.initialiseListeners()
        binding.keyboard.amountText.requestFocus()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun initialiseTransactionDateClickListener() {
        binding.transactionDate.text = DateFormatter.convertLongToDate(selectedDate)
        binding.transactionDate.setOnClickListener {
            val calendarConstraintsBuilder = CalendarConstraints.Builder()
                .setValidator(DateValidatorPointBackward.now())
            val datePickerBuilder = MaterialDatePicker.Builder.datePicker()
            datePickerBuilder.setCalendarConstraints(calendarConstraintsBuilder.build())
            datePickerBuilder.setSelection(selectedDate.toLong())
            val datePicker = datePickerBuilder.build()
            datePicker.addOnPositiveButtonClickListener {
                binding.transactionDate.text = DateFormatter.convertLongToDate(it.toString())
                selectedDate = it.toString()
            }
            datePicker.show(supportFragmentManager, "")
        }
    }

    private fun getSelectedCategoryIndex(type: String = transactionViewModel.transactionType.value.name): Int {
        return if (type == TransactionMode.EXPENSE.name) {
            selectedCategoryIndexList[EXPENSE_INDEX]
        } else if (type == TransactionMode.INCOME.name) {
            selectedCategoryIndexList[INCOME_INDEX]
        } else {
            selectedCategoryIndexList[INVESTMENT_INDEX]
        }
    }

    private fun setSelectedCategoryIndex(index: Int) {
        val selectedIndex = if (categoryList.isEmpty()) {
            categoryAdditionSnackBar()
            0
        } else if (index >= 0) {
            index
        } else {
            Toast.makeText(
                applicationContext,
                "First category is selected as old category is not available for this transaction type",
                Toast.LENGTH_LONG,
            ).show()
            0
        }
        when (transactionViewModel.transactionType.value) {
            TransactionMode.EXPENSE -> {
                selectedCategoryIndexList[EXPENSE_INDEX] = selectedIndex
            }

            TransactionMode.INCOME -> {
                selectedCategoryIndexList[INCOME_INDEX] = selectedIndex
            }

            else -> {
                selectedCategoryIndexList[INVESTMENT_INDEX] = selectedIndex
            }
        }
    }

    private fun categoryAdditionSnackBar() {
        Snackbar.make(
            binding.root,
            "No category available for this transaction type",
            Snackbar.LENGTH_LONG,
        ).show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.transaction_menu, menu)
        menu?.findItem(R.id.transaction_delete)?.isVisible = currentTransaction != null
        return super.onCreateOptionsMenu(menu)
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
            positiveText = "Delete",
            positiveCallback = {
                transactionViewModel.deleteTransaction(transactionViewModel.transaction.value!!)
                it.dismiss()
                this.finish()
            },
            negativeCallback = {
                it.dismiss()
            },
        )
    }
}

val EXPENSE_INDEX = 0
val INCOME_INDEX = 1
val INVESTMENT_INDEX = 2

enum class PaymentStatus {
    DEBT,
    CREDIT,
    COMPLETED,
}
