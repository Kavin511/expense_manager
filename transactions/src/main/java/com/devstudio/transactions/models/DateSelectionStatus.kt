package com.devstudio.transactions.models

import androidx.core.util.Pair

sealed class DateSelectionStatus {
    object CANCELED : DateSelectionStatus()
    data class SELECTED(val selectedRange: Pair<Long, Long>) : DateSelectionStatus()
}
