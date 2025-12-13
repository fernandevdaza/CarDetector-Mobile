package com.example.cardetectormobile.ui.navigation

sealed class HomeRoutes(val route: String) {
    data object PersonalData : HomeRoutes("personal_data")
    data object Activity : HomeRoutes("activity")
    data object RequestsConfig : HomeRoutes("requests_config")
}
