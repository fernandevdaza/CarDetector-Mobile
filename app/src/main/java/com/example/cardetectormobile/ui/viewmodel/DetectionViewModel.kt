package com.example.cardetectormobile.ui.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.material.icons.Icons
import com.example.cardetectormobile.data.local.SessionManager
import com.example.cardetectormobile.data.local.entity.DetectionHistoryEntity
import com.example.cardetectormobile.data.model.DetectionResponse
import com.example.cardetectormobile.domain.repository.CarRepository
import com.example.cardetectormobile.domain.repository.HistoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream

data class DetectionUiState(
    val isLoading: Boolean = false,
    val result: DetectionResponse? = null,
    val error: String? = null,
    val metadataMissing: Boolean = false,
    val imageUri: String? = null
)

class DetectionViewModel(
    private val repository: CarRepository,
    private val sessionManager: SessionManager,
    private val historyRepository: HistoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetectionUiState())
    val uiState: StateFlow<DetectionUiState> = _uiState.asStateFlow()

    fun setImage(uri: Uri) {
        _uiState.value = _uiState.value.copy(imageUri = uri.toString())
    }

    fun uploadImage(uri: Uri, context: Context) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null,
                metadataMissing = false,
                imageUri = uri.toString()
            )

            try {
                val (exifLat, exifLon) = getExifLocation(context, uri)
                Log.d("DetectionVM", "EXIF lat=$exifLat lon=$exifLon")

                if (exifLat == null || exifLon == null) {
                    Log.w("DetectionVM", "Imagen sin metadata GPS; no se enviará al backend")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        result = null,
                        error = null,
                        metadataMissing = true
                    )
                    return@launch
                }

                // Check request limit
                val role = sessionManager.getRole()
                if (role != null && !role.equals("ADMIN", ignoreCase = true)) {
                    val maxRequests = sessionManager.getMaxRequests()
                    val dailyRequests = sessionManager.getDailyRequestsCount()

                    if (dailyRequests >= maxRequests) {
                         _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            result = null,
                            error = "Has alcanzado tu límite diario de $maxRequests detecciones.",
                            metadataMissing = false
                        )
                        return@launch
                    }
                }

                val file = uriToFile(context, uri)

                val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

                val latBody = exifLat.toString()
                    .toRequestBody("text/plain".toMediaTypeOrNull())
                val lonBody = exifLon.toString()
                    .toRequestBody("text/plain".toMediaTypeOrNull())

                Log.d("DetectionVM", "Voy a enviar latBody=$exifLat lonBody=$exifLon")

                val response = repository.detectVehicle(body, latBody, lonBody)

                if (response.isSuccessful && response.body() != null) {
                    val detectionResponse = response.body()!!
                    Log.d("DetectionVM", "Éxito: $detectionResponse")

                    // Guardar en historial con userId
                    saveDetectionToHistory(
                        response = detectionResponse,
                        exifLat = exifLat,
                        exifLon = exifLon,
                        imageUri = uri.toString()
                    )
                    
                    // Increment usage count
                    sessionManager.incrementDailyRequests()

                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        result = detectionResponse,
                        error = null,
                        metadataMissing = false
                    )
                } else {
                    val errorMsg =
                        "Error API: ${response.code()} - ${response.errorBody()?.string()}"
                    Log.e("DetectionVM", errorMsg)
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        result = null,
                        error = "Error: ${response.code()} - No se puede detectar",
                        metadataMissing = false
                    )
                }
            } catch (e: Exception) {
                Log.e("DetectionVM", "Excepción crítica: ${e.message}", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    result = null,
                    error = e.message ?: "Error desconocido",
                    metadataMissing = false
                )
            }
        }
    }

    private suspend fun saveDetectionToHistory(
        response: DetectionResponse,
        exifLat: Double,
        exifLon: Double,
        imageUri: String
    ) {
        val userId = sessionManager.getUserId()
            ?: run {
                Log.w("DetectionVM", "No hay userId en SessionManager; no guardo historial")
                return
            }

        val msg = response.message

        val entity = DetectionHistoryEntity(
            userId = userId,
            brand = msg.brand,
            modelName = msg.modelName,
            year = msg.year,
            lat = exifLat,
            lon = exifLon,
            imageUri = imageUri
        )

        historyRepository.insertDetection(entity)
    }

    private fun getExifLocation(context: Context, uri: Uri): Pair<Double?, Double?> {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val exif = ExifInterface(inputStream)

                Log.d(
                    "DetectionVM",
                    "EXIF_RAW_LAT=${exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE)} " +
                            "EXIF_RAW_LON=${exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE)}"
                )

                val latLong = FloatArray(2)
                val hasLatLong = exif.getLatLong(latLong)

                if (hasLatLong) {
                    val lat = latLong[0].toDouble()
                    val lon = latLong[1].toDouble()
                    Log.d("DetectionVM", "EXIF getLatLong -> lat=$lat lon=$lon")
                    lat to lon
                } else {
                    Log.d("DetectionVM", "La imagen NO tiene coordenadas GPS en EXIF")
                    null to null
                }
            } ?: (null to null)
        } catch (e: Exception) {
            Log.e("DetectionVM", "Error leyendo EXIF: ${e.message}", e)
            null to null
        }
    }

    private fun uriToFile(context: Context, uri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw IllegalStateException("No se pudo abrir InputStream para uri=$uri")

        val tempFile = File.createTempFile("upload", ".jpg", context.cacheDir)
        FileOutputStream(tempFile).use { outputStream ->
            inputStream.use { it.copyTo(outputStream) }
        }
        return tempFile
    }

    fun clearState() {
        _uiState.value = DetectionUiState()
    }

    fun clearMetadataMissingFlag() {
        _uiState.value = _uiState.value.copy(metadataMissing = false)
    }
}
