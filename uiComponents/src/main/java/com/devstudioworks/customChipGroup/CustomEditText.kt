package com.devstudioworks.customChipGroup

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class CustomEditText(context: Context, attrs: AttributeSet) : TextInputEditText(context, attrs) {
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