package com.example.cardetectormobile.ui.viewmodel

import androidx.lifecycle.ViewModel
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.cardetectormobile.data.local.SessionManager
import com.example.cardetectormobile.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    object Success : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}

class LoginViewModel(
    private val repository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading

            try {
                val response = repository.login(email, pass)

                if (response.isSuccessful && response.body() != null) {
                    val token = response.body()!!.token
                    val userId = response.body()!!.userId
                    val role = response.body()!!.role
                    Log.d("Auth", "TokenResponse = $userId")
                    sessionManager.saveSession(token, userId, role)

                    _uiState.value = LoginUiState.Success
                } else {
                    _uiState.value = LoginUiState.Error("Error: ${response.code()} - Credenciales inválidas")
                }
            } catch (e: Exception) {
                _uiState.value = LoginUiState.Error("Fallo de conexión: ${e.message}")
            }
        }
    }
}