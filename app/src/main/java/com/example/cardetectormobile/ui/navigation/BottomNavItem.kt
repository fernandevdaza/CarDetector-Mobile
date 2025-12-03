package com.example.cardetectormobile.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
){
    object Detection: BottomNavItem("detection", "Detectar", Icons.Default.Home)
    object History: BottomNavItem("history", "Historial", Icons.AutoMirrored.Default.List)
    object Map: BottomNavItem("map", "Mapa", Icons.Default.LocationOn)
    object Profile: BottomNavItem("profile", "Perfil", Icons.Default.Person)
}