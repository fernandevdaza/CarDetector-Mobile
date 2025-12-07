package com.example.cardetectormobile.data.network
import com.example.cardetectormobile.data.model.DetectionResponse
import com.example.cardetectormobile.data.model.LoginResponse
import com.example.cardetectormobile.data.model.RegisterRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {

    @FormUrlEncoded
    @POST("auth/token")
    suspend fun loginUser(
        @Field("username") user: String,
        @Field("password") pass: String
    ): Response<LoginResponse>

    @POST("auth/")
    suspend fun registerUser(
        @Body request: RegisterRequest
    ): Response<Unit>

    @Multipart
    @POST("/inference/car-with-image")
    suspend fun detectVehicle(
        @Part file: MultipartBody.Part,
        @Part("lat") lat: RequestBody? = null,
        @Part("lon") lon: RequestBody? = null
        ): Response<DetectionResponse>

}