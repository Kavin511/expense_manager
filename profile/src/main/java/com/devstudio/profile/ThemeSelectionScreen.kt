package com.devstudio.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.devstudio.account.R
import com.devstudio.core_data.Theme_proto
import com.devstudio.core_data.UserPreferences
import com.devstudio.profile.viewmodels.ProfileViewModel
import com.devstudioworks.ui.components.Page
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun ThemeSelectionScreen(navController: NavHostController) {
    val profileViewModel = hiltViewModel<ProfileViewModel>()
    val theme =
        profileViewModel.userPreferencesDataStore.data.collectAsState(initial = UserPreferences.getDefaultInstance()).value.theme

    val themeList = listOf(
        Theme_proto.DARK, Theme_proto.LIGHT, Theme_proto.SYSTEM_DEFAULT
    )
    Page(title = "Choose Theme", navController = navController, shouldNavigateUp = true) {
        LazyColumn {
            items(themeList.size) { index ->
                val themeProto = themeList[index]
                Row(
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth()
                        .clickable {
                            CoroutineScope(Dispatchers.IO).launch {
                                profileViewModel.updateTheme(themeProto)
                            }
                        },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    RadioButton(selected = themeProto == theme, onClick = null)
                    val name = themeProto.name.replace("_", " ").lowercase()
                    Text(
                        text = name.replaceFirstChar { it.uppercase() },
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(
                                start = dimensionResource(id = com.devstudio.core.designsystem.R.dimen.spacing_medium)
                            )
                    )
                }
            }
        }
    }
}