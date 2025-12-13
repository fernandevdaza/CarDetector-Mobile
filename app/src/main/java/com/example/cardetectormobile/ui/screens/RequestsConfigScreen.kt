package com.example.cardetectormobile.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.cardetectormobile.ui.components.ProfileTextField
import com.example.cardetectormobile.ui.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestsConfigScreen(
    viewModel: ProfileViewModel,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var limitInput by remember { mutableStateOf(uiState.maxRequests.toString()) }

    LaunchedEffect(uiState.maxRequests) {
        limitInput = uiState.maxRequests.toString()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
//        Text(
//            " para usuarios:",
//            color = MaterialTheme.colorScheme.onSurface
//        )

        ProfileTextField(
            label = "Límite diario de requests para usuarios",
            value = limitInput,
            onValueChange = {
                if (it.all { char -> char.isDigit() }) {
                    limitInput = it
                }
            },
        )
        Button(
            onClick = {
                val limit = limitInput.toIntOrNull()
                if (limit != null) {
                    viewModel.updateMaxRequests(limit)
                    Toast.makeText(context, "Límite actualizado", Toast.LENGTH_SHORT).show()
                    onBackClick()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar Configuración")
        }
    }
}
