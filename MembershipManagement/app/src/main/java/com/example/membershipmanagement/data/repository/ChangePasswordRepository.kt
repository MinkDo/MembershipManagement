package com.example.membershipmanagement.data.repository



import android.util.Log
import com.example.membershipmanagement.data.remote.ApiService
import com.example.membershipmanagement.utils.UserPreferences
import com.example.membershipmanagement.utils.extractErrorMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.HttpException

data class ChangePasswordRequest(
    val newPassword: String,
    val confirmNewPassword: String,
    val yourPassword: String
)

class ChangePasswordRepository(private val apiService: ApiService, private val userPreferences: UserPreferences) {

    suspend fun changePassword(userId: String, currentPassword: String, newPassword: String, confirmNewPassword: String): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val response = userPreferences.getToken()?.let {
                    apiService.changePassword(
                        token = "Bearer " +it,
                        id = userId,
                        request = ChangePasswordRequest(
                            newPassword = newPassword,
                            confirmNewPassword = confirmNewPassword,
                            yourPassword = currentPassword
                        )
                    )
                }

                if (response?.isSuccessful == true) {
                    Log.d("ProfileRepository", "Cập nhật thành công")
                    Result.success("Cập nhật thành công")
                } else {
                    // 📌 Trích xuất lỗi từ JSON response
                    val errorBody = response?.errorBody()?.string()
                    val errorMessage = extractErrorMessage(errorBody)
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
