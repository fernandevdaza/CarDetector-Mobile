package com.example.cardetectormobile.data.network

import com.example.cardetectormobile.data.model.LoginResponse
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiService {

    @FormUrlEncoded
    @POST("auth/token")
    suspend fun loginUser(
        @Field("username") user: String,
        @Field("password") pass: String
    ): Response<LoginResponse>
}