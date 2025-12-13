package com.example.cardetectormobile.ui.theme

// ui/theme/Type.kt
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.dp

// Puedes definir una fontFamily global aquí si importas una fuente de Google Fonts
val AppFontFamily = FontFamily.Default

val CarDetectorTypography = Typography(
    // Estilo para el texto normal (Inputs)
    bodyLarge = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    // Estilo para las etiquetas (MARCA, MODELO)
    labelSmall = TextStyle(
        fontFamily = AppFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        letterSpacing = 1.sp // Espaciado entre letras como en tu diseño
    )
)