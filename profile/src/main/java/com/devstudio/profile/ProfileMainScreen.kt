package com.devstudio.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material3.Card
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
import com.devstudio.core_model.models.ExpressWalletAppState
import com.devstudioworks.ui.components.Page
import com.devstudioworks.ui.theme.DEFAULT_CARD_CORNER_RADIUS
import com.devstudioworks.ui.theme.appColors
import com.devstudioworks.ui.theme.model.AppColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileMainScreen(navController: NavHostController) {
    val profileViewModel = hiltViewModel<ProfileViewModel>()
    val profileUiState = profileViewModel.profileUiState.collectAsStateWithLifecycle()
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(topAppBarState)
    Page(title = "Profile", navController = navController) {
        Surface(
            modifier = Modifier
                .widthIn(max = 640.dp)
                .padding(10.dp),
        ) {
            Column(
                modifier = Modifier
            ) {
                val uiState = profileUiState.value
                when (uiState) {
                    ProfileUiState.Loading -> Text(text = stringResource(R.string.loading))
                    is ProfileUiState.Success -> {
                        ProfilePanel(appColors, profileViewModel)
                        PreferencesPanel(uiState.data, navController)
                    }
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalLayoutApi::class)
private fun ProfilePanel(
    appColors: AppColor,
    profileViewModel: ProfileViewModel
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = appColors.material.tertiaryContainer,
                shape = RoundedCornerShape(DEFAULT_CARD_CORNER_RADIUS)
            )
            .padding(bottom = 10.dp)
    ) {
        FlowRow(
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier
                .background(color = appColors.material.tertiaryContainer)
                .padding(10.dp)
                .align(
                    Alignment.CenterHorizontally
                )
        ) {
            ProfileInfoCardElement(
                value = profileViewModel.getTotalAssets().toString(),
                Icons.Filled.Money,
                "Total Assets"
            )
            ProfileInfoCardElement(
                value = profileViewModel.getTotalNumberOfTransactions().toString(),
                Icons.Filled.Numbers,
                "Total Transactions"
            )
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
private fun PreferencesPanel(profileUiState: EditableSettings, navController: NavHostController) {
    val preferenceList = listOf<Preference>(
        Preference(
            title = "Change Theme",
            description = "Theme preferences",
            icon = Icons.Filled.DarkMode,
        ) {
            navController.navigate(ExpressWalletAppState.ThemeScreen.route)
        },
        Preference(
            title = "Custom Remainder",
            description = "Remainder to update transactions",
            icon = Icons.Filled.Notifications,
        ) {
            navController.navigate(ExpressWalletAppState.RemainderScreen.route)
        }
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

@Composable
fun PreferenceItem(preference: Preference) {
    Row(
        modifier = Modifier
            .padding(vertical = 5.dp)
            .clickable(onClick = preference.clickEvent)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
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

@Composable
private fun ProfileInfoCardElement(value: String, icon: ImageVector, title: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth(.5f),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            imageVector = icon,
            contentDescription = title,
            modifier = Modifier
                .width(25.dp)
                .height(25.dp)
                .align(Alignment.CenterHorizontally)
        )
        Text(text = value, modifier = Modifier.padding(top = 5.dp))
        Text(text = title, modifier = Modifier.padding(top = 5.dp))
    }
}