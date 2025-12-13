package com.example.cardetectormobile.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cardetectormobile.ui.components.BackButton
import com.example.cardetectormobile.ui.components.ProfileTextField
import com.example.cardetectormobile.ui.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalDataScreen(
    viewModel: ProfileViewModel,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var firstName by remember { mutableStateOf(uiState.firstName ?: "") }
    var lastName by remember { mutableStateOf(uiState.lastName ?: "") }
    var email by remember { mutableStateOf(uiState.email ?: "") }

    LaunchedEffect(uiState.isLoading) {
        if (!uiState.isLoading) {
            firstName = uiState.firstName ?: ""
            lastName = uiState.lastName ?: ""
            email = uiState.email ?: ""
        }
    }

    val isModified = firstName != (uiState.firstName ?: "") ||
            lastName != (uiState.lastName ?: "") ||
            email != (uiState.email ?: "")

    LaunchedEffect(uiState.updateSuccess) {
        if (uiState.updateSuccess) {
            Toast.makeText(context, "Datos actualizados", Toast.LENGTH_SHORT).show()
            viewModel.resetUpdateSuccess() // Reset state
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearError()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        ProfileTextField(
            label = "Nombres",
            value = firstName,
            onValueChange = { firstName = it }
        )
        ProfileTextField(
            label = "Apellidos",
            value = lastName,
            onValueChange = { lastName = it }
        )
        ProfileTextField(
            label = "Email",
            value = email,
            onValueChange = { email = it }
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                viewModel.updateUserData(firstName, lastName, email)
            },
            enabled = isModified && !uiState.isLoading,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                disabledContainerColor = Color.Gray
            )
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White
                )
            } else {
                Text("Guardar")
            }
        }
    }
}


