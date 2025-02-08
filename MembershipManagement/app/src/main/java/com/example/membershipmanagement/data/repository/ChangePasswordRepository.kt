package com.example.membershipmanagement.data.repository



import com.example.membershipmanagement.data.remote.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException

data class ChangePasswordRequest(
    val newPassword: String,
    val confirmNewPassword: String,
    val yourPassword: String
)

class ChangePasswordRepository(private val apiService: ApiService) {

    suspend fun changePassword(userId: String, currentPassword: String, newPassword: String, confirmNewPassword: String): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.changePassword(
                    userId = userId,
                    request = ChangePasswordRequest(
                        newPassword = newPassword,
                        confirmNewPassword = confirmNewPassword,
                        yourPassword = currentPassword
                    )
                )

                if (response.isSuccessful) {
                    Result.success("Mật khẩu đã được cập nhật thành công!")
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Lỗi không xác định"
                    Result.failure(Exception(errorMessage))
                }
            } catch (e: HttpException) {
                Result.failure(Exception("Lỗi HTTP: ${e.message}"))
            } catch (e: Exception) {
                Result.failure(Exception("Lỗi kết nối: ${e.message}"))
            }
        }
    }
}
