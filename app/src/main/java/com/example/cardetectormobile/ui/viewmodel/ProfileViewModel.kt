package com.example.cardetectormobile.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cardetectormobile.data.local.SessionManager
import com.example.cardetectormobile.domain.repository.HistoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ProfileUiState(
    val isLoading: Boolean = true,
    val userId: String? = null,
    val role: String? = null,
    val totalDetections: Int = 0,
    val lastDetectionTime: String? = null,
    val lastLat: Double? = null,
    val lastLon: Double? = null,
    val errorMessage: String? = null
)


class ProfileViewModel(
    private val sessionManager: SessionManager,
    private val historyRepository: HistoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            val userId = sessionManager.getUserId()
            val role = sessionManager.getRole()

            if (userId == null) {
                _uiState.value = ProfileUiState(
                    isLoading = false,
                    errorMessage = "No hay sesión activa."
                )
                return@launch
            }

            try {
                val total = historyRepository.getDetectionsCountForUser(userId)
                val lastDetection = historyRepository.getLastDetectionForUser(userId)

                val lastTime = lastDetection?.createdAt?.let { formatDateTime(it) }
                val lastLat = lastDetection?.lat
                val lastLon = lastDetection?.lon

                _uiState.value = ProfileUiState(
                    isLoading = false,
                    userId = userId,
                    role = role,
                    totalDetections = total,
                    lastDetectionTime = lastTime,
                    lastLat = lastLat,
                    lastLon = lastLon,
                    errorMessage = null
                )
            } catch (e: Exception) {
                _uiState.value = ProfileUiState(
                    isLoading = false,
                    userId = userId,
                    role = role,
                    errorMessage = e.message ?: "Error al cargar el panel de usuario"
                )
            }
        }
    }

    fun logout() {
        sessionManager.clearSession()
    }

    private fun formatDateTime(epochMillis: Long): String {
        val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return formatter.format(Date(epochMillis))
    }
}
