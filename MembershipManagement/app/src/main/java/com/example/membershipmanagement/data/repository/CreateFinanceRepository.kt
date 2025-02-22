package com.example.membershipmanagement.data.repository

import android.util.Log
import com.example.membershipmanagement.data.remote.ApiService
import com.example.membershipmanagement.utils.UserPreferences
import com.example.membershipmanagement.utils.extractErrorMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.HttpException

data class FinanceRequest(
    val type: Int,
    val category: String,
    val amount: Int,
    val transactionDate: String,
    val description: String
)
data class EditFinanceRequest(
    val id: Int,
    val type: Int,
    val category: String,
    val amount: Int,
    val transactionDate: String,
    val description: String
)

data class EditFinanceResponse(
    val statusCode: Int,
    val message: String?,
    val errors: Any?,
    val data: Finance?
)

class CreateFinanceRepository(private val apiService: ApiService,private val userPreferences: UserPreferences) {
    suspend fun createFinance(request: FinanceRequest): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.createFinance("Bearer ${userPreferences.getToken()}",request)

                if (response.isSuccessful) {
                    Result.success("Giao dịch đã được tạo thành công!")
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = extractErrorMessage(errorBody)
                    Result.failure(Exception(errorMessage))
                }
            } catch (e: Exception) {
                Log.e("CreateFinanceRepository", "Lỗi kết nối: ${e.message}")
                Result.failure(Exception("Lỗi kết nối: ${e.message}"))
            }
        }
    }
    suspend fun getFinanceById(id: String): EditFinanceResponse? {
        val response= userPreferences.getToken()?.let {
            apiService.getFinanceById("Bearer "+it, id)
        }
        if (response != null) {
            return if (response.isSuccessful) response.body() else null
        }else return null
    }
    suspend fun updateFinance(request: EditFinanceRequest): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val token = userPreferences.getToken()
                if (token.isNullOrEmpty()) {
                    return@withContext Result.failure(Exception("Bạn chưa đăng nhập"))
                }

                val response = apiService.updateFinance("Bearer $token", request.id, request)

                if (response.isSuccessful && response.body()?.statusCode == 200) {
                    Log.d("CreateFinanceRepository", "Cập nhật finance thành công")
                    Result.success("Cập nhật finance thành công")
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = JSONObject(errorBody ?: "{}").optString("message", "Lỗi API")
                    Log.e("CreateFinanceRepository", "Lỗi khi lấy dữ liệu: $errorMessage")
                    Result.failure(Exception(errorMessage))
                }
            } catch (e: HttpException) {
                Log.e("CreateFinanceRepository", "Lỗi HTTP: ${e.message}")
                Result.failure(Exception("Lỗi HTTP: ${e.message}"))
            } catch (e: Exception) {
                Log.e("CreateFinanceRepository", "Lỗi kết nối: ${e.message}")
                Result.failure(Exception("Lỗi kết nối: ${e.message}"))
            }
        }
    }
}
