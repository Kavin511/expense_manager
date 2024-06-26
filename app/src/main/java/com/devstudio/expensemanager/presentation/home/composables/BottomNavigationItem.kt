package com.devstudio.expensemanager.presentation.home.composables

import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavigationItem(
    val name: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val navigationRoute: String,
)
