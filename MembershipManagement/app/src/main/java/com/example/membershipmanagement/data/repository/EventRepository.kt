package com.example.membershipmanagement.data.repository

import android.util.Log
import com.example.membershipmanagement.data.remote.ApiService
import com.example.membershipmanagement.utils.UserPreferences
import com.example.membershipmanagement.utils.extractErrorMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.HttpException


data class EventResponse(
    val statusCode: Int,
    val message: String?,
    val errors: Any?,
    val data: EventData?
)

data class EventData(
    val currentPage: Int,
    val pageSize: Int,
    val pageCount: Int,
    val totalItemCount: Int,
    val hasPrevious: Boolean,
    val hasNext: Boolean,
    val items: List<Event>
)


data class Event(
    val id: Int,
    val name: String,
    val startDate: String,  // ISO-8601 Format
    val endDate: String?,
    val location: String,
    val description: String?,
    val fee: Double?, // Có thể là null
    val maxParticipants: Int?, // Có thể là null
    val createdAt: String,
    val status: List<Int> // Trạng thái sự kiện (Có thể có nhiều trạng thái)
)


class EventRepository(private val apiService: ApiService,private val userPreferences: UserPreferences) {
    suspend fun getEvents(page: Int, size: Int): Result<List<Event>> {
        return withContext(Dispatchers.IO) {
            try {
                val token = userPreferences.getToken()
                if (token.isNullOrEmpty()) {
                    return@withContext Result.failure(Exception("Lỗi: Token không hợp lệ"))
                }

                val response = apiService.getEvents("Bearer $token", page, size)

                if (response.isSuccessful) {
                    val events = response.body()?.data?.items ?: emptyList()
                    Result.success(events)
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Lỗi API không xác định"
                    Result.failure(Exception("Lỗi API: ${response.code()} - $errorMessage"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Lỗi kết nối: ${e.message}"))
            }
        }
    }

    suspend fun getFilteredEvents(page: Int, size: Int, name: String?, status: Int?): Result<List<Event>> {
        return withContext(Dispatchers.IO) {
            try {
                val token = userPreferences.getToken()
                if (token.isNullOrEmpty()) {
                    return@withContext Result.failure(Exception("Lỗi: Token không hợp lệ"))
                }

                val response = apiService.getFilteredEvents("Bearer $token", page, size, name, status)

                if (response.isSuccessful) {
                    val events = response.body()?.data?.items ?: emptyList()
                    Result.success(events)
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Lỗi API không xác định"
                    Result.failure(Exception("Lỗi API: ${response.code()} - $errorMessage"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Lỗi kết nối: ${e.message}"))
            }
        }
    }


    suspend fun registerForEvent(eventId: Int): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val token = userPreferences.getToken() ?: return@withContext Result.failure(Exception("Bạn chưa đăng nhập"))
                val response = apiService.registerForEvent("Bearer $token", eventId)
                // ✅ Nếu API thành công (statusCode == 200)
                response.body()?.let { responseBody ->
                    if (response.isSuccessful && responseBody.statusCode == 200) {
                        Log.d("EventRepository", "đăng ký thành công")
                        return@withContext Result.success("đăng ký thành công")
                    }
                }

                // ❌ Nếu API trả về lỗi, lấy lỗi từ errorBody
                val errorMessage = try {
                    JSONObject(response.errorBody()?.string()).optString("message", "Lỗi khi đăng ký")
                } catch (e: Exception) {
                    "Lỗi khi đăng ký sự kiện"
                }

                Log.e("EventRepository", "Lỗi khi đăng ký: $errorMessage")
                Result.failure(Exception(errorMessage))

            } catch (e: Exception) {
                Log.e("EventRepository", "Lỗi kết nối: ${e.message}")
                Result.failure(Exception("Lỗi kết nối: ${e.message}"))
            }
        }
    }

    suspend fun unregisterFromEvent(eventId: Int): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val token = userPreferences.getToken() ?: return@withContext Result.failure(Exception("Bạn chưa đăng nhập"))
                val response = apiService.unregisterFromEvent("Bearer $token", eventId)

                // ✅ Nếu API thành công (statusCode == 200)
                response.body()?.let { responseBody ->
                    if (response.isSuccessful && responseBody.statusCode == 200) {
                        Log.d("EventRepository", "Hủy đăng ký thành công")
                        return@withContext Result.success("Hủy đăng ký thành công")
                    }
                }

                // ❌ Nếu API trả về lỗi, lấy lỗi từ errorBody
                val errorMessage = try {
                    JSONObject(response.errorBody()?.string()).optString("message", "Lỗi khi hủy đăng ký")
                } catch (e: Exception) {
                    "Lỗi khi hủy đăng ký sự kiện"
                }

                Log.e("EventRepository", "Lỗi khi hủy đăng ký: $errorMessage")
                Result.failure(Exception(errorMessage))

            } catch (e: Exception) {
                Log.e("EventRepository", "Lỗi kết nối: ${e.message}")
                Result.failure(Exception("Lỗi kết nối: ${e.message}"))
            }
        }
    }


    // 📌 Xóa sự kiện (chỉ dành cho Admin)
    suspend fun deleteEvent(eventId: Int): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val token = userPreferences.getToken() ?: return@withContext Result.failure(Exception("Bạn chưa đăng nhập"))
                val response = apiService.deleteEvent("Bearer $token", eventId)
                val responseBody = response.body()

                return@withContext if (responseBody?.statusCode == 200) {
                    Log.d("EventRepository", "Xóa sự kiện thành công")
                    Result.success("Sự kiện đã được xóa!")
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
}


