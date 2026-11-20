package com.harun.splitledger.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Light Travel-Fintech Palette (Daylight / Cafe / Airport Legibility)
private val LightBackground = Color(0xFFF6F4EE)
private val LightSurface = Color(0xFFF6F4EE)
private val LightSurfaceLowest = Color(0xFFFFFFFF)
private val LightSurfaceLow = Color(0xFFF0EDE5)
private val LightSurfaceContainer = Color(0xFFE9E5DB)
private val LightSurfaceHigh = Color(0xFFE1DDD2)
private val LightOnSurface = Color(0xFF121820)
private val LightOnSurfaceVariant = Color(0xFF4B5563)
private val LightOutline = Color(0xFF8C877B)
private val LightOutlineVariant = Color(0xFFDDD8CC)

private val LightPrimary = Color(0xFF0D6E6E)
private val LightOnPrimary = Color(0xFFFFFFFF)
private val LightPrimaryContainer = Color(0xFFD0ECEB)
private val LightOnPrimaryContainer = Color(0xFF053939)
private val LightSecondary = Color(0xFF3C5364)
private val LightOnSecondary = Color(0xFFFFFFFF)
private val LightSecondaryContainer = Color(0xFFDCE7F0)
private val LightOnSecondaryContainer = Color(0xFF162936)
private val LightError = Color(0xFFB83B1D)
private val LightOnError = Color(0xFFFFFFFF)
private val LightErrorContainer = Color(0xFFFCE4DC)
private val LightOnErrorContainer = Color(0xFF5D1908)

// Dark Travel-Fintech Palette (Night Flight / Hotel Room First-Class Scheme)
private val DarkBackground = Color(0xFF0D1217)
private val DarkSurface = Color(0xFF0D1217)
private val DarkSurfaceLowest = Color(0xFF0A0E12)
private val DarkSurfaceLow = Color(0xFF141B23)
private val DarkSurfaceContainer = Color(0xFF1B242F)
private val DarkSurfaceHigh = Color(0xFF232F3D)
private val DarkOnSurface = Color(0xFFEEF2F6)
private val DarkOnSurfaceVariant = Color(0xFF9CA8B7)
private val DarkOutline = Color(0xFF637080)
private val DarkOutlineVariant = Color(0xFF273342)

private val DarkPrimary = Color(0xFF4DD0C8)
private val DarkOnPrimary = Color(0xFF003735)
private val DarkPrimaryContainer = Color(0xFF0E4D4B)
private val DarkOnPrimaryContainer = Color(0xFFBDF5F0)
private val DarkSecondary = Color(0xFFA7C2D6)
private val DarkOnSecondary = Color(0xFF122837)
private val DarkSecondaryContainer = Color(0xFF233A4B)
private val DarkOnSecondaryContainer = Color(0xFFDCE7F0)
private val DarkError = Color(0xFFFF8A65)
private val DarkOnError = Color(0xFF4C1506)
private val DarkErrorContainer = Color(0xFF421F15)
private val DarkOnErrorContainer = Color(0xFFFFD8CC)

private val SplitLedgerLightColorScheme = lightColorScheme(
    primary = LightPrimary,
    onPrimary = LightOnPrimary,
    primaryContainer = LightPrimaryContainer,
    onPrimaryContainer = LightOnPrimaryContainer,
    secondary = LightSecondary,
    onSecondary = LightOnSecondary,
    secondaryContainer = LightSecondaryContainer,
    onSecondaryContainer = LightOnSecondaryContainer,
    background = LightBackground,
    onBackground = LightOnSurface,
    surface = LightSurface,
    onSurface = LightOnSurface,
    surfaceVariant = LightSurfaceContainer,
    onSurfaceVariant = LightOnSurfaceVariant,
    surfaceContainerLowest = LightSurfaceLowest,
    surfaceContainerLow = LightSurfaceLow,
    surfaceContainer = LightSurfaceContainer,
    surfaceContainerHigh = LightSurfaceHigh,
    outline = LightOutline,
    outlineVariant = LightOutlineVariant,
    error = LightError,
    onError = LightOnError,
    errorContainer = LightErrorContainer,
    onErrorContainer = LightOnErrorContainer,
)

private val SplitLedgerDarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkOnPrimaryContainer,
    secondary = DarkSecondary,
    onSecondary = DarkOnSecondary,
    secondaryContainer = DarkSecondaryContainer,
    onSecondaryContainer = DarkOnSecondaryContainer,
    background = DarkBackground,
    onBackground = DarkOnSurface,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceContainer,
    onSurfaceVariant = DarkOnSurfaceVariant,
    surfaceContainerLowest = DarkSurfaceLowest,
    surfaceContainerLow = DarkSurfaceLow,
    surfaceContainer = DarkSurfaceContainer,
    surfaceContainerHigh = DarkSurfaceHigh,
    outline = DarkOutline,
    outlineVariant = DarkOutlineVariant,
    error = DarkError,
    onError = DarkOnError,
    errorContainer = DarkErrorContainer,
    onErrorContainer = DarkOnErrorContainer,
)

@Immutable
data class SplitLedgerSemanticColors(
    val credit: Color,
    val creditContainer: Color,
    val onCreditContainer: Color,
    val debt: Color,
    val debtContainer: Color,
    val onDebtContainer: Color,
    val warning: Color,
    val warningContainer: Color,
    val onWarningContainer: Color,
    val avatarColors: List<Color>,
)

private val LightSemanticColors = SplitLedgerSemanticColors(
    credit = Color(0xFF116A4B),
    creditContainer = Color(0xFFDCF3E9),
    onCreditContainer = Color(0xFF0A432F),
    debt = Color(0xFFB83B1D),
    debtContainer = Color(0xFFFCE4DC),
    onDebtContainer = Color(0xFF6B1E0B),
    warning = Color(0xFF9A5504),
    warningContainer = Color(0xFFFEF0D5),
    onWarningContainer = Color(0xFF5A3102),
    avatarColors = listOf(
        Color(0xFF0D6E6E),
        Color(0xFF3B5998),
        Color(0xFF8A4F2B),
        Color(0xFF5D4B8A),
        Color(0xFF2F6B4F),
    ),
)

private val DarkSemanticColors = SplitLedgerSemanticColors(
    credit = Color(0xFF4ADEA8),
    creditContainer = Color(0xFF0F372A),
    onCreditContainer = Color(0xFFB7F6DD),
    debt = Color(0xFFFF8A65),
    debtContainer = Color(0xFF421F15),
    onDebtContainer = Color(0xFFFFD8CC),
    warning = Color(0xFFF6B84C),
    warningContainer = Color(0xFF3B2908),
    onWarningContainer = Color(0xFFFCE4B3),
    avatarColors = listOf(
        Color(0xFF2AA198),
        Color(0xFF668FD6),
        Color(0xFFD68557),
        Color(0xFF9E8AD4),
        Color(0xFF55A87E),
    ),
)

val LocalSplitLedgerColors = staticCompositionLocalOf { LightSemanticColors }

val SplitLedgerTypography = Typography(
    displaySmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 36.sp,
        lineHeight = 42.sp,
        letterSpacing = (-0.75).sp,
        fontFeatureSettings = "tnum",
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 34.sp,
        letterSpacing = (-0.4).sp,
        fontFeatureSettings = "tnum",
    ),
    headlineSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = (-0.2).sp,
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        lineHeight = 26.sp,
        letterSpacing = (-0.15).sp,
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.sp,
    ),
    titleSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 22.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.2.sp,
    ),
)

fun TextStyle.tabular(): TextStyle = copy(fontFeatureSettings = "tnum")

object SplitLedgerTheme {
    val semanticColors: SplitLedgerSemanticColors
        @Composable get() = LocalSplitLedgerColors.current
}

@Composable
fun SplitLedgerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) SplitLedgerDarkColorScheme else SplitLedgerLightColorScheme
    val semanticColors = if (darkTheme) DarkSemanticColors else LightSemanticColors

    CompositionLocalProvider(LocalSplitLedgerColors provides semanticColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = SplitLedgerTypography,
            content = content,
        )
    }
}
