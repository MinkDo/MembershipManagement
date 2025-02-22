package com.example.membershipmanagement.data.repository

import android.util.Log
import com.example.membershipmanagement.data.remote.ApiService
import com.example.membershipmanagement.utils.UserPreferences
import com.example.membershipmanagement.utils.extractErrorMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.HttpException
import java.io.File

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
    fun logout(){
        userPreferences.clear()
    }
    suspend fun registerUser(
        roles: Int,
        avatarFile: File?,
        fullName: String,
        email: String,
        phoneNumber: String,
        password: String,
        confirmPassword: String
    ): Result<String> {
        return try {
            val rolesBody = roles.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val fullNameBody = fullName.toRequestBody("text/plain".toMediaTypeOrNull())
            val emailBody = email.toRequestBody("text/plain".toMediaTypeOrNull())
            val phoneBody = phoneNumber.toRequestBody("text/plain".toMediaTypeOrNull())
            val passwordBody = password.toRequestBody("text/plain".toMediaTypeOrNull())
            val confirmPasswordBody = confirmPassword.toRequestBody("text/plain".toMediaTypeOrNull())

            // Xử lý file ảnh (nếu có)
            val avatarPart = avatarFile?.let {
                val requestFile = it.asRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("Avatar", it.name, requestFile)
            }

            val token = userPreferences.getToken()
            if (token == null) {
                return Result.failure(Exception("Không có token xác thực"))
            }

            val response = apiService.registerUser(
                "Bearer $token",
                rolesBody, avatarPart, fullNameBody, emailBody,
                phoneBody, passwordBody, confirmPasswordBody
            )

            if (response.isSuccessful) {
                Log.d("AuthRepository", "Cập nhật thành công")
                Result.success("Cập nhật thành công")
            } else {
                // 📌 Trích xuất lỗi từ JSON response
                val errorBody = response.errorBody()?.string()
                val errorMessage = extractErrorMessage(errorBody)
                Log.d("AuthRepository","Response: $errorMessage")
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(Exception("Lỗi kết nối: ${e.message}"))
        }
    }



}
