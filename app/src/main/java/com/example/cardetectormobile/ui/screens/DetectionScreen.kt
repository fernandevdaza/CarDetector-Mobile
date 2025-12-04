package com.example.cardetectormobile.ui.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cardetectormobile.ui.components.DetectionDetailField
@Composable
fun DetectionScreen(){
    var brand by remember { mutableStateOf("") }
    var model by remember { mutableStateOf("") }
    var year by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState()) // Habilita scroll
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                .clickable { /* Lógica para abrir galería/cámara */ },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Aún no se ha subido ninguna imagen",
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        DetectionDetailField(
            label = "Marca",
            value = brand,
            onValueChange = { brand = it }
        )

        DetectionDetailField(
            label = "Modelo",
            value = model,
            onValueChange = { model = it }
        )

        DetectionDetailField(
            label = "Año aproximado",
            value = year,
            onValueChange = { year = it },
            keyboardType = KeyboardType.Number
        )
    }
}