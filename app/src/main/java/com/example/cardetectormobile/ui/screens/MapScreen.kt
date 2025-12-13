package com.example.cardetectormobile.ui.screens

import android.content.Context
import android.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.cardetectormobile.data.local.entity.DetectionHistoryEntity
import com.example.cardetectormobile.ui.viewmodel.HistoryViewModel
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import kotlin.math.abs
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@Composable
fun MapScreen(
    viewModel: HistoryViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Solo detecciones con coordenadas válidas
    val detectionsWithLocation = remember(uiState.detections) {
        uiState.detections.filter { it.lat != null && it.lon != null }
    }

    // Ubicación seleccionada al tocar un marcador
    var selectedLocation by remember {
        mutableStateOf<Pair<Double, Double>?>(null)
    }

    // Detecciones asociadas a la ubicación seleccionada (misma zona)
    val detectionsAtSelectedLocation: List<DetectionHistoryEntity> =
        remember(selectedLocation, uiState.detections) {
            if (selectedLocation == null) emptyList()
            else {
                val (selLat, selLon) = selectedLocation!!
                val threshold = 0.0007  // un poco más “tolerante” (~70m aprox.)

                uiState.detections.filter { det ->
                    val lat = det.lat
                    val lon = det.lon
                    lat != null && lon != null &&
                            abs(lat - selLat) < threshold &&
                            abs(lon - selLon) < threshold
                }
            }
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        // ================== MAPA EN CARD ==================
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.4f),
            shape = MaterialTheme.shapes.large,
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        ) {
            if (detectionsWithLocation.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay detecciones con ubicación para mostrar.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                OsmMap(
                    context = context,
                    detections = detectionsWithLocation,
                    onMarkerSelected = { lat, lon ->
                        selectedLocation = lat to lon
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // ================== PANEL INFERIOR: AUTOS EN ESTA ZONA ==================
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
                    .padding(12.dp)
            ) {

                Text(
                    text = "Autos en esta zona",
                    style = MaterialTheme.typography.titleMedium
                )

                if (selectedLocation != null) {
                    val (lat, lon) = selectedLocation!!
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Lat: %.5f, Lon: %.5f".format(lat, lon),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                } else {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Toca un marcador en el mapa para ver los autos detectados en esa zona.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                Divider()
                Spacer(modifier = Modifier.height(8.dp))

                when {
                    selectedLocation == null -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Sin zona seleccionada.",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    detectionsAtSelectedLocation.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No se encontraron detecciones en esta zona.",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(detectionsAtSelectedLocation, key = { it.id }) { det ->
                                DetectionAtLocationItem(det)
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
private fun OsmMap(
    context: Context,
    detections: List<DetectionHistoryEntity>,
    onMarkerSelected: (lat: Double, lon: Double) -> Unit
) {
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { ctx ->
            val appContext = ctx.applicationContext
            val prefs = appContext.getSharedPreferences("osmdroid_prefs", Context.MODE_PRIVATE)
            Configuration.getInstance().load(appContext, prefs)
            Configuration.getInstance().userAgentValue = appContext.packageName



            MapView(ctx).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)

                setBackgroundColor(Color.WHITE)

                if (detections.isNotEmpty()) {
                    val first = detections.first()
                    val startLat = first.lat ?: 0.0
                    val startLon = first.lon ?: 0.0
                    controller.setZoom(13.0)
                    controller.setCenter(GeoPoint(startLat, startLon))
                }
            }
        },
        update = { mapView ->
            mapView.overlays.clear()

            if (detections.isNotEmpty()) {
                detections.forEach { det ->
                    val lat = det.lat ?: return@forEach
                    val lon = det.lon ?: return@forEach

                    val marker = Marker(mapView).apply {
                        position = GeoPoint(lat, lon)
                        title = "${det.brand} ${det.modelName}"
                        subDescription = det.year?.let { "Año aprox.: $it" } ?: "Año desconocido"
                        setOnMarkerClickListener { m, _ ->
                            onMarkerSelected(lat, lon)
                            mapView.controller.animateTo(m.position)
                            true
                        }
                    }

                    mapView.overlays.add(marker)
                }
            }

            mapView.invalidate()
        }
    )
}




@Composable
private fun DetectionAtLocationItem(
    detection: DetectionHistoryEntity
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Text(
                text = "${detection.brand} ${detection.modelName}",
                style = MaterialTheme.typography.bodyLarge
            )

            detection.year?.let {
                Text(
                    text = "Año aprox.: $it",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (detection.lat != null && detection.lon != null) {
                Text(
                    text = "Lat: %.5f, Lon: %.5f".format(detection.lat, detection.lon),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}

