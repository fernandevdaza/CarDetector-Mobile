package com.example.cardetectormobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.cardetectormobile.ui.components.ProfileScreenCard
import com.example.cardetectormobile.ui.viewmodel.ProfileViewModel
import java.util.Locale
import androidx.compose.foundation.isSystemInDarkTheme

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onGoToHistory: () -> Unit,
    onGoToMap: () -> Unit,
    onLogoutClick: () -> Unit,
    onGoToPersonalData: () -> Unit,
    onGoToActivity: () -> Unit,
    onGoToRequestsConfig: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    
    val isSystemDark = isSystemInDarkTheme()
    val effectiveTheme = uiState.isDarkTheme ?: isSystemDark

    var showDeleteAccountDialog by remember { mutableStateOf(false) }
    var showDeleteHistoryDialog by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }

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
            iconContentDescription = "Personal Data Info",
            onClick = onGoToPersonalData
        )
        ProfileScreenCard(
            content = "Ver Actividad",
            icon = Icons.Default.Menu,
            iconContentDescription = "Personal Data Info",
            onClick = onGoToActivity
        )


// ...

        ProfileScreenCard(
            content = if (effectiveTheme) "Cambiar a Modo Claro" else "Cambiar a Modo Oscuro",
            icon = Icons.Default.Edit,
            iconContentDescription = "Change Theme",
            onClick = { viewModel.toggleTheme(isSystemDark) }
        )
        if (uiState.role == "admin") {
            ProfileScreenCard(
                content = "Configurar Límite de Requests",
                icon = Icons.Default.Build,
                iconContentDescription = "Config Requests",
                onClick = onGoToRequestsConfig
            )
        }
        ProfileScreenCard(
            content = "Borrar Historial de Detecciones",
            icon = Icons.Default.Delete,
            iconContentDescription = "Personal Data Info",
            onClick = { showDeleteHistoryDialog = true }
        )
        ProfileScreenCard(
            content = "Borrar Cuenta",
            icon = Icons.Default.Close,
            iconContentDescription = "Personal Data Info",
            onClick = { showDeleteAccountDialog = true }
        )

        Spacer(modifier = Modifier.weight(1f))

            OutlinedButton(
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFE34F4F)
                ),
                onClick = {
                    showLogoutDialog = true
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

    if (showDeleteAccountDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showDeleteAccountDialog = false },
            title = { Text("Eliminar Cuenta",
                color = MaterialTheme.colorScheme.onSurface) },
            text = { Text("¿Estás seguro de que deseas eliminar tu cuenta? Esta acción no se puede deshacer.",
                color = MaterialTheme.colorScheme.onSurface) },
            confirmButton = {
                androidx.compose.material3.TextButton(
                    onClick = {
                        showDeleteAccountDialog = false
                        viewModel.deleteAccount {
                            onLogoutClick()
                        }
                    }
                ) {
                    Text("Eliminar", color = Color.Red)
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(onClick = { showDeleteAccountDialog = false }) {
                    Text("Cancelar",
                        color = MaterialTheme.colorScheme.onSurface)
                }
            },
            containerColor = MaterialTheme.colorScheme.surface

        )
    }

    if (showDeleteHistoryDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showDeleteHistoryDialog = false },
            title = { Text("Eliminar Historial",
                color = MaterialTheme.colorScheme.onSurface) },
            text = { Text("¿Estás seguro de que deseas eliminar todo tu historial de detecciones?",
                color = MaterialTheme.colorScheme.onSurface) },
            confirmButton = {
                androidx.compose.material3.TextButton(
                    onClick = {
                        showDeleteHistoryDialog = false
                        viewModel.deleteHistory()
                    }
                ) {
                    Text("Eliminar", color = Color.Red)
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(onClick = { showDeleteHistoryDialog = false }) {
                    Text("Cancelar",
                        color = MaterialTheme.colorScheme.onSurface)
                }
            },
            containerColor = MaterialTheme.colorScheme.surface
        )
    }

    if (showLogoutDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = {
                Text(
                "Cerrar Sesión",
                color = MaterialTheme.colorScheme.onSurface
                )
                    },
            text = {
                Text(
                    "¿Estás seguro de que deseas cerrar sesión?",
                    color = MaterialTheme.colorScheme.onSurface
                ) },
            confirmButton = {
                androidx.compose.material3.TextButton(
                    onClick = {
                        showLogoutDialog = false
                        viewModel.logout()
                        onLogoutClick()
                    }
                ) {
                    Text("Cerrar Sesión", color = Color.Red)
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(onClick = { showLogoutDialog = false }) {
                    Text(
                        "Cancelar",
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            },
            containerColor = MaterialTheme.colorScheme.surface

        )
    }
}
