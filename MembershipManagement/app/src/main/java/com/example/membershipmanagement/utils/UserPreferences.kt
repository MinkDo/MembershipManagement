package com.example.membershipmanagement.utils


import android.content.Context
import android.content.SharedPreferences
import android.util.Log

class UserPreferences(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    fun saveToken(token: String) {
        prefs.edit().putString("auth_token", token).apply()
        Log.d("AuthRepository","Save token: $token")
    }

    fun getToken(): String? {
        return prefs.getString("auth_token", null)
    }

    fun saveUserEmail(email: String) {
        prefs.edit().putString("user_email", email).apply()
        Log.d("AuthRepository","Save token: $email")
    }

    fun getUserEmail(): String? {
        return prefs.getString("user_email", null)
    }

    fun clear() {
        prefs.edit().clear().apply()
    }
}
