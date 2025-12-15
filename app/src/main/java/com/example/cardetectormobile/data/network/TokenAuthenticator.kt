package com.example.cardetectormobile.data.network

import com.example.cardetectormobile.data.local.SessionManager
import com.example.cardetectormobile.data.model.LoginResponse
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TokenAuthenticator(
    private val sessionManager: SessionManager
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        if (responseCount(response) >= 3) {
            return null
        }

        val refreshToken = sessionManager.getRefreshToken() ?: return null

        synchronized(this) {
             val newToken = sessionManager.getToken()
             
             if (newToken != null && isRequestTokenOutdated(response.request, newToken)) {
                 return response.request.newBuilder()
                        .header("Authorization", "Bearer $newToken")
                        .build()
             }

            val refreshResult = refreshAccessToken(refreshToken)

            return if (refreshResult != null) {
                sessionManager.saveToken(refreshResult.token)
                sessionManager.saveRefreshToken(refreshResult.refreshToken)

                response.request.newBuilder()
                    .header("Authorization", "Bearer ${refreshResult.token}")
                    .build()
            } else {
                sessionManager.clearSession()
                null
            }
        }
    }
    
    private fun isRequestTokenOutdated(request: Request, currentToken: String): Boolean {
        val authHeader = request.header("Authorization")
        return authHeader != null && authHeader != "Bearer $currentToken"
    }

    private fun refreshAccessToken(refreshToken: String): LoginResponse? {
        val api = Retrofit.Builder()
            .baseUrl("http://192.168.100.18:8000/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)

        return try {
            val response = runBlocking {
                api.refreshToken(refreshToken)
            }
            if (response.isSuccessful) {
                response.body()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun responseCount(response: Response): Int {
        var result = 1
        var prior = response.priorResponse
        while (prior != null) {
            result++
            prior = prior.priorResponse
        }
        return result
    }
}
