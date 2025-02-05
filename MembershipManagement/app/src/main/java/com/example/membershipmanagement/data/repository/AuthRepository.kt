package com.example.membershipmanagement.data.repository



import android.content.Context
import android.util.Log
import com.example.membershipmanagement.data.remote.ApiService
import com.example.membershipmanagement.utils.UserPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.membershipmanagement.data.repository.AuthRepository
import com.example.membershipmanagement.viewmodel.AuthViewModel


data class LoginRequest(val email: String, val password: String)

data class LoginResponse(
    val statusCode: Int,
    val message: String,
    val errors: Errors?,
    val data: LoginData?
)

data class Errors(
    val email: List<String>?,
    val password: List<String>?
)

data class LoginData(
    val token: String,
    val expiredAt: String
)

class AuthRepository(private val apiService: ApiService, private val userPreferences: UserPreferences) {

    suspend fun login(email: String, password: String): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.login(LoginRequest(email, password))
                Log.d("AuthRepository","Response: $response")
                if (response.statusCode == 200 && response.data != null) {
                    userPreferences.saveToken(response.data.token) // Lưu token
                    userPreferences.saveUserEmail(email) // Lưu email
                    Result.success(response.message)
                } else {
                    Result.failure(Exception(response.message))
                }
            } catch (e: HttpException) {
                Result.failure(Exception("Lỗi máy chủ: ${e.message}"))
            } catch (e: Exception) {
                Result.failure(Exception("Lỗi kết nối: ${e.message}"))
            }
        }
    }
}
