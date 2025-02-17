package com.devstudioworks.ui.components

import android.content.Context
import android.content.DialogInterface
import com.google.android.material.dialog.MaterialAlertDialogBuilder

fun MaterialAlert(
    context: Context,
    title: String,
    positiveText: String,
    negativeText: String,
    positiveCallback: (DialogInterface) -> Unit,
    negativeCallback: (DialogInterface) -> Unit,
) {
    val materialAlertDialogBuilder = MaterialAlertDialogBuilder(context)
    materialAlertDialogBuilder.setTitle(
        title,
    ).setPositiveButton(positiveText) { dialog, _ ->
        positiveCallback.invoke(dialog)
    }.setNegativeButton(negativeText) { dialog, _ ->
        negativeCallback.invoke(dialog)
    }
    materialAlertDialogBuilder.show()
}
