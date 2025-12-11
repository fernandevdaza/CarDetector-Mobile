package com.example.cardetectormobile.data.model

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("access_token")
    val token: String,

    @SerializedName("token_type")
    val tokenType: String,

    @SerializedName("user_id")
    val userId: String,

    @SerializedName("role")
    val role: String
)

data class RegisterRequest(
    val email: String,
    val first_name: String,
    val last_name: String,
    val password: String,
    val role: String
)