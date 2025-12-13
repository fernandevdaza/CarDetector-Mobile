package com.example.cardetectormobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cardetectormobile.ui.viewmodel.ProfileViewModel

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
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // =========== CARD: INFO BÁSICA DE CUENTA ===========
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = uiState.userId?.let { "Usuario $it" } ?: "Usuario",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = "ID: ${uiState.userId ?: "---"}",
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = "Rol: ${uiState.role ?: "---"}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        // =========== CARD: ACTIVIDAD ===========
        Card(
            modifier = Modifier
                .fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text("Actividad", style = MaterialTheme.typography.titleMedium)

                Text(
                    text = "Detecciones totales: ${uiState.totalDetections}",
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = "Última detección: ${uiState.lastDetectionTime ?: "—"}",
                    style = MaterialTheme.typography.bodySmall
                )

                if (uiState.lastLat != null && uiState.lastLon != null) {
                    Text(
                        text = "Última ubicación: " +
                                "Lat %.5f, Lon %.5f".format(uiState.lastLat, uiState.lastLon),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                }
            }
        }

        // =========== CARD: ACCESOS RÁPIDOS ===========
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            shape = MaterialTheme.shapes.large,
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text("Accesos rápidos", style = MaterialTheme.typography.titleMedium)

                Button(
                    onClick = onGoToHistory,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Ver historial de detecciones")
                }

                Button(
                    onClick = onGoToMap,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Ver mapa de detecciones")
                }

                Spacer(modifier = Modifier.weight(1f))

                OutlinedButton(
                    onClick = {
                        viewModel.logout()
                        onLogoutClick()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cerrar sesión")
                }
            }
        }

        if (uiState.isLoading) {
            // Overlay simple de "cargando" abajo del todo
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
