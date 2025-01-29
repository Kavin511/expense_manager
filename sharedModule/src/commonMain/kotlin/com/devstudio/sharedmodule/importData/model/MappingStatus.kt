package com.devstudio.sharedmodule.importData.model


sealed class MappingStatus {
    data object YetToMap : MappingStatus()
    data class Mapped(var fieldIndex: Int) : MappingStatus()
    sealed class MappingError(open val errorValueIndex: Int) : MappingStatus() {
        data class FieldNotSelected(override val errorValueIndex: Int) :
            MappingError(errorValueIndex)
        data class AmountMappingFailed(override val errorValueIndex: Int) :
            MappingError(errorValueIndex)

        data class TransactionModeMappingFailed(override val errorValueIndex: Int) :
            MappingError(errorValueIndex)

        data class DATEMappingFailed(override val errorValueIndex: Int) :
            MappingError(errorValueIndex)

        data class BookNameMappingFailed(override val errorValueIndex: Int) :
            MappingError(errorValueIndex)

        data class CategoryMappingFailed(override val errorValueIndex: Int) :
            MappingError(errorValueIndex)

    }
}