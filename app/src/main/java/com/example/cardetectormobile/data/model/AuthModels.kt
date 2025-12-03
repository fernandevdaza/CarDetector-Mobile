package com.example.cardetectormobile.data.model

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("access_token") val token: String,
    @SerializedName("token_type") val tokenType: String,
    val role: String
)