package com.devstudio.profile.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.devstudio.profile.viewmodels.ProfileViewModel
import com.devstudio.theme.DEFAULT_CARD_CORNER_RADIUS
import com.devstudio.theme.model.AppColor

@Composable
@OptIn(ExperimentalLayoutApi::class)
fun ProfileSummary(
    appColors: AppColor,
    profileViewModel: ProfileViewModel,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = appColors.material.tertiaryContainer,
                shape = RoundedCornerShape(DEFAULT_CARD_CORNER_RADIUS),
            )
            .padding(bottom = 10.dp),
    ) {
        FlowRow(
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier
                .background(color = appColors.material.tertiaryContainer)
                .padding(10.dp)
                .align(
                    Alignment.CenterHorizontally,
                ),
        ) {
            ProfileInfoCardElement(
                value = profileViewModel.getTotalAssets().toString(),
                Icons.Filled.Money,
                "Total Assets",
            )
            ProfileInfoCardElement(
                value = profileViewModel.getTotalNumberOfTransactions().toString(),
                Icons.Filled.Numbers,
                "Total Transactions",
            )
        }
    }
}

@Composable
private fun ProfileInfoCardElement(value: String, icon: ImageVector, title: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth(.5f),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Image(
            imageVector = icon,
            contentDescription = title,
            modifier = Modifier
                .width(25.dp)
                .height(25.dp)
                .align(Alignment.CenterHorizontally),
        )
        Text(text = value, modifier = Modifier.padding(top = 5.dp))
        Text(text = title, modifier = Modifier.padding(top = 5.dp))
    }
}
