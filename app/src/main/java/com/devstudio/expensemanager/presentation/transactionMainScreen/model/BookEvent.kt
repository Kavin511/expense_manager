package com.devstudio.expensemanager.presentation.transactionMainScreen.model

import com.devstudio.transactions.models.BottomSheetEvent

class BookEvent(
    override var showBottomSheet: Boolean, override var selectedItem: Long? = null
) : BottomSheetEvent<Long>()

