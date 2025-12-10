package com.example.cardetectormobile.ui.screens


import android.R
import android.net.Uri
import com.example.cardetectormobile.utils.FileUtils
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.cardetectormobile.ui.components.DetectionDetailField
import com.example.cardetectormobile.ui.viewmodel.DetectionViewModel

@Composable
fun DetectionScreen(
    viewModel: DetectionViewModel
){
    val context = LocalContext.current
    val fileUtils = remember { FileUtils(context) }
    val uiState by viewModel.uiState.collectAsState()

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var showOptionalDialog by remember { mutableStateOf(false) }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) {
        success ->
        if (success && tempCameraUri != null){
            imageUri = tempCameraUri
            viewModel.uploadImage(tempCameraUri!!, context)
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) {
        uri ->
        if (uri != null){
            imageUri = uri
            viewModel.uploadImage(uri, context)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        isGranted ->
        if (isGranted) {
            val uri = fileUtils.createTempPictureUri()
            tempCameraUri = uri
            cameraLauncher.launch(uri)
        }
    }

    if (showOptionalDialog){
        AlertDialog(
            onDismissRequest = { showOptionalDialog = false },
            title = { Text("Seleccionar Imagen")},
            text = { Text("¿Desde dónde quieres subir la foto?")},
            confirmButton = {
                TextButton(onClick = {
                    showOptionalDialog = false
                    galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }) { Text("Galería") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showOptionalDialog = false
                    permissionLauncher.launch(android.Manifest.permission.CAMERA)
                }) { Text("Cámara")}
            }

        )
    }


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
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp))
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                .clickable { showOptionalDialog = true },
            contentAlignment = Alignment.Center
        ) {
            if (imageUri != null){
                AsyncImage(
                    model = imageUri,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            } else {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.AddCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Toca para añadir una foto", color = MaterialTheme.colorScheme.onSurface)
                }
            }

            if (uiState.isLoading) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        DetectionDetailField(
            label = "Marca",
            value = uiState.result?.message?.brand ?: "---",
            onValueChange = {}
        )

        DetectionDetailField(
            label = "Modelo",
            value = uiState.result?.message?.modelName ?: "---",
            onValueChange = {}
        )

        DetectionDetailField(
            label = "Año aproximado",
            value = uiState.result?.message?.year?.toString() ?: "---",
            onValueChange = { }
        )

        Spacer(modifier = Modifier.height(16.dp))

        val hasData = imageUri != null || uiState.result != null

        OutlinedButton(
            onClick = {
                imageUri = null
                viewModel.clearState()
            },
            modifier = Modifier.align(Alignment.CenterHorizontally),
            enabled = hasData,

            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = if (hasData)  Color.Black else MaterialTheme.colorScheme.outline,
                containerColor = Color(0xFFE34F4F),
                disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
            ),
            border = if (hasData) {
                BorderStroke(1.dp, MaterialTheme.colorScheme.error)
            } else {
                BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f))
            }
        ) {
            Icon(Icons.Default.Delete, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Limpiar", color = MaterialTheme.colorScheme.outline)
        }
    }
}