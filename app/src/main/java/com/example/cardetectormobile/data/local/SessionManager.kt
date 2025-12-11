package com.example.cardetectormobile.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    companion object {
        const val KEY_TOKEN = "jwt_token"
        const val KEY_USER_ID = "user_id"
        const val KEY_ROLE = "user_role"
    }

    fun saveToken(token: String) {
        prefs.edit { putString(KEY_TOKEN, token) }
    }

    fun saveSession(token: String, userId: String, role: String) {
        prefs.edit {
            putString(KEY_TOKEN, token)
            putString(KEY_USER_ID, userId)
            putString(KEY_ROLE, role)
        }
    }

    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)

    fun getUserId(): String? = prefs.getString(KEY_USER_ID, null)

    fun getRole(): String? = prefs.getString(KEY_ROLE, null)

    fun isAdmin(): Boolean = getRole().equals("ADMIN", ignoreCase = true)

    fun clearSession() {
        prefs.edit { clear() }
    }
}