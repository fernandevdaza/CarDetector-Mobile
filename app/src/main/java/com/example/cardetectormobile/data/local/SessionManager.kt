package com.example.cardetectormobile.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    companion object {
        const val KEY_TOKEN = "jwt_token"
        const val KEY_REFRESH_TOKEN = "refresh_token"
        const val KEY_USER_ID = "user_id"
        const val KEY_ROLE = "user_role"
        const val KEY_FIRST_NAME = "first_name"
        const val KEY_LAST_NAME = "last_name"
        const val KEY_EMAIL = "email"
        const val KEY_MAX_REQUESTS = "max_requests"
        const val KEY_DAILY_COUNT = "daily_count"
        const val KEY_LAST_REQUEST_DATE = "last_request_date"
        const val KEY_IS_DARK_THEME = "is_dark_theme"
    }

    fun saveToken(token: String) {
        prefs.edit { putString(KEY_TOKEN, token) }
    }

    fun saveRefreshToken(token: String) {
        prefs.edit { putString(KEY_REFRESH_TOKEN, token) }
    }

    fun saveSession(
        token: String,
        refreshToken: String,
        userId: String,
        role: String,
        firstName: String,
        lastName: String,
        email: String
    ) {
        prefs.edit {
            putString(KEY_TOKEN, token)
            putString(KEY_REFRESH_TOKEN, refreshToken)
            putString(KEY_USER_ID, userId)
            putString(KEY_ROLE, role)
            putString(KEY_FIRST_NAME, firstName)
            putString(KEY_LAST_NAME, lastName)
            putString(KEY_EMAIL, email)
        }
    }

    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)
    fun getRefreshToken(): String? = prefs.getString(KEY_REFRESH_TOKEN, null)

    fun getUserId(): String? = prefs.getString(KEY_USER_ID, null)

    fun getRole(): String? = prefs.getString(KEY_ROLE, null)
    fun getFirstName(): String? = prefs.getString(KEY_FIRST_NAME, null)
    fun getLastName(): String? = prefs.getString(KEY_LAST_NAME, null)
    fun getEmail(): String? = prefs.getString(KEY_EMAIL, null)

    fun isAdmin(): Boolean = getRole().equals("admin", ignoreCase = true)

    fun getMaxRequests(): Int = prefs.getInt(KEY_MAX_REQUESTS, 3)

    fun saveMaxRequests(max: Int) {
        prefs.edit { putInt(KEY_MAX_REQUESTS, max) }
    }

    fun isDarkTheme(): Boolean? {
        return if (prefs.contains(KEY_IS_DARK_THEME)) {
            prefs.getBoolean(KEY_IS_DARK_THEME, false)
        } else {
            null
        }
    }

    private val _themeFlow = kotlinx.coroutines.flow.MutableStateFlow(isDarkTheme())
    val themeFlow = _themeFlow.asStateFlow()

    fun setDarkTheme(isDark: Boolean) {
        prefs.edit { putBoolean(KEY_IS_DARK_THEME, isDark) }
        _themeFlow.value = isDark
    }

    fun getDailyRequestsCount(): Int {
        val today = java.text.SimpleDateFormat("yyyyMMdd", java.util.Locale.US).format(java.util.Date())
        val lastDate = prefs.getString(KEY_LAST_REQUEST_DATE, "")
        
        return if (lastDate != today) {
             0
        } else {
             prefs.getInt(KEY_DAILY_COUNT, 0)
        }
    }

    fun incrementDailyRequests() {
        val today = java.text.SimpleDateFormat("yyyyMMdd", java.util.Locale.US).format(java.util.Date())
        val lastDate = prefs.getString(KEY_LAST_REQUEST_DATE, "")
        
        val currentCount = if (lastDate != today) {
             0
        } else {
             prefs.getInt(KEY_DAILY_COUNT, 0)
        }
        
        prefs.edit {
            putString(KEY_LAST_REQUEST_DATE, today)
            putInt(KEY_DAILY_COUNT, currentCount + 1)
        }
    }

    fun clearSession() {
        val maxRequests = getMaxRequests() // Persist max requests setting
        val dailyCount = prefs.getInt(KEY_DAILY_COUNT, 0)
        val lastDate = prefs.getString(KEY_LAST_REQUEST_DATE, "")

        prefs.edit { 
            clear() 
            putInt(KEY_MAX_REQUESTS, maxRequests)
            putInt(KEY_DAILY_COUNT, dailyCount)
            putString(KEY_LAST_REQUEST_DATE, lastDate)
        }
    }
}