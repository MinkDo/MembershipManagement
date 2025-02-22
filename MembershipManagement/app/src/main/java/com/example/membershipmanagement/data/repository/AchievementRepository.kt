package com.example.membershipmanagement.data.repository

import android.util.Log
import com.example.membershipmanagement.data.remote.ApiService
import com.example.membershipmanagement.utils.UserPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

data class AchievementResponse(
    val statusCode: Int,
    val message: String?,
    val errors: Any?,
    val data: AchievementData?
)

data class AchievementData(
    val currentPage: Int,
    val pageSize: Int,
    val pageCount: Int,
    val totalItemCount: Int,
    val hasPrevious: Boolean,
    val hasNext: Boolean,
    val items: List<Achievement>
)

data class Achievement(
    val id: Int,
    val name: String,
    val dateAchieved: String,  // ISO-8601 Format
    val description: String?,
    val user: User,
    val event: Event
)

class AchievementRepository(private val apiService: ApiService, private val userPreferences: UserPreferences) {

    suspend fun getAchievements(page: Int, size: Int): Result<List<Achievement>> {
        return withContext(Dispatchers.IO) {
            try {
                val token = userPreferences.getToken()
                if (token.isNullOrEmpty()) {
                    return@withContext Result.failure(Exception("Lỗi: Token không hợp lệ"))
                }

                val response = apiService.getAchievements("Bearer $token", page, size)
                if (response.isSuccessful) {
                    val achievements = response.body()?.data?.items ?: emptyList()
                    Result.success(achievements)
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
    suspend fun deleteAchievement(id: Int): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val token = userPreferences.getToken() ?: return@withContext Result.failure(Exception("Bạn chưa đăng nhập"))
                val response = apiService.deleteAchievement("Bearer $token", id)
                val responseBody = response.body()

                return@withContext if (responseBody?.statusCode == 200) {
                    Log.d("AchievementRepository", "Xóa Achievement thành công")
                    Result.success("Achievementn đã được xóa!")
                } else {
                    Log.e("EventRepository", "Lỗi khi xóa sự kiện: ${responseBody?.message ?: "Không rõ lỗi"}")
                    Result.failure(Exception(responseBody?.message ?: "Lỗi khi xóa sự kiện"))
                }
            } catch (e: Exception) {
                Log.e("EventRepository", "Lỗi kết nối: ${e.message}")
                Result.failure(Exception("Lỗi kết nối: ${e.message}"))
            }
        }
    }
    suspend fun getFilteredAchievements(
        page: Int,
        size: Int,
        name: String?,
        userId: String?,
        eventId: Int?,
        startDate: String?,
        endDate: String?
    ): Result<List<Achievement>> {
        return withContext(Dispatchers.IO) {
            try {
                val token = userPreferences.getToken() ?: return@withContext Result.failure(Exception("Bạn chưa đăng nhập"))
                val response = apiService.getFilteredAchievements("Bearer $token", page, size, name, userId, eventId, startDate, endDate)

                if (response.isSuccessful) {
                    Result.success(response.body()?.data?.items ?: emptyList())
                } else {
                    Result.failure(Exception("Lỗi API: ${response.code()}"))
                }
            } catch (e: HttpException) {
                Result.failure(Exception("Lỗi HTTP: ${e.message}"))
            } catch (e: Exception) {
                Result.failure(Exception("Lỗi kết nối: ${e.message}"))
            }
        }
    }
}
