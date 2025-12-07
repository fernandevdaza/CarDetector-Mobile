package com.example.cardetectormobile.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.cardetectormobile.di.AppContainer
import com.example.cardetectormobile.ui.navigation.BottomNavItem
import com.example.cardetectormobile.ui.viewmodel.DetectionViewModel
import kotlinx.coroutines.selects.select

@OptIn(ExperimentalMaterial3Api::class)
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

    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val currentTitle = items.find { it.route == currentDestination?.route }?.title ?: "Car Detector"

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = currentTitle,
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Start,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = Color.Black,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = Color.Black
            ){
                val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                items.forEach { screen ->
                NavigationBarItem(
                    icon = { Icon(screen.icon, contentDescription = screen.title)},
                    label = { Text(screen.title, color = MaterialTheme.colorScheme.onSurface)},
                    selected = currentDestination?.hierarchy?.any { it.route == screen.route} == true,
                    onClick = {
                        bottomNavController.navigate(screen.route){
                            popUpTo(bottomNavController.graph.findStartDestination().id){
                                saveState = true
                                }
                            launchSingleTop = true
                            restoreState = true
                            }
                        },
                    colors = NavigationBarItemColors(
                        selectedIconColor = Color.White,
                        selectedTextColor = Color.Black,
                        selectedIndicatorColor = Color.DarkGray,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurface,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledIconColor = MaterialTheme.colorScheme.onSurface,
                        disabledTextColor = MaterialTheme.colorScheme.onSurface
                        )
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
                    val detectionViewModel: DetectionViewModel = viewModel(
                        factory = viewModelFactory {
                            initializer {
                                DetectionViewModel(
                                    repository = appContainer.carRepository
                                )
                            }
                        }
                    )
                    DetectionScreen(viewModel = detectionViewModel)
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