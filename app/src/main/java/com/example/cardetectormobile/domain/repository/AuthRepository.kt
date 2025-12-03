package com.example.cardetectormobile.domain.repository

import com.example.cardetectormobile.data.model.LoginResponse
import com.example.cardetectormobile.data.network.ApiService
import retrofit2.Response

class AuthRepository(private val apiService: ApiService) {

    suspend fun login(email: String, pass: String): Response<LoginResponse> {
        return apiService.loginUser(
            user = email,
            pass = pass
        )
    }
}