package com.example.cardetectormobile.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cardetectormobile.data.local.SessionManager
import com.example.cardetectormobile.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class RegisterUiState {
    object Idle: RegisterUiState()
    object Loading: RegisterUiState()
    object Success: RegisterUiState()
    data class Error(val message: String) : RegisterUiState()
}

class RegisterViewModel(
    private val repository: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow<RegisterUiState>(RegisterUiState.Idle)

    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun register(
        email: String,
        firstName: String,
        lastName: String,
        password: String,
    ){
      viewModelScope.launch {
          _uiState.value = RegisterUiState.Loading

          try {
              val registerResponse = repository.register(email, firstName, lastName, password, "user")

              if (!registerResponse.isSuccessful){
                  _uiState.value = RegisterUiState.Error("Hubo un problema al realizar el registro")
                  return@launch
              }

              val loginResponse = repository.login(email, password)

              if (loginResponse.isSuccessful && loginResponse.body() != null) {
                  val token = loginResponse.body()!!.token
                  sessionManager.saveToken(token)

                  _uiState.value = RegisterUiState.Success
              } else {
                  _uiState.value = RegisterUiState.Error("Hubo un problema al iniciar sesión")
              }


          }catch (e: Exception){
              _uiState.value = RegisterUiState.Error(e.message ?: "Error desconocido")
          }
      }
    }

}