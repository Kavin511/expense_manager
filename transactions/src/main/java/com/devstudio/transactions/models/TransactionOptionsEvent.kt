package com.devstudio.transactions.models

class TransactionOptionsEvent(
    override var showBottomSheet: Boolean,
    override var selectedItem: FilterItem? = null,
) : BottomSheetEvent<FilterItem>()

open class BottomSheetEvent<T>(
    open var showBottomSheet: Boolean = false,
    open var selectedItem: T? = null,
)