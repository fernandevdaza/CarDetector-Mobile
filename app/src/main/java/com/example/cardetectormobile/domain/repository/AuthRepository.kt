package com.example.cardetectormobile.domain.repository

import com.example.cardetectormobile.data.model.LoginResponse
import com.example.cardetectormobile.data.model.RegisterRequest
import com.example.cardetectormobile.data.model.UpdateUserDataRequest
import com.example.cardetectormobile.data.model.UpdateUserDataResponse
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
    ): Response<Unit> {
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
        suspend fun updateUserData(
            token: String,
            email: String,
            firstName: String,
            lastName: String
        ): Response<UpdateUserDataResponse> {
            val authHeader = "Bearer ${token.trim()}"
            android.util.Log.d("AuthRepository", "Updating user data with token: $authHeader")
            android.util.Log.d(
                "AuthRepository",
                "Request Body: email=$email, first_name=$firstName, last_name=$lastName"
            )

            return apiService.updateUserData(
                authHeader,
                UpdateUserDataRequest(
                    email = email,
                    first_name = firstName,
                    last_name = lastName
                )
            )
        }

    suspend fun deleteAccount(token: String): Response<Unit> {
        return apiService.deleteAccount("Bearer $token")
    }

    suspend fun refreshToken(refreshToken: String): Response<LoginResponse> {
        return apiService.refreshToken(refreshToken)
    }
}