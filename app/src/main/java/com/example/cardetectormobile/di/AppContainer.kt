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
    val apiService = RetrofitClient.instance

    val authRepository = AuthRepository(apiService)

    var carRepository = CarRepository(apiService)
    val db = DetectionDatabaseProvider.getDatabase(context)

    var historyRepository = HistoryRepositoryImpl(db.detectionDao())

}