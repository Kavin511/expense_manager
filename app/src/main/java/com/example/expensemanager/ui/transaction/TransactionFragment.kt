package com.example.expensemanager.ui.transaction

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.viewModelScope
import com.example.expensemanager.databinding.FragmentTransactionBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.coroutineContext

class TransactionFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentTransactionBinding? = null
    private lateinit var transactionViewModel: TransactionViewModel
    private val binding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionBinding.inflate(inflater, container, false)
        transactionViewModel = TransactionViewModel()
        transactionViewModel.category.forEach {
            val chip = com.google.android.material.chip.Chip(context)
            chip.text = it.key
            chip.id = it.value
            chip.setOnClickListener {
                chip.isSelected = true
            }
            binding.categoryGroup.addView(chip)
        }
        val transactionKeyboard = TransactionKeyboard(
            requireContext(),
            binding.amountText.editableText,
            binding.keyboard
        )
        binding.amountText.showSoftInputOnFocus = false
        binding.amountText.requestFocus()
        transactionKeyboard.initialiseListeners()
        transactionViewModel.viewModelScope.launch {
            binding.amountText.selectionPosition.collect {
                transactionKeyboard.selectionPosition = it
            }
        }
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            TransactionFragment().apply {
                arguments = Bundle().apply {
                }
            }
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
                if (_selectionPosition!=null) {
                    _selectionPosition?.value = selStart
                }
            }
        }
    }
}