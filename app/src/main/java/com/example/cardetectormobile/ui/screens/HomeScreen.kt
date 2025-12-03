package com.example.cardetectormobile.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.cardetectormobile.di.AppContainer
import com.example.cardetectormobile.ui.navigation.BottomNavItem

@Composable
fun HomeScreen(
    appContainer: AppContainer,
    onLogoutClick: () -> Unit
){
    val bottomNavController = rememberNavController()
    val items = listOf(
        BottomNavItem.Detection,
        BottomNavItem.History,
        BottomNavItem.Map,
        BottomNavItem.Profile
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
            ){
                val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                items.forEach { screen ->
                NavigationBarItem(
                    icon = { Icon(screen.icon, contentDescription = screen.title)},
                    label = { Text(screen.title)},
                    selected = currentDestination?.hierarchy?.any { it.route == screen.route} == true,
                    onClick = {
                        bottomNavController.navigate(screen.route){
                            popUpTo(bottomNavController.graph.findStartDestination().id){
                                saveState = true
                                }
                            launchSingleTop = true
                            restoreState = true
                            }
                        }
                    )
                }
            }
        },
        modifier = Modifier.fillMaxSize()
    ) {
        innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(innerPadding)
        ){
            NavHost(
                navController = bottomNavController,
                startDestination = BottomNavItem.Detection.route
            ){
                composable(BottomNavItem.Detection.route){
                    DetectionScreen()
                }

                composable(BottomNavItem.History.route){
                    HistoryScreen()
                }

                composable(BottomNavItem.Map.route){
                    MapScreen()
                }

                composable(BottomNavItem.Profile.route){
                    ProfileScreen(
                        onLogoutClick = {
                            onLogoutClick()
                        }
                    )
                }
            }
        }
    }

}