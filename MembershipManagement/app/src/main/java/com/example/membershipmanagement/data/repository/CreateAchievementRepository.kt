package com.example.membershipmanagement.data.repository


import android.util.Log
import com.example.membershipmanagement.data.remote.ApiService
import com.example.membershipmanagement.utils.UserPreferences
import com.example.membershipmanagement.utils.extractErrorMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.HttpException

data class CreateAchievementRequest(
    val name: String,
    val dateAchieved: String,
    val description: String,
    val userId: String,
    val eventId: Int
)
data class EditAchievementResponse(
    val statusCode: Int,
    val message: String?,
    val errors: Any?,
    val data: Achievement?
)
data class AchievementRequest(
    val id: Int,
    val name: String,
    val dateAchieved: String,  // ISO-8601 Format
    val description: String?,
    val userId: String,
    val eventId: Int
)


class CreateAchievementRepository(private val apiService: ApiService, private val userPreferences: UserPreferences) {

    suspend fun createAchievement(request: CreateAchievementRequest): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val token = userPreferences.getToken() ?: return@withContext Result.failure(Exception("Bạn chưa đăng nhập"))
                val response = apiService.createAchievement("Bearer $token", request)

                if (response.isSuccessful) {
                    Result.success("Tạo thành tích thành công")
                } else {
                    val errorMessage = JSONObject(response.errorBody()?.string()).optString("message", "Lỗi khi tạo thành tích")
                    Result.failure(Exception(errorMessage))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Lỗi kết nối: ${e.message}"))
            }
        }
    }
    suspend fun getAchievementById(id: String): EditAchievementResponse? {
        val response= userPreferences.getToken()?.let {
            apiService.getAchievementById("Bearer "+it, id)
        }
        if (response != null) {
            return if (response.isSuccessful) response.body() else null
        }else return null
    }
    suspend fun updateAchievement(request: AchievementRequest): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val token = userPreferences.getToken()
                if (token.isNullOrEmpty()) {
                    return@withContext Result.failure(Exception("Bạn chưa đăng nhập"))
                }

                val response = apiService.updateAchievement("Bearer $token", request.id, request)

                if (response.isSuccessful && response.body()?.statusCode == 200) {
                    Log.d("CreateAchievementRepository", "Cập nhật sự kiện thành công")
                    Result.success("Cập nhật sự kiện thành công")
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = JSONObject(errorBody ?: "{}").optString("message", "Lỗi API")
                    Log.e("ReportRepository", "Lỗi khi lấy dữ liệu: $errorMessage")
                    Result.failure(Exception(errorMessage))
                }
            } catch (e: HttpException) {
                Log.e("CreateAchievementRepository", "Lỗi HTTP: ${e.message}")
                Result.failure(Exception("Lỗi HTTP: ${e.message}"))
            } catch (e: Exception) {
                Log.e("CreateAchievementRepository", "Lỗi kết nối: ${e.message}")
                Result.failure(Exception("Lỗi kết nối: ${e.message}"))
            }
        }
    }
}