package com.devstudio.transactions.models

import com.devstudio.model.models.ExpressWalletEvent

class TransactionOptionsEvent(
    override var showBottomSheet: Boolean,
    override var selectedItem: FilterItem? = null,
) : BottomSheetEvent<FilterItem>()

open class BottomSheetEvent<T>(
    open var showBottomSheet: Boolean = false,
    open var selectedItem: T? = null,
) : ExpressWalletEvent