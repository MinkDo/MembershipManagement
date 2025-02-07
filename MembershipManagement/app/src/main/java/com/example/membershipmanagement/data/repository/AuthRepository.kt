package com.example.membershipmanagement.data.repository

import android.util.Log
import com.example.membershipmanagement.data.remote.ApiService
import com.example.membershipmanagement.utils.UserPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

data class LoginRequest(val email: String?, val password: String?)

data class LoginResponse(
    val statusCode: Int,
    val message: String,
    val errors: Errors?,
    val data: LoginData?
)

data class Errors(
    val Email: List<String>?,
    val Password: List<String>?
)

data class LoginData(
    val token: String,
    val expiredAt: String
)

class AuthRepository(private val apiService: ApiService, private val userPreferences: UserPreferences) {

    suspend fun login(email: String, password: String): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d("AuthRepository","Start login...")
                val response = apiService.login(LoginRequest(email, password))
                Log.d("AuthRepository","Response: $response")
                if (response.isSuccessful && response.body() != null) {
                    val responseBody = response.body()!!
                    userPreferences.saveToken(responseBody.data?.token ?: "")
                    userPreferences.saveUserEmail(email)
                    Result.success(responseBody.message)
                } else {
                    val errorMessage = "Tài khoản mật khẩu không chính xác."
                    Log.e("AuthRepository",response.errorBody()?.string() ?: "Lỗi không xác định")
                    Result.failure(Exception(errorMessage))
                }
            } catch (e: HttpException) {
                Result.failure(Exception("Lỗi HTTP: ${e.message}"))
            } catch (e: Exception) {
                Result.failure(Exception("Lỗi kết nối: ${e.message}"))
            }
        }
    }


    private fun parseErrors(errors: Errors): String {
        val emailError = errors.Email?.joinToString(", ") ?: ""
        val passwordError = errors.Password?.joinToString(", ") ?: ""
        return listOf(emailError, passwordError).filter { it.isNotEmpty() }.joinToString("\n")
    }
}
