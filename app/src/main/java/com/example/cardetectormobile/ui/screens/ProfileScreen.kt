package com.example.cardetectormobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.cardetectormobile.ui.components.ProfileScreenCard
import com.example.cardetectormobile.ui.viewmodel.ProfileViewModel
import java.util.Locale

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onGoToHistory: () -> Unit,
    onGoToMap: () -> Unit,
    onLogoutClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Box(
            ){
                Icon(
                    Icons.Default.AccountCircle,
                    contentDescription = "Account Icon",
                    modifier = Modifier.size(80.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            Box{
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = "${uiState.firstName ?: "Usuario"} ${uiState.lastName ?: "Desconocido"}".trim(),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Rol: ${
                            uiState.role?.replaceFirstChar {
                                if (it.isLowerCase()) it.titlecase(
                                    Locale.getDefault()
                                ) else it.toString()
                            } ?: "---"}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
        ProfileScreenCard(
            content = "Datos Personales",
            icon = Icons.Default.Info,
            iconContentDescription = "Personal Data Info"
        )
        ProfileScreenCard(
            content = "Ver Actividad",
            icon = Icons.Default.Menu,
            iconContentDescription = "Personal Data Info"
        )
        ProfileScreenCard(
            content = "Cambiar Tema",
            icon = Icons.Default.Build,
            iconContentDescription = "Personal Data Info"
        )
        ProfileScreenCard(
            content = "Borrar Historial de Detecciones",
            icon = Icons.Default.Delete,
            iconContentDescription = "Personal Data Info"
        )
        ProfileScreenCard(
            content = "Borrar Cuenta",
            icon = Icons.Default.Close,
            iconContentDescription = "Personal Data Info"
        )
        Spacer(modifier = Modifier.weight(1f))

            OutlinedButton(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE34F4F)
                ),
                onClick = {
                    viewModel.logout()
                    onLogoutClick()
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    "Cerrar sesión",
                    color = Color.White
                )
            }

        if (uiState.isLoading) {
            Text(
                text = "Cargando...",
                style = MaterialTheme.typography.bodySmall
            )
        }

        uiState.errorMessage?.let { error ->
            Text(
                text = error,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}
