package com.devstudio.profile.composables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.devstudio.account.R
import com.devstudio.core_data.Theme_proto.DARK
import com.devstudio.core_data.Theme_proto.LIGHT
import com.devstudio.core_data.Theme_proto.SYSTEM_DEFAULT
import com.devstudio.core_model.models.ExpressWalletAppState
import com.devstudio.profile.viewmodels.EditableSettings
import com.devstudio.profile.viewmodels.ProfileUiState
import com.devstudio.profile.viewmodels.ProfileViewModel
import com.devstudioworks.ui.components.Page
import com.devstudioworks.ui.icons.EMAppIcons
import com.devstudioworks.ui.theme.appColors

@Composable
fun ProfileMainScreen(navController: NavHostController) {
    val profileViewModel = hiltViewModel<ProfileViewModel>()
    val profileUiState = profileViewModel.profileUiState.collectAsStateWithLifecycle()
    Page(title = "Profile", navController = navController) {
        Surface(
            modifier = Modifier
                .widthIn(max = 640.dp)
                .padding(10.dp),
        ) {
            Column {
                when (val uiState = profileUiState.value) {
                    ProfileUiState.Loading -> Text(text = stringResource(R.string.loading))
                    is ProfileUiState.Success -> {
                        ProfileSummary(appColors, profileViewModel)
                        PreferencesPanel(uiState.data, navController)
                    }
                }
            }
        }
    }
}

data class Preference(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val clickEvent: () -> Unit = {},
)


@Composable
private fun PreferencesPanel(profileUiState: UserPreferencesData, navController: NavHostController) {
    val theme = profileUiState.theme
    if (theme == SYSTEM) {
        if (isSystemInDarkTheme()) {
            DARK
        } else {
            LIGHT
        }
    }
    val themeIcon = when (theme) {
        LIGHT -> {
            EMAppIcons.Light
        }

        DARK -> {
            EMAppIcons.Dark
        }

        else -> {
            EMAppIcons.Light
        }
    }
    val preferenceList = listOf(Preference(
        title = "Change Theme",
        description = "Theme preferences",
        icon = themeIcon,
    ) {
        navController.navigate(ExpressWalletAppState.ThemeScreen.route)
    }, Preference(
        title = "Custom Remainder",
        description = "Remainder to update transactions",
        icon = Icons.Filled.Notifications,
    ) {
        navController.navigate(ExpressWalletAppState.RemainderScreen.route)
    })
    Column {
        Spacer(modifier = Modifier.padding(10.dp))
        Label("Preferences")
        Spacer(modifier = Modifier.padding(5.dp))
        LazyColumn {
            items(preferenceList.size) { index ->
                val preference = preferenceList[index]
                PreferenceItem(preference)
            }
        }
    }
}

@Composable
fun PreferenceItem(preference: Preference) {
    Row(
        modifier = Modifier
            .padding(vertical = 5.dp)
            .clickable(onClick = preference.clickEvent)
            .fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = preference.icon,
            contentDescription = null,
            modifier = Modifier.padding(end = 10.dp),
            tint = appColors.material.onSurfaceVariant
        )
        Text(
            text = preference.description,
            textAlign = TextAlign.Justify,
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(vertical = 5.dp),
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = appColors.material.onSurfaceVariant
            )
        )
    }
}

@Composable
fun Label(value: String) {
    Text(
        text = value, color = appColors.material.onSurfaceVariant, style = TextStyle(
            fontSize = 22.sp,
            fontWeight = FontWeight.Medium,
            color = appColors.material.onSurfaceVariant
        )
    )
}