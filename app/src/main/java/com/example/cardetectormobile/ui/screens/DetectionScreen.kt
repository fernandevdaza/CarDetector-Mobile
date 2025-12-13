package com.example.cardetectormobile.ui.screens

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.cardetectormobile.ui.components.DetectionDetailField
import com.example.cardetectormobile.ui.viewmodel.DetectionViewModel
import com.example.cardetectormobile.utils.FileUtils

@Composable
fun DetectionScreen(
    viewModel: DetectionViewModel
) {
    val context = LocalContext.current
    val fileUtils = remember { FileUtils(context) }
    val uiState by viewModel.uiState.collectAsState()
    val imageUri = uiState.imageUri?.let { Uri.parse(it) }

    var showSourceDialog by remember { mutableStateOf(false) }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }

    // ===================== CÁMARA =====================

    var capturedLocation by remember { mutableStateOf<android.location.Location?>(null) }
    var isFetchingLocation by remember { mutableStateOf(false) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempCameraUri != null) {
            // INYECTAR METADATA (Solución Robustez)
            capturedLocation?.let { loc ->
                try {
                    context.contentResolver.openFileDescriptor(tempCameraUri!!, "rw")?.use { pfd ->
                        val exif = androidx.exifinterface.media.ExifInterface(pfd.fileDescriptor)
                        exif.setLatLong(loc.latitude, loc.longitude)
                        exif.saveAttributes()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            viewModel.uploadImage(tempCameraUri!!, context)
        }
        isFetchingLocation = false
    }

    fun requestLocationAndLaunchCamera() {
        isFetchingLocation = true
        val locationManager = context.getSystemService(android.content.Context.LOCATION_SERVICE) as android.location.LocationManager

        try {
            // Callback simple
            val listener = object : android.location.LocationListener {
                override fun onLocationChanged(location: android.location.Location) {
                    locationManager.removeUpdates(this)
                    capturedLocation = location
                    
                    // Launch Camera
                    val uri = fileUtils.createTempPictureUri()
                    tempCameraUri = uri
                    cameraLauncher.launch(uri)
                    isFetchingLocation = false // Camera has launched, but we define "fetching" as "fetching location". 
                    // Actually we want to keep some loading state if needed, but the camera UI takes over.
                }
                override fun onStatusChanged(provider: String?, status: Int, extras: android.os.Bundle?) {}
                override fun onProviderEnabled(provider: String) {}
                override fun onProviderDisabled(provider: String) {}
            }

            val isGpsEnabled = locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)
            val isNetworkEnabled = locationManager.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER)

            if (isGpsEnabled) {
                // Request single update is cleaner if available (API 30+ has getCurrentLocation)
                // We use requestLocationUpdates with cleanup for compatibility.
                locationManager.requestLocationUpdates(android.location.LocationManager.GPS_PROVIDER, 1000L, 0f, listener)
            } else if (isNetworkEnabled) {
                locationManager.requestLocationUpdates(android.location.LocationManager.NETWORK_PROVIDER, 1000L, 0f, listener)
            } else {
                 Toast.makeText(context, "Por favor activa el GPS", Toast.LENGTH_SHORT).show()
                 isFetchingLocation = false
            }
        } catch (e: SecurityException) {
            Toast.makeText(context, "Error de permisos de ubicación", Toast.LENGTH_SHORT).show()
            isFetchingLocation = false
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val cameraGranted = permissions[Manifest.permission.CAMERA] ?: false
        val locationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false

        if (cameraGranted && locationGranted) {
            requestLocationAndLaunchCamera()
        } else {
            Toast.makeText(context, "Se requieren permisos de Cámara y Ubicación", Toast.LENGTH_LONG).show()
        }
    }
    
    // ===================== GALERÍA (OPEN_DOCUMENT) =====================

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri == null) {
            Toast.makeText(
                context,
                "No se pudo obtener la imagen seleccionada",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            // Opcional: mantener permiso persistente
            try {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: SecurityException) {
                // si no es persistible, no pasa nada
            }

            viewModel.uploadImage(uri, context)
        }
    }

    // ===================== DIALOGO SELECCIÓN DE FUENTE =====================

    if (showSourceDialog) {
        AlertDialog(
            onDismissRequest = { showSourceDialog = false },
            title = { Text("Seleccionar imagen") },
            text = { Text("¿Desde dónde quieres subir la foto?") },
            confirmButton = {
                TextButton(onClick = {
                    showSourceDialog = false
                    // Abrimos selector de documentos para imágenes (SAF)
                    galleryLauncher.launch(arrayOf("image/*"))
                }) {
                    Text("Galería")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showSourceDialog = false
                    // Request BOTH permissions
                    cameraPermissionLauncher.launch(
                        arrayOf(Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION)
                    )
                }) {
                    Text("Cámara")
                }
            }
        )
    }

    // ===================== DIALOGO: FALTA METADATA =====================

    if (uiState.metadataMissing) {
        AlertDialog(
            onDismissRequest = {
                viewModel.clearMetadataMissingFlag()
                viewModel.clearState()
            },
            title = { Text("Foto no válida") },
            text = {
                Text(
                    "Esta imagen no contiene información de ubicación en sus metadatos.\n\n" +
                            "Solo se pueden usar fotos tomadas desde un celular " +
                            "que conserven sus datos de ubicación. " +
                            "Por favor, toma una foto nueva o elige otra imagen."
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.clearMetadataMissingFlag()
                    viewModel.clearState()
                }) {
                    Text("Entendido")
                }
            }
        )
    }

    // ===================== UI PRINCIPAL =====================

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
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
                .clickable { showSourceDialog = true },
            contentAlignment = Alignment.Center
        ) {
            if (imageUri != null) {
                AsyncImage(
                    model = imageUri,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.AddCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Toca para añadir una foto",
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            if (uiState.isLoading || isFetchingLocation) {
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

        if (uiState.error != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = uiState.error!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        val hasData = imageUri != null || uiState.result != null

        OutlinedButton(
            onClick = {
                viewModel.clearState()
            },
            modifier = Modifier.align(Alignment.CenterHorizontally),
            enabled = hasData,
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = if (hasData) Color.Black else MaterialTheme.colorScheme.outline,
                containerColor = if (hasData) Color(0xFFE34F4F) else Color.Transparent,
                disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
            ),
            border = if (hasData) {
                BorderStroke(1.dp, Color(0xFFE34F4F))
            } else {
                BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f))
            }
        ) {
            Icon(

                Icons.Default.Delete,
                contentDescription = null,
                tint = if (hasData) Color.White else MaterialTheme.colorScheme.outline
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "Limpiar",
                color = if (hasData) Color.White else MaterialTheme.colorScheme.outline
            )
        }
    }
}
