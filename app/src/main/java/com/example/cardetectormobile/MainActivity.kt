package com.example.cardetectormobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.cardetectormobile.data.local.SessionManager
import com.example.cardetectormobile.data.network.RetrofitClient
import com.example.cardetectormobile.di.AppContainer
import com.example.cardetectormobile.domain.repository.AuthRepository
import com.example.cardetectormobile.ui.navigation.AppNavigation
import com.example.cardetectormobile.ui.screens.LoginScreen
import com.example.cardetectormobile.ui.screens.OnboardingScreen
import com.example.cardetectormobile.ui.theme.CarDetectorMobileTheme
import com.example.cardetectormobile.ui.viewmodel.LoginViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val appContainer = AppContainer(applicationContext)

        val token = appContainer.sessionManager.getToken()

        val startDestination = if (!token.isNullOrBlank()) "home" else "onboarding"

        setContent {
            CarDetectorMobileTheme {
                Scaffold (
                    containerColor = MaterialTheme.colorScheme.background,
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    Box (
                        modifier = Modifier.fillMaxSize().padding(innerPadding)
                    ) {
                        AppNavigation(
                            startDestination = startDestination,
                            appContainer = appContainer
                        )
//                        LoginScreen(
//                            viewModel = viewModel,
//                            onLoginSuccess = {
//                                println("Navegar a Home...")
//                            }
//                        )
                    }

                }
            }
        }
    }
}