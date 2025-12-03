package com.example.cardetectormobile.ui.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.cardetectormobile.di.AppContainer
import com.example.cardetectormobile.ui.screens.LoginScreen
import com.example.cardetectormobile.ui.screens.OnboardingScreen
import com.example.cardetectormobile.ui.viewmodel.LoginViewModel

@Composable
fun AppNavigation(
    appContainer: AppContainer,
    startDestination: String,
){
    val navController = rememberNavController()

    NavHost (navController = navController, startDestination = startDestination){
        composable("onboarding"){
            OnboardingScreen(
                onLoginClick = {
                    navController.navigate("login")
                },
                onRegisterClick = {
                    navController.navigate("register")
                }
            )
        }

        composable("login"){
            val loginViewModel: LoginViewModel = viewModel(
                factory = viewModelFactory {
                    initializer {
                        LoginViewModel(
                            repository = appContainer.authRepository,
                            sessionManager = appContainer.sessionManager
                        )
                    }
                }
            )
            LoginScreen(
                viewModel = loginViewModel,
                onLoginSuccess = {
                    navController.navigate("home"){
                        popUpTo("onboarding"){ inclusive = true}
                    }
                }
            )
        }

        composable("register"){
            Text("¡Bienvenido! Aquí iría la pantalla de registro")
        }

        composable("home"){
            Text("¡Bienvenido! Aquí iría la pantalla principal")
        }
    }

}