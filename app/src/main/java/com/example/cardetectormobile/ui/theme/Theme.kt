package com.example.cardetectormobile.ui.theme


import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

// Definimos que el esquema oscuro use TUS colores
private val DarkColorPalette = darkColorScheme(
    background = WhiteSmoke,      // Tu color 0F1123
    surface = PureWhite,            // Tu color 15172B
    onBackground = BrightGray,       // Texto sobre fondo
    onSurface = MutedGray,          // Texto secundario sobre tarjetas
    outline = TransparentGray,            // Bordes
    primary = PrimaryBlue
)

@Composable
fun CarDetectorMobileTheme(
    // Por defecto forzamos oscuro porque tu diseño es oscuro,
    // pero podrías detectar el sistema con isSystemInDarkTheme()
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    // Aquí aplicas el esquema
    val colorScheme = if (darkTheme) DarkColorPalette else DarkColorPalette // Podrías crear un LightPalette

    MaterialTheme(
        colorScheme = colorScheme,
        typography = CarDetectorTypography,
        content = content
    )
}