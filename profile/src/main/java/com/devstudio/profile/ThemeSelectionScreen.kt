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
import com.devstudio.data.model.Theme
import com.devstudio.data.model.TransactionFilterType
import com.devstudio.data.model.UserPreferencesData
import com.devstudio.designSystem.MEDIUM_SPACING
import com.devstudio.profile.viewmodels.ProfileViewModel
import com.devstudio.designSystem.components.Page
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun ThemeSelectionScreen(navController: NavHostController) {
    val profileViewModel = hiltViewModel<ProfileViewModel>()
    val theme: UserPreferencesData =
        profileViewModel.userPreferencesDataStore.userData.collectAsState(
            initial = UserPreferencesData(
                Theme.SYSTEM_DEFAULT,
                selectedBookId = 0,
                filterType = TransactionFilterType.ALL,
            ),
        ).value

    val themeList = listOf(
        Theme.DARK,
        Theme.LIGHT,
        Theme.SYSTEM_DEFAULT,
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
                    horizontalArrangement = Arrangement.Start,
                ) {
                    RadioButton(selected = themeProto == theme.theme, onClick = null)
                    val name = themeProto.name.replace("_", " ").lowercase()
                    Text(
                        text = name.replaceFirstChar { it.uppercase() },
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(
                                start = MEDIUM_SPACING,
                            ),
                    )
                }
            }
        }
    }
}
