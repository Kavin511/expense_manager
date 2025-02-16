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
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.navigation.compose.rememberNavController
import com.devstudio.account.R
import com.devstudio.data.model.Theme
import com.devstudio.data.model.Theme.DARK
import com.devstudio.data.model.Theme.LIGHT
import com.devstudio.profile.viewmodels.EditableSettings
import com.devstudio.profile.viewmodels.ProfileUiState
import com.devstudio.profile.viewmodels.ProfileViewModel
import com.devstudio.designSystem.components.Screen
import com.devstudio.designSystem.icons.EMAppIcons
import com.devstudio.designSystem.appColors
import com.devstudio.designSystem.maxScreenWidth
import com.devstudio.model.models.ExpressWalletAppState
import com.devstudio.model.models.OnEvent

@Composable
fun ProfileMainScreen(
    onEvent: OnEvent,
    navController: NavHostController = rememberNavController(),
) {
    val profileViewModel = hiltViewModel<ProfileViewModel>()
    val profileUiState = profileViewModel.profileUiState.collectAsStateWithLifecycle()
    Screen(title = { Text(text = "Profile") }, navController = navController) {
        Surface(
            modifier = Modifier
                .widthIn(max = maxScreenWidth)
        ) {
            Column {
                when (val uiState = profileUiState.value) {
                    ProfileUiState.Loading -> Text(text = stringResource(R.string.loading))
                    is ProfileUiState.Success -> {
                        ProfileSummary(appColors, profileViewModel)
                        PreferencesPanel(uiState.data, onEvent)
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
private fun PreferencesPanel(profileUiState: EditableSettings, onEvent: OnEvent) {
    val theme = profileUiState.theme
    if (theme == Theme.SYSTEM_DEFAULT) {
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
    val preferenceList = listOf(
        Preference(
            title = "Change Theme",
            description = "Theme preferences",
            icon = themeIcon,
        ) {
            onEvent.invoke.invoke(ExpressWalletAppState.ThemeScreen)
        },
    )
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

/*
    Preference(
       title = "Custom Remainder",
       description = "Remainder to update transactions",
       icon = Icons.Filled.Notifications,
   ) {
       navController.navigate(ExpressWalletAppState.RemainderScreen.route)
   }
 */

@Composable
fun PreferenceItem(preference: Preference) {
    Row(
        modifier = Modifier
            .padding(vertical = 5.dp)
            .clickable(onClick = preference.clickEvent)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = preference.icon,
            contentDescription = null,
            modifier = Modifier.padding(end = 10.dp),
            tint = appColors.material.onSurfaceVariant,
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
                color = appColors.material.onSurfaceVariant,
            ),
        )
    }
}

@Composable
fun Label(value: String) {
    Text(
        text = value,
        color = appColors.material.onSurfaceVariant,
        style = TextStyle(
            fontSize = 22.sp,
            fontWeight = FontWeight.Medium,
            color = appColors.material.onSurfaceVariant,
        ),
    )
}
