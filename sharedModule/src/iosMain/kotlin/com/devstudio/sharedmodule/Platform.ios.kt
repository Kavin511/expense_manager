package com.devstudio.sharedmodule

import androidx.compose.runtime.Composable

actual suspend fun saveTransactions (transactions: List<List<String>>){

}
@Composable
public actual fun FilePicker(
    show: Boolean,
    initialDirectory: String?,
    fileExtensions: Array<String>,
    title: String?,
    onFileSelected: @Composable (List<List<String>>?) -> Unit,
) {
   
}

actual object AppContext
