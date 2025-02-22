package com.example.membershipmanagement.data.repository

import android.util.Log
import com.example.membershipmanagement.data.remote.ApiService
import com.example.membershipmanagement.utils.UserPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

data class RegistrationResponse(
    val statusCode: Int,
    val message: String?,
    val errors: Any?,
    val data: RegistrationData?
)

data class RegistrationData(
    val currentPage: Int,
    val pageSize: Int,
    val pageCount: Int,
    val totalItemCount: Int,
    val hasPrevious: Boolean,
    val hasNext: Boolean,
    val items: List<RegisteredUser>
)

data class RegisteredUser(
    val registeredAt: String,
    val id: String,
    val userName: String,
    val email: String,
    val phoneNumber: String?,
    val fullName: String,
    val avatarUrl: String?,
    val createdAt: String?
)

class EventRegistrationRepository(private val apiService: ApiService, private val userPreferences: UserPreferences) {

    // 📌 Gọi API lấy danh sách đăng ký của sự kiện
    suspend fun getEventRegistrations(eventId: Int): Result<List<RegisteredUser>> {
        return withContext(Dispatchers.IO) {
            try {
                val token = userPreferences.getToken()
                if (token.isNullOrEmpty()) {
                    return@withContext Result.failure(Exception("Lỗi: Token không hợp lệ"))
                }

                val response = apiService.getEventRegistrations("Bearer $token", eventId)
                if (response.isSuccessful) {
                    val users = response.body()?.data?.items ?: emptyList()
                    Result.success(users)
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Lỗi API không xác định"
                    Result.failure(Exception("Lỗi API: ${response.code()} - $errorMessage"))
                }
            } catch (e: HttpException) {
                Result.failure(Exception("Lỗi HTTP: ${e.message}"))
            } catch (e: Exception) {
                Result.failure(Exception("Lỗi kết nối: ${e.message}"))
            }
        }
    }

    // ❌ Xóa đăng ký của một người dùng khỏi sự kiện
    suspend fun deleteUserRegistration(eventId: Int, userId: String): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val token = userPreferences.getToken() ?: return@withContext Result.failure(Exception("Bạn chưa đăng nhập"))
                val response = apiService.deleteUserRegistration("Bearer $token", eventId, userId)

                return@withContext if (response.isSuccessful) {
                    Result.success("Xóa đăng ký thành công")
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Lỗi API không xác định"
                    Result.failure(Exception("Lỗi API: ${response.code()} - $errorMessage"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Lỗi kết nối: ${e.message}"))
            }
        }
    }
}
