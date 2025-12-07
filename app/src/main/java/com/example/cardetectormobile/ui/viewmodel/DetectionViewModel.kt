package com.example.cardetectormobile.ui.viewmodel

import android.content.Context
import android.media.ExifInterface
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cardetectormobile.data.model.DetectionResponse
import com.example.cardetectormobile.domain.repository.AuthRepository
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
    val error: String? = null
)

class DetectionViewModel(
    private val repository: CarRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetectionUiState())
    val uiState: StateFlow<DetectionUiState> = _uiState.asStateFlow()

    fun uploadImage(uri: Uri, context: Context, lat: Double? = null, lon: Double? = null){
        viewModelScope.launch {
            _uiState.value = DetectionUiState(isLoading = true)

            try {
                val (exifLat, exifLon) = getExifLocation(context, uri)

                val file = uriToFile(uri, context)

                val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("file", file.name, requestFile)

                val latBody = lat?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())
                val lonBody = lon?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())

                val response = repository.detectVehicle(body)

                if (response.isSuccessful && response.body() != null){
                    Log.d("DetectionVM", "Éxito: ${response.body()}")
                    _uiState.value = DetectionUiState(
                        isLoading = false,
                        result = response.body()
                    )
                } else {
                    val errorMsg = "Error API: ${response.code()} - ${response.errorBody()?.string()}"
                    Log.e("DetectionVM", errorMsg)
                    _uiState.value = DetectionUiState(
                        isLoading = false,
                        error = "Error: ${response.code()} - No se puede detectar"
                    )
                }
            } catch (e: Exception){
                Log.e("DetectionVM", "Excepción crítica: ${e.message}")
                e.printStackTrace()
                _uiState.value = DetectionUiState(isLoading = false, error = e.message)
            }

        }
    }
    private fun getExifLocation(context: Context, uri: Uri): Pair<Double?, Double?> {
        return try {
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                val exif = ExifInterface(inputStream)
                val latLong = FloatArray(2)

                // getLatLong devuelve true si encontró las coordenadas
                if (exif.getLatLong(latLong)) {
                    Pair(latLong[0].toDouble(), latLong[1].toDouble())
                } else {
                    Pair(null, null)
                }
            } ?: Pair(null, null)
        } catch (e: Exception) {
            e.printStackTrace()
            Pair(null, null)
        }
    }

    private fun uriToFile(uri: Uri, context: Context): File {
        val inputStream = context.contentResolver.openInputStream(uri)
        val tempFile = File.createTempFile("upload", ".jpg", context.cacheDir)
        val outputStream = FileOutputStream(tempFile)
        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()
        return tempFile
    }
}