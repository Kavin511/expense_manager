package com.devstudio.category

sealed class CategoryState {
    object LOADING : CategoryState()
    class COMPLETED(val categoryList: kotlinx.coroutines.flow.Flow<List<com.devstudio.database.models.Category>>) :
        CategoryState()

    object ERROR : CategoryState()
}
