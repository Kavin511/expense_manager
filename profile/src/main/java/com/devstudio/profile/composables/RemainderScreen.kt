package com.devstudio.profile.composables

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.devstudio.data.repository.Remainder
import com.devstudio.profile.viewmodels.ProfileViewModel
import com.devstudioworks.ui.components.Page
import com.devstudioworks.ui.theme.appColors
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.text.SimpleDateFormat
import java.util.Locale

var hour = 0
var minute = 0

@Composable
fun RemainderScreen(navController: NavHostController) {
    val profilerViewModel = hiltViewModel<ProfileViewModel>()
    val context = LocalContext.current
    val dayStateList = remember {
        mutableStateListOf(
            Day("S", false, "Sunday", 0),
            Day("M", false, "Monday", 1),
            Day("T", false, "Tuesday", 2),
            Day("W", false, "Wednesday", 3),
            Day("T", false, "Thursday", 4),
            Day("F", false, "Friday", 5),
            Day("S", false, "Saturday", 6),
        )
    }
    Page(title = "Remainder", navController = navController, shouldNavigateUp = true, action = {
        Text(
            text = "Save",
            modifier = Modifier.clickable {
                val remainders = dayStateList.map { Remainder(it.id, hour, minute, isEnabled = it.isSelected) }
                profilerViewModel.setRemainders(remainders, context)
//            navController.popBackStack()
            },
        )
    }) {
        val text = remember {
            mutableStateOf("12:00 PM")
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .fillMaxWidth(.75f)
                    .fillMaxHeight(.75f),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                DaySelector(dayStateList)
                Spacer(modifier = Modifier.padding(10.dp))
                TimeSelector(text)
            }
        }
    }
}

@Composable
private fun TimeSelector(text: MutableState<String>) {
    val activity = LocalContext.current as AppCompatActivity
    val picker =
        MaterialTimePicker.Builder().setTimeFormat(TimeFormat.CLOCK_12H).setHour(12).setMinute(0)
            .setTitleText("Select Remainder time").build()
    picker.addOnPositiveButtonClickListener {
        hour = picker.hour
        minute = picker.minute
        val time = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(
            SimpleDateFormat("HH:mm", Locale.getDefault()).parse(
                "$hour:$minute",
            )!!,
        )
        text.value = time
    }
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = "What time of the day?")
        Row {
            Text(
                text = text.value,
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .border(
                        border = BorderStroke(
                            1.dp,
                            appColors.material.primary,
                        ),
                        shape = RoundedCornerShape(15.dp),
                    )
                    .padding(10.dp)
                    .clickable {
                        picker.show(
                            activity.supportFragmentManager,
                            "tag",
                        )
                    },
            )
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
private fun DaySelector(selectedDays: SnapshotStateList<Day>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = "Which days would you like to be reminded to add transactions?")
        Spacer(modifier = Modifier.padding(vertical = 5.dp))
        FlowRow(
            content = {
                repeat(selectedDays.size) { index ->
                    val selectedState = mutableStateOf(selectedDays[index].isSelected)
                    FilterChip(
                        modifier = Modifier.padding(horizontal = 2.dp),
                        selected = selectedState.value,
                        onClick = {
                            selectedDays[index].isSelected = !selectedState.value
                            selectedState.value = selectedDays[index].isSelected
                        },
                        label = {
                            Text(
                                text = selectedDays[index].description,
                            )
                        },
                    )
                }
            },
        )
    }
}

data class Day(
    val name: String,
    var isSelected: Boolean = false,
    val description: String,
    var id: Int,
)
