package com.example.cardetectormobile.ui.screens

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.cardetectormobile.data.local.entity.DetectionHistoryEntity
import com.example.cardetectormobile.ui.viewmodel.HistoryViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel,
) {
    val uiState by viewModel.uiState.collectAsState()

    if (uiState.isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else if (uiState.detections.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Aún no tienes detecciones guardadas.", color = MaterialTheme.colorScheme.onSurface)
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(uiState.detections, key = { it.id }) { detection ->
                DetectionHistoryItem(
                    detection = detection,
                    onDelete = { viewModel.deleteDetection(detection.id) }
                )
            }
        }
    }
}

@Composable
private fun DetectionHistoryItem(
    detection: DetectionHistoryEntity,
    onDelete: () -> Unit
) {
    val formatter = rememberDateFormatter()

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .background(color = MaterialTheme.colorScheme.surface)
        ) {

            if (detection.imageUri != null) {
                AsyncImage(
                    model = Uri.parse(detection.imageUri),
                    contentDescription = "Foto del vehículo",
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No\nimg", style = MaterialTheme.typography.labelSmall)
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically)
            ) {
                Text(
                    text = "${detection.brand} ${detection.modelName}",
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = detection.year?.toString() ?: "Año desconocido",
                    style = MaterialTheme.typography.bodyMedium
                )

                val coords = if (detection.lat != null && detection.lon != null) {
                    "Lat: ${"%.5f".format(detection.lat)}, Lon: ${"%.5f".format(detection.lon)}"
                } else {
                    "Ubicación no disponible"
                }
                Text(
                    text = coords,
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = formatter.format(Date(detection.createdAt)),
                    style = MaterialTheme.typography.labelSmall
                )
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier.align(Alignment.Top)
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Eliminar")
            }
        }
    }
}

@Composable
private fun rememberDateFormatter(): SimpleDateFormat {
    // súper simple, en un caso real puedes ajustarlo a locale / formato bonito
    return SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
}
