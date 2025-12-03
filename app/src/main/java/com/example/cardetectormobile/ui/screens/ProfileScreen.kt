package com.example.cardetectormobile.ui.screens


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ProfileScreen(
    onLogoutClick: () -> Unit
){
    Box(
        modifier = Modifier.fillMaxSize()
    ){
        Text("Pantalla Profile")
        Box(
            modifier = Modifier.fillMaxSize()
        ){
            Button(
                onClick = { onLogoutClick() },
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 8.dp)
            ) {
                Text("Cerrar Sesión")
            }
        }

    }
}