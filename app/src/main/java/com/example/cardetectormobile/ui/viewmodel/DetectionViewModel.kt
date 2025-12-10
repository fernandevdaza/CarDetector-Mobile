package com.example.cardetectormobile.ui.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cardetectormobile.data.model.DetectionResponse
import com.example.cardetectormobile.domain.repository.CarRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    val metadataMissing: Boolean = false     // <- bandera para mostrar modal
)

class DetectionViewModel(
    private val repository: CarRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetectionUiState())
    val uiState: StateFlow<DetectionUiState> = _uiState.asStateFlow()

    fun uploadImage(uri: Uri, context: Context) {
        viewModelScope.launch {
            // limpiamos errores anteriores, empezamos loading
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null,
                metadataMissing = false
            )

            try {
                // 1) Leer lat/lon desde EXIF
                val (exifLat, exifLon) = getExifLocation(context, uri)
                Log.d("DetectionVM", "EXIF lat=$exifLat lon=$exifLon")

                // 2) Si no hay metadata => NO llamamos al backend
                if (exifLat == null || exifLon == null) {
                    Log.w("DetectionVM", "Imagen sin metadata GPS; no se enviará al backend")
                    _uiState.value = DetectionUiState(
                        isLoading = false,
                        result = null,
                        error = null,
                        metadataMissing = true
                    )
                    return@launch
                }

                // 3) Pasar Uri -> File temporal
                val file = uriToFile(context, uri)

                // 4) Construir cuerpo multipart
                val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

                val latBody = exifLat.toString()
                    .toRequestBody("text/plain".toMediaTypeOrNull())
                val lonBody = exifLon.toString()
                    .toRequestBody("text/plain".toMediaTypeOrNull())

                Log.d("DetectionVM", "Voy a enviar latBody=$exifLat lonBody=$exifLon")

                // 5) Llamar al backend
                val response = repository.detectVehicle(body, latBody, lonBody)

                if (response.isSuccessful && response.body() != null) {
                    Log.d("DetectionVM", "Éxito: ${response.body()}")
                    _uiState.value = DetectionUiState(
                        isLoading = false,
                        result = response.body(),
                        error = null,
                        metadataMissing = false
                    )
                } else {
                    val errorMsg =
                        "Error API: ${response.code()} - ${response.errorBody()?.string()}"
                    Log.e("DetectionVM", errorMsg)
                    _uiState.value = DetectionUiState(
                        isLoading = false,
                        result = null,
                        error = "Error: ${response.code()} - No se puede detectar",
                        metadataMissing = false
                    )
                }
            } catch (e: Exception) {
                Log.e("DetectionVM", "Excepción crítica: ${e.message}", e)
                _uiState.value = DetectionUiState(
                    isLoading = false,
                    result = null,
                    error = e.message ?: "Error desconocido",
                    metadataMissing = false
                )
            }
        }
    }

    /**
     * Lee lat/lon del EXIF del Uri (Storage Access Framework / cámara).
     * Si no hay coordenadas, devuelve (null, null).
     */
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

    /**
     * Copia el contenido del Uri a un File temporal en cacheDir.
     */
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
