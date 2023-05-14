package com.devstudio.transactions.composables.transacionDashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.BottomSheetScaffoldState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.util.Pair
import androidx.fragment.app.FragmentManager
import androidx.hilt.navigation.compose.hiltViewModel
import com.devstudio.transactions.composables.transactionList.TransactionsList
import com.devstudio.transactions.models.DateSelectionStatus
import com.devstudio.transactions.viewmodel.TransactionViewModel
import com.devstudioworks.ui.theme.appColors
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDashBoard(
    filterBottomSheetScaffoldState: BottomSheetScaffoldState
) {
    Surface(
        modifier = Modifier
            .widthIn(max = 640.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(PaddingValues(6.dp))
        ) {
            TransactionSummary()
            TransactionOptions(filterBottomSheetScaffoldState)
            TransactionsList()
        }
    }
}


fun showDateRangePicker(
    fragmentManager: FragmentManager,
    dateSelectionRange: Pair<Long, Long>?,
    dateSelectionListener: (Any) -> Unit
) {
    val dateRangePicker =
        MaterialDatePicker.Builder.dateRangePicker().setSelection(dateSelectionRange).build()
    dateRangePicker.addOnPositiveButtonClickListener {
        dateSelectionListener(DateSelectionStatus.SELECTED(it))
    }
    dateRangePicker.addOnNegativeButtonClickListener {
        dateSelectionListener(DateSelectionStatus.CANCELED)
    }
    dateRangePicker.showNow(fragmentManager, "")
}

@Preview()
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionOptions(
    filterBottomSheetScaffoldState: BottomSheetScaffoldState = BottomSheetScaffoldState(
        bottomSheetState = SheetState(
            initialValue = SheetValue.Hidden,
            skipPartiallyExpanded = true
        ), snackbarHostState = SnackbarHostState()
    )
) {
    val coroutineScope = rememberCoroutineScope()
    val transactionViewModel = hiltViewModel<TransactionViewModel>()
    val selectedTransactionFilter =
        transactionViewModel.selectedTransactionFilter.collectAsState().value
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AnimatedVisibility(
            visible = selectedTransactionFilter != null,
            enter = fadeIn(initialAlpha = .5f),
            exit = fadeOut(animationSpec = tween(durationMillis = 200))
        ) {
            TextButton(onClick = {
                if (selectedTransactionFilter != null) {
                    transactionViewModel.updateSelectedTransactionFilter(null)
                }
            }) {
                Text(
                    text = if (selectedTransactionFilter == null) "" else "Reset",
                    color = if (selectedTransactionFilter == null) {
                        Color.Unspecified
                    } else appColors.material.tertiary
                )
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        Button(
            shape = if (selectedTransactionFilter == null) {
                ButtonDefaults.outlinedShape
            } else {
                ButtonDefaults.filledTonalShape
            },
            colors = if (selectedTransactionFilter == null) {
                ButtonDefaults.outlinedButtonColors()
            } else {
                ButtonDefaults.filledTonalButtonColors()
            },
            onClick = {
                coroutineScope.launch {
                    filterBottomSheetScaffoldState.bottomSheetState.show()
                }
            },
            border = BorderStroke(width = 1.dp, color = appColors.material.onPrimaryContainer),
            modifier = Modifier.align(alignment = Alignment.CenterVertically)
        ) {
            Icon(imageVector = Icons.Filled.FilterList, contentDescription = "Filter")
        }
    }
}
