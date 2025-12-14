package com.example.cardetectormobile.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cardetectormobile.data.local.SessionManager

import com.example.cardetectormobile.domain.repository.AuthRepository
import com.example.cardetectormobile.domain.repository.HistoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

data class ProfileUiState(
    val isLoading: Boolean = true,
    val userId: String? = null,
    val role: String? = null,
    val email: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val totalDetections: Int = 0,
    val lastDetectionTime: Long = 0,
    val maxRequests: Int = 10,
    val dailyRequestsCount: Int = 0,
    val errorMessage: String? = null,
    val updateSuccess: Boolean = false,
    val isDarkTheme: Boolean? = null
)

class ProfileViewModel(
    private val sessionManager: SessionManager,
    private val historyRepository: HistoryRepository,
    private val authRepository: AuthRepository
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
            val email = sessionManager.getEmail()
            val firstName = sessionManager.getFirstName()
            val lastName = sessionManager.getLastName()
            val role = sessionManager.getRole()
            val maxRequests = sessionManager.getMaxRequests()
            val isDark = sessionManager.isDarkTheme()

            if (userId == null) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "No hay sesión activa.",
                    isDarkTheme = isDark
                )
                return@launch
            }

            try {
                val total = historyRepository.getDetectionsCountForUser(userId)
                val lastDetection = historyRepository.getLastDetectionForUser(userId)
                
                val dailyRequests = sessionManager.getDailyRequestsCount() 

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    userId = userId,
                    role = role,
                    email = email,
                    firstName = firstName,
                    lastName = lastName,
                    totalDetections = total,
                    lastDetectionTime = lastDetection?.createdAt ?: 0,
                    maxRequests = maxRequests,
                    dailyRequestsCount = dailyRequests,
                    errorMessage = null,
                    isDarkTheme = isDark
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Error al cargar el perfil",
                    isDarkTheme = isDark
                )
            }
        }
    }
    
    fun toggleTheme(isSystemDark: Boolean) {
        val currentPreference = _uiState.value.isDarkTheme
        
        // If preference is null (system), the effective theme is isSystemDark.
        // So if we toggle, we want the opposite of the effective theme.
        val effectiveTheme = currentPreference ?: isSystemDark
        val newTheme = !effectiveTheme
        
        sessionManager.setDarkTheme(newTheme)
        _uiState.value = _uiState.value.copy(isDarkTheme = newTheme)
    }

    fun updateUserData(firstName: String, lastName: String, email: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val token = sessionManager.getToken()
                if (token == null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Token no encontrado"
                    )
                    return@launch
                }
                val response = authRepository.updateUserData(token, email, firstName, lastName)
                if (response.isSuccessful) {
                    // Update local session
                    sessionManager.saveSession(
                        token = sessionManager.getToken() ?: "",
                        refreshToken = sessionManager.getRefreshToken() ?: "",
                        userId = sessionManager.getUserId() ?: "",
                        role = sessionManager.getRole() ?: "",
                        firstName = firstName,
                        lastName = lastName,
                        email = email
                    )
                     _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        firstName = firstName,
                        lastName = lastName,
                        email = email,
                        updateSuccess = true
                    )
                } else {
                     _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Error al actualizar: ${response.code()}"
                    )
                }
            } catch (e: Exception) {
                 _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Error de red"
                )
            }
        }
    }

    fun deleteAccount(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val token = sessionManager.getToken()
                if (token != null) {
                    val response = authRepository.deleteAccount(token)
                    if (response.isSuccessful) {
                        sessionManager.clearSession()
                        onSuccess()
                    } else {
                         _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = "Error al borrar cuenta: ${response.code()}"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Error de red"
                )
            }
        }
    }
    
    fun updateMaxRequests(newLimit: Int) {
        sessionManager.saveMaxRequests(newLimit)
        _uiState.value = _uiState.value.copy(maxRequests = newLimit)
    }
    
    fun deleteHistory() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val userId = sessionManager.getUserId()
                if (userId != null) {
                    historyRepository.clearForUser(userId)
                    loadProfile() // Reload stats
                }
            } catch (e: Exception) {
                 _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error al borrar historial"
                )
            }
        }
    }

    fun resetUpdateSuccess() {
        _uiState.value = _uiState.value.copy(updateSuccess = false)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
    
    fun logout() {
        sessionManager.clearSession()
    }
}
