package com.example.membershipmanagement.data.repository

import android.util.Log
import com.example.membershipmanagement.data.remote.ApiService
import com.example.membershipmanagement.utils.UserPreferences
import com.example.membershipmanagement.utils.extractErrorMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.HttpException


data class EditEventResponse(
    val statusCode: Int,
    val message: String?,
    val errors: Any?,
    val data: Event?
)

data class EditEventRequest(
    val id: Int,
    val name: String,
    val location: String,
    val startDate: String,
    val endDate: String?,
    val description: String?,
    val fee: Int?,
    val maxParticipants: Int?
)

class EditEventRepository(private val apiService: ApiService, private val userPreferences: UserPreferences) {
    suspend fun getEventById(id: String): EditEventResponse? {
        val response= userPreferences.getToken()?.let {
            apiService.getEventById("Bearer "+it, id)
        }
        if (response != null) {
            return if (response.isSuccessful) response.body() else null
        }else return null
    }

    suspend fun updateEvent(request: EditEventRequest): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val token = userPreferences.getToken()
                if (token.isNullOrEmpty()) {
                    return@withContext Result.failure(Exception("Bạn chưa đăng nhập"))
                }

                val response = apiService.updateEvent("Bearer $token", request.id, request)

                if (response.isSuccessful && response.body()?.statusCode == 200) {
                    Log.d("EditEventRepository", "Cập nhật sự kiện thành công")
                    Result.success("Cập nhật sự kiện thành công")
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = extractErrorMessage(errorBody)
                    Result.failure(Exception(errorMessage))
                }
            } catch (e: HttpException) {
                Log.e("EditEventRepository", "Lỗi HTTP: ${e.message}")
                Result.failure(Exception("Lỗi HTTP: ${e.message}"))
            } catch (e: Exception) {
                Log.e("EditEventRepository", "Lỗi kết nối: ${e.message}")
                Result.failure(Exception("Lỗi kết nối: ${e.message}"))
            }
        }
    }

}