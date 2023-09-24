package com.devstudio.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.devstudio.data.model.Theme
import com.devstudio.profile.viewmodels.ProfileUiState
import com.devstudio.profile.viewmodels.ProfileViewModel
import com.devstudioworks.ui.components.Page

@Composable
fun ThemeSelectionScreen(navController: NavHostController) {
    val profileViewModel = hiltViewModel<ProfileViewModel>()
    val profileUiState = profileViewModel.profileUiState.collectAsStateWithLifecycle()
    val themeList = listOf(
        Theme.DARK, Theme.LIGHT, Theme.SYSTEM
    )
    when (profileUiState.value) {
        is ProfileUiState.Success -> {
            val userPreferencesData = (profileUiState.value as ProfileUiState.Success).data
            Page(title = "Choose Theme", navController = navController, shouldNavigateUp = true) {
                LazyColumn {
                    items(themeList.size) { index ->
                        val theme = themeList[index]
                        Row(
                            modifier = Modifier
                                .padding(10.dp)
                                .fillMaxWidth()
                                .clickable {
                                    profileViewModel.updateTheme(theme)
                                },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {
                            RadioButton(selected = theme == userPreferencesData.theme, onClick = null)
                            val name = theme.name.replace("_", " ").lowercase()
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

        else -> {
            CircularProgressIndicator()
        }
    }
}
