package com.example.cardetectormobile.di

import android.content.Context
import com.example.cardetectormobile.data.local.DetectionDatabaseProvider
import com.example.cardetectormobile.data.local.SessionManager
import com.example.cardetectormobile.data.network.RetrofitClient
import com.example.cardetectormobile.data.repository.HistoryRepositoryImpl
import com.example.cardetectormobile.domain.repository.AuthRepository
import com.example.cardetectormobile.domain.repository.CarRepository

class AppContainer(context: Context){
    val sessionManager = SessionManager(context)
    
    val apiService: com.example.cardetectormobile.data.network.ApiService by lazy {
        val authenticator = com.example.cardetectormobile.data.network.TokenAuthenticator(sessionManager)
        val client = okhttp3.OkHttpClient.Builder()
            .authenticator(authenticator)
            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .build()

        RetrofitClient.create(client)
    }

    val authRepository = AuthRepository(apiService)

    var carRepository = CarRepository(apiService)
    val db = DetectionDatabaseProvider.getDatabase(context)

    var historyRepository = HistoryRepositoryImpl(db.detectionDao())

}