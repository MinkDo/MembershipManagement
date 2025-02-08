package com.example.membershipmanagement.data.repository

import android.util.Log
import com.example.membershipmanagement.data.remote.ApiService
import com.example.membershipmanagement.utils.UserPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class UserApiResponse(
    val statusCode: Int,
    val message: String?,
    val errors: Any?,
    val data: UserDataResponse
)

data class UserDataResponse(
    val currentPage: Int,
    val pageSize: Int,
    val pageCount: Int,
    val totalItemCount: Int,
    val hasPrevious: Boolean,
    val hasNext: Boolean,
    val items: List<User>
)

data class User(
    val id: String,
    val userName: String,
    val email: String,
    val phoneNumber: String?,
    val fullName: String,
    val avatarUrl: String?,
    val createdAt: String
)
class UserRepository(private val apiService: ApiService, private val userPreferences: UserPreferences) {

    suspend fun deleteUser(userId: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = userPreferences.getToken()?.let { apiService.deleteUser("Bearer "+ it,userId) }
                if (response?.isSuccessful == true) {
                    Result.success(Unit) // ✅ Xóa thành công
                } else {
                    Result.failure(Exception("Lỗi API: ${response?.code()} - ${response?.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Lỗi kết nối: ${e.message}"))
            }
        }
    }
    suspend fun getUsers(page: Int, size: Int): Result<List<User>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = userPreferences.getToken()
                    ?.let { apiService.getUsers("Bearer "+it, page, size) }
                if (response?.isSuccessful == true) {
                    response.body()?.let {
                        Result.success(it.data.items) // ✅ Lấy danh sách user từ `data.items`
                    } ?: Result.failure(Exception("Không có dữ liệu"))
                } else {
                    Result.failure(Exception("Lỗi API: ${response?.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Lỗi kết nối: ${e.message}"))
            }
        }
    }
    suspend fun filterUsers(
        search: List<String>?,
        order: List<String>?,
        page: Int,
        size: Int
    ): Result<List<User>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = userPreferences.getToken()
                    ?.let { apiService.filterUsers("Bearer "+it, search,order,page, size) }
                Log.d("UserRepository","FilterUsers Response: $response")
                if (response?.isSuccessful == true) {
                    response.body()?.let {
                        Result.success(it.data.items) // 🔹 Lấy danh sách `items`
                    } ?: Result.failure(Exception("Không có dữ liệu"))
                } else {
                    Result.failure(Exception("Lỗi API: ${response?.message()}"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Lỗi kết nối: ${e.message}"))
            }
        }
    }
}