package com.example.cardetectormobile.di

import android.content.Context
import com.example.cardetectormobile.data.local.SessionManager
import com.example.cardetectormobile.data.network.RetrofitClient
import com.example.cardetectormobile.domain.repository.AuthRepository

class AppContainer(context: Context){
    val sessionManager = SessionManager(context)
    val apiService = RetrofitClient.instance

    val authRepository = AuthRepository(apiService)

}