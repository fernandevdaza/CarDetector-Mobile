package com.example.cardetectormobile.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryBlue,
    background = MidnightBlue,
    surface = DarkSlate,
    onBackground = TextWhite,
    onSurface = TextWhite,
    outline = SteelBlue,
    error = ErrorRed
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    background = WhiteSmoke,
    surface = PureWhite,
    onBackground = Color.Black,
    onSurface = MutedGrayLight, // Or MutedGrayLight for secondary text? Standard text should be blackish.
    outline = TransparentGray,
    error = ErrorRed
)

@Composable
fun CarDetectorMobileTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = CarDetectorTypography,
        content = content
    )
}