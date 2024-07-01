package com.devstudio.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import com.devstudio.theme.md_theme_dark_background
import com.devstudio.theme.md_theme_dark_error
import com.devstudio.theme.md_theme_dark_errorContainer
import com.devstudio.theme.md_theme_dark_expense_icon_tint
import com.devstudio.theme.md_theme_dark_income_icon_tint
import com.devstudio.theme.md_theme_dark_inverseOnSurface
import com.devstudio.theme.md_theme_dark_inversePrimary
import com.devstudio.theme.md_theme_dark_inverseSurface
import com.devstudio.theme.md_theme_dark_onBackground
import com.devstudio.theme.md_theme_dark_onError
import com.devstudio.theme.md_theme_dark_onErrorContainer
import com.devstudio.theme.md_theme_dark_onPrimary
import com.devstudio.theme.md_theme_dark_onPrimaryContainer
import com.devstudio.theme.md_theme_dark_onSecondary
import com.devstudio.theme.md_theme_dark_onSecondaryContainer
import com.devstudio.theme.md_theme_dark_onSurface
import com.devstudio.theme.md_theme_dark_onSurfaceVariant
import com.devstudio.theme.md_theme_dark_onTertiary
import com.devstudio.theme.md_theme_dark_onTertiaryContainer
import com.devstudio.theme.md_theme_dark_outline
import com.devstudio.theme.md_theme_dark_outlineVariant
import com.devstudio.theme.md_theme_dark_primary
import com.devstudio.theme.md_theme_dark_primaryContainer
import com.devstudio.theme.md_theme_dark_scrim
import com.devstudio.theme.md_theme_dark_secondary
import com.devstudio.theme.md_theme_dark_secondaryContainer
import com.devstudio.theme.md_theme_dark_surface
import com.devstudio.theme.md_theme_dark_surfaceTint
import com.devstudio.theme.md_theme_dark_surfaceVariant
import com.devstudio.theme.md_theme_dark_tertiary
import com.devstudio.theme.md_theme_dark_tertiaryContainer
import com.devstudio.theme.md_theme_dark_transaction_expense_container
import com.devstudio.theme.md_theme_dark_transaction_income_container
import com.devstudio.theme.md_theme_light_background
import com.devstudio.theme.md_theme_light_error
import com.devstudio.theme.md_theme_light_errorContainer
import com.devstudio.theme.md_theme_light_expense_icon_tint
import com.devstudio.theme.md_theme_light_income_icon_tint
import com.devstudio.theme.md_theme_light_inverseOnSurface
import com.devstudio.theme.md_theme_light_inversePrimary
import com.devstudio.theme.md_theme_light_inverseSurface
import com.devstudio.theme.md_theme_light_onBackground
import com.devstudio.theme.md_theme_light_onError
import com.devstudio.theme.md_theme_light_onErrorContainer
import com.devstudio.theme.md_theme_light_onPrimary
import com.devstudio.theme.md_theme_light_onPrimaryContainer
import com.devstudio.theme.md_theme_light_onSecondary
import com.devstudio.theme.md_theme_light_onSecondaryContainer
import com.devstudio.theme.md_theme_light_onSurface
import com.devstudio.theme.md_theme_light_onSurfaceVariant
import com.devstudio.theme.md_theme_light_onTertiary
import com.devstudio.theme.md_theme_light_onTertiaryContainer
import com.devstudio.theme.md_theme_light_outline
import com.devstudio.theme.md_theme_light_outlineVariant
import com.devstudio.theme.md_theme_light_primary
import com.devstudio.theme.md_theme_light_primaryContainer
import com.devstudio.theme.md_theme_light_scrim
import com.devstudio.theme.md_theme_light_secondary
import com.devstudio.theme.md_theme_light_secondaryContainer
import com.devstudio.theme.md_theme_light_surface
import com.devstudio.theme.md_theme_light_surfaceTint
import com.devstudio.theme.md_theme_light_surfaceVariant
import com.devstudio.theme.md_theme_light_tertiary
import com.devstudio.theme.md_theme_light_tertiaryContainer
import com.devstudio.theme.md_theme_light_transaction_expense_container
import com.devstudio.theme.md_theme_light_transaction_income_container
import com.devstudio.theme.model.AppColor

private val LightColors = lightColorScheme(
    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,
    primaryContainer = md_theme_light_primaryContainer,
    onPrimaryContainer = md_theme_light_onPrimaryContainer,
    secondary = md_theme_light_secondary,
    onSecondary = md_theme_light_onSecondary,
    secondaryContainer = md_theme_light_secondaryContainer,
    onSecondaryContainer = md_theme_light_onSecondaryContainer,
    tertiary = md_theme_light_tertiary,
    onTertiary = md_theme_light_onTertiary,
    tertiaryContainer = md_theme_light_tertiaryContainer,
    onTertiaryContainer = md_theme_light_onTertiaryContainer,
    error = md_theme_light_error,
    errorContainer = md_theme_light_errorContainer,
    onError = md_theme_light_onError,
    onErrorContainer = md_theme_light_onErrorContainer,
    background = md_theme_light_background,
    onBackground = md_theme_light_onBackground,
    surface = md_theme_light_surface,
    onSurface = md_theme_light_onSurface,
    surfaceVariant = md_theme_light_surfaceVariant,
    onSurfaceVariant = md_theme_light_onSurfaceVariant,
    outline = md_theme_light_outline,
    inverseOnSurface = md_theme_light_inverseOnSurface,
    inverseSurface = md_theme_light_inverseSurface,
    inversePrimary = md_theme_light_inversePrimary,
    surfaceTint = md_theme_light_surfaceTint,
    outlineVariant = md_theme_light_outlineVariant,
    scrim = md_theme_light_scrim,
)

private val DarkColors = darkColorScheme(
    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,
    primaryContainer = md_theme_dark_primaryContainer,
    onPrimaryContainer = md_theme_dark_onPrimaryContainer,
    secondary = md_theme_dark_secondary,
    onSecondary = md_theme_dark_onSecondary,
    secondaryContainer = md_theme_dark_secondaryContainer,
    onSecondaryContainer = md_theme_dark_onSecondaryContainer,
    tertiary = md_theme_dark_tertiary,
    onTertiary = md_theme_dark_onTertiary,
    tertiaryContainer = md_theme_dark_tertiaryContainer,
    onTertiaryContainer = md_theme_dark_onTertiaryContainer,
    error = md_theme_dark_error,
    errorContainer = md_theme_dark_errorContainer,
    onError = md_theme_dark_onError,
    onErrorContainer = md_theme_dark_onErrorContainer,
    background = md_theme_dark_background,
    onBackground = md_theme_dark_onBackground,
    surface = md_theme_dark_surface,
    onSurface = md_theme_dark_onSurface,
    surfaceVariant = md_theme_dark_surfaceVariant,
    onSurfaceVariant = md_theme_dark_onSurfaceVariant,
    outline = md_theme_dark_outline,
    inverseOnSurface = md_theme_dark_inverseOnSurface,
    inverseSurface = md_theme_dark_inverseSurface,
    inversePrimary = md_theme_dark_inversePrimary,
    surfaceTint = md_theme_dark_surfaceTint,
    outlineVariant = md_theme_dark_outlineVariant,
    scrim = md_theme_dark_scrim,
)

@Composable
fun AppMaterialTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content:
    @Composable () -> Unit,
) {
    val colors = if (!useDarkTheme) {
        lightPalette
    } else {
        darkPalette
    }

    CompositionLocalProvider(LocalColors provides colors) {
        MaterialTheme(
            colorScheme = colors.material,
            content = content,
            typography = ExpressWalletTypography,
        )
    }
}

val lightPalette =
    AppColor(
        material = LightColors,
        transactionIncomeColor = md_theme_light_transaction_income_container,
        transactionExpenseColor = md_theme_light_transaction_expense_container,
        incomeIconTint = md_theme_light_income_icon_tint,
        expenseIconTint = md_theme_light_expense_icon_tint,
        transactionInvestmentColor = md_theme_light_tertiary,
    )

val darkPalette =
    AppColor(
        material = DarkColors,
        transactionIncomeColor = md_theme_dark_transaction_income_container,
        transactionExpenseColor = md_theme_dark_transaction_expense_container,
        incomeIconTint = md_theme_dark_income_icon_tint,
        expenseIconTint = md_theme_dark_expense_icon_tint,
        transactionInvestmentColor = md_theme_dark_tertiary,
    )

private val LocalColors = staticCompositionLocalOf { lightPalette }
val appColors: AppColor
    @Composable
    @ReadOnlyComposable
    get() = LocalColors.current
