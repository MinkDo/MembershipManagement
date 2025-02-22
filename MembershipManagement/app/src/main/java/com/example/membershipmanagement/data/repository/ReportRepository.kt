package com.example.membershipmanagement.data.repository

import android.util.Log
import com.example.membershipmanagement.data.remote.ApiService
import com.example.membershipmanagement.utils.UserPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

data class ReportResponse(
    val statusCode: Int,
    val message: String?,
    val errors: Any?,
    val data: List<ReportData>?
)

data class ReportData(
    val type: Int, // 0: Thu, 1: Chi
    val transactionCount: Int,
    val totalAmount: Int,
    val averageAmount: Int
)

class ReportRepository(private val apiService: ApiService, private val userPreferences: UserPreferences) {

    // 📌 Gọi API lấy báo cáo tài chính theo thời gian
    suspend fun getFinanceReport(startDate: String?, endDate: String?): Result<List<ReportData>> {
        return withContext(Dispatchers.IO) {
            try {
                val token = userPreferences.getToken()
                if (token.isNullOrEmpty()) {
                    return@withContext Result.failure(Exception("Lỗi: Token không hợp lệ"))
                }

                val response = apiService.getFinanceReport("Bearer $token", startDate, endDate)
                if (response.isSuccessful) {
                    val reportData = response.body()?.data ?: emptyList()
                    Result.success(reportData)
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
}
