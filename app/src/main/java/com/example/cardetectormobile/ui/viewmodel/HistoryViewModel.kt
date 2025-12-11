package com.example.cardetectormobile.ui.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cardetectormobile.data.local.entity.DetectionHistoryEntity
import com.example.cardetectormobile.domain.repository.HistoryRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.example.cardetectormobile.data.local.SessionManager


data class DetectionHistoryUiState(
    val detections: List<DetectionHistoryEntity> = emptyList(),
    val isLoading: Boolean = false
)

class HistoryViewModel(
    private val repository: HistoryRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    val uiState: StateFlow<DetectionHistoryUiState> =
        makeUiStateFlow()

    private fun makeUiStateFlow(): StateFlow<DetectionHistoryUiState> {
        val isAdmin = sessionManager.isAdmin()
        val userId = sessionManager.getUserId()

        val flow = if (isAdmin) {
            // Admin ve TODO lo que hay en la DB local
            repository.getAllDetectionHistory()
        } else if (userId != null) {
            // Usuario normal ve solo sus detecciones
            repository.getDetectionHistoryForUser(userId)
        } else {
            // Caso borde: no hay userId => lista vacía
            kotlinx.coroutines.flow.flowOf(emptyList())
        }

        return flow
            .map { list ->
                DetectionHistoryUiState(
                    detections = list,
                    isLoading = false
                )
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                DetectionHistoryUiState(isLoading = true)
            )
    }

    fun deleteDetection(id: Long) {
        viewModelScope.launch {
            repository.deleteDetection(id)
        }
    }

    fun clearAll() {
        viewModelScope.launch {
            if (sessionManager.isAdmin()) {
                repository.clearAll()
            } else {
                sessionManager.getUserId()?.let {
                    repository.clearForUser(it)
                }
            }
        }
    }
}
