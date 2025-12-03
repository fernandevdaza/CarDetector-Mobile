package com.example.cardetectormobile.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    companion object {
        const val KEY_TOKEN = "jwt_token"
    }

    fun saveToken(token: String) {
        prefs.edit { putString(KEY_TOKEN, token) }
    }

    fun getToken(): String? {
        return prefs.getString(KEY_TOKEN, null)
    }

    fun clearSession() {
        prefs.edit { clear() }
    }
}