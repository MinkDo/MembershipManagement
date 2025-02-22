package com.example.membershipmanagement.data.repository

import android.util.Log
import com.example.membershipmanagement.data.remote.ApiService
import com.example.membershipmanagement.utils.UserPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

data class FinanceResponse(
    val statusCode: Int,
    val message: String?,
    val errors: Any?,
    val data: FinanceData?
)

data class FinanceData(
    val currentPage: Int,
    val pageSize: Int,
    val pageCount: Int,
    val totalItemCount: Int,
    val hasPrevious: Boolean,
    val hasNext: Boolean,
    val items: List<Finance>
)

data class Finance(
    val id: Int,
    val type: Int,  // 0: Thu, 1: Chi
    val category: String,
    val amount: Int,
    val transactionDate: String,  // ISO-8601 Format
    val description: String?,
    val createdAt: String
)

class FinanceRepository(private val apiService: ApiService, private val userPreferences: UserPreferences) {

    // 📌 Lấy danh sách giao dịch tài chính có bộ lọc
    suspend fun getFilteredFinances(page: Int, size: Int, type: Int?, start: String?, end: String?): Result<List<Finance>> {
        return withContext(Dispatchers.IO) {
            try {
                val token = userPreferences.getToken()
                if (token.isNullOrEmpty()) {
                    return@withContext Result.failure(Exception("Lỗi: Token không hợp lệ"))
                }

                val response = apiService.getFilteredFinances("Bearer $token", page, size, type, start, end)
                if (response.isSuccessful) {
                    val finances = response.body()?.data?.items ?: emptyList()
                    Result.success(finances)
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

    suspend fun deleteFinance(id: Int): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val token = userPreferences.getToken() ?: return@withContext Result.failure(Exception("Bạn chưa đăng nhập"))
                val response = apiService.deleteFinance("Bearer $token", id)
                val responseBody = response.body()

                return@withContext if (responseBody?.statusCode == 200) {
                    Log.d("FinanceRepository", "Xóa Achievement thành công")
                    Result.success("Finance đã được xóa!")
                } else {
                    Log.e("FinanceRepository", "Lỗi khi xóa sự kiện: ${responseBody?.message ?: "Không rõ lỗi"}")
                    Result.failure(Exception(responseBody?.message ?: "Lỗi khi xóa sự kiện"))
                }
            } catch (e: Exception) {
                Log.e("FinanceRepository", "Lỗi kết nối: ${e.message}")
                Result.failure(Exception("Lỗi kết nối: ${e.message}"))
            }
        }
    }

}
