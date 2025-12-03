package com.example.cardetectormobile.domain.repository

import com.example.cardetectormobile.data.model.LoginResponse
import com.example.cardetectormobile.data.model.RegisterRequest
import com.example.cardetectormobile.data.network.ApiService
import retrofit2.Response

class AuthRepository(private val apiService: ApiService) {

    suspend fun login(email: String, pass: String): Response<LoginResponse> {
        return apiService.loginUser(
            user = email,
            pass = pass
        )
    }

    suspend fun register(
        email: String,
        firstName: String,
        lastName: String,
        password: String,
        role: String
    ): Response<Unit>{
        return apiService.registerUser(
            RegisterRequest(
                email = email,
                first_name = firstName,
                last_name = lastName,
                password = password,
                role = role
            )
        )
    }
}