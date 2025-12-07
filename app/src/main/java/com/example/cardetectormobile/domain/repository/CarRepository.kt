package com.example.cardetectormobile.domain.repository

import com.example.cardetectormobile.data.model.DetectionResponse
import com.example.cardetectormobile.data.model.LoginResponse
import com.example.cardetectormobile.data.model.RegisterRequest
import com.example.cardetectormobile.data.network.ApiService
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

class CarRepository(private val apiService: ApiService) {

    suspend fun detectVehicle(
        image: MultipartBody.Part,
        lat: RequestBody? = null,
        lon: RequestBody? = null
    ): Response<DetectionResponse> {
        return apiService.detectVehicle(image, lat, lon)
    }
}