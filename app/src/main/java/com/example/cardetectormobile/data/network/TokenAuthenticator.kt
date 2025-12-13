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
        // Prevent infinite loops
        if (responseCount(response) >= 3) {
            return null
        }

        val refreshToken = sessionManager.getRefreshToken() ?: return null

        // Synchronization to avoid multiple refresh calls at once?
        // Simple synchronized block on the class or sessionManager
        synchronized(this) {
             val newToken = sessionManager.getToken()
             
             // Check if token was updated by another thread while we were waiting
             if (newToken != null && isRequestTokenOutdated(response.request, newToken)) {
                 return response.request.newBuilder()
                        .header("Authorization", "Bearer $newToken")
                        .build()
             }

            // Create a temporary API service for the refresh call to avoid cycles
            // or use simple OkHttp request
            val refreshResult = refreshAccessToken(refreshToken)

            return if (refreshResult != null) {
                sessionManager.saveToken(refreshResult.token)
                sessionManager.saveRefreshToken(refreshResult.refreshToken)

                response.request.newBuilder()
                    .header("Authorization", "Bearer ${refreshResult.token}")
                    .build()
            } else {
                // Refresh failed, maybe logout?
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
        // Creates a separate minimal Retrofit client for the refresh call
        // This avoids using the main client which might have interceptors/authenticators that trigger loops
        val api = Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8000") // TODO: Use constant
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
