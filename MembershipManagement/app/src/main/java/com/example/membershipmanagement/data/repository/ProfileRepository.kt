package com.example.membershipmanagement.data.repository

import android.util.Log
import androidx.core.app.NotificationCompat.MessagingStyle.Message
import com.example.membershipmanagement.data.remote.ApiService
import com.example.membershipmanagement.utils.UserPreferences
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File


//Success: {'statusCode': 200,
//    'message': None,
//    'errors': None,
//    'data': {'roles': ['Admin'],
//            'profile': {'id': 1,
//            'gender': 0,
//            'dateOfBirth': None,
//            'address': None,
//            'currentRank': 4,
//            'joinDate': '2020-02-02'},
//    'id': 'e3410859-1949-482e-b7ac-407e9d279341',
//    'userName': 'admin@example.com',
//    'email': 'admin@example.com',
//    'phoneNumber': None,
//    'fullName': 'Admin Account',
//    'avatarUrl': 'http://hdkhanh462-001-site1.ltempurl.com/static/20250202142849_b2dc17535d7348aca9aabb379af3d8fd.jpg'}}
data class AccountResponse(
    val statusCode: Int,
    val message: String?,
    val errors: Any?,
    val data: UserData
)

data class UserData(
    val roles: List<String>,
    val profile: UserProfile,
    val id: String,
    val userName: String,
    val email: String,
    val phoneNumber: String?,
    val fullName: String,
    val avatarUrl: String
)

data class UserProfile(
    val id: Int,
    val gender: Int,
    val dateOfBirth: String?,
    val address: String?,
    val currentRank: Int,
    val joinDate: String?
)


class ProfileRepository(private val apiService: ApiService, private val userPreferences: UserPreferences) {
    suspend fun getProfile(): AccountResponse? {
        val response = userPreferences.getToken()?.let { apiService.getProfile("Bearer "+it) }

        if (response != null) {
            return if (response.isSuccessful) response.body() else null
        }else return null

    }

    suspend fun getUserById(id: String): AccountResponse? {
        val response= userPreferences.getToken()?.let {
            apiService.getUserById("Bearer "+it, id)
        }
        if (response != null) {
            return if (response.isSuccessful) response.body() else null
        }else return null
    }

    suspend fun updateProfile(
        id: String,
        phoneNumber: String,
        fullName: String,
        avatarFile: File?,  // Ảnh đại diện
        avatarUrl: String,
        gender: Int,
        dateOfBirth: String,
        address: String,
        currentRank: Int,
        joinDate: String
    ): Result<String> {
        return try {
            val token = userPreferences.getToken()
            if (token.isNullOrEmpty()) {
                return Result.failure(Exception("Token không hợp lệ"))
            }

            // Chuyển đổi dữ liệu sang RequestBody
            val phoneBody = phoneNumber.toRequestBodyPlain()
            val fullNameBody = fullName.toRequestBodyPlain()
            val avatarUrlBody = avatarUrl.toRequestBodyPlain()
            val genderBody = gender.toString().toRequestBodyPlain()
            val dobBody = dateOfBirth.toRequestBodyPlain()
            val addressBody = address.toRequestBodyPlain()
            val rankBody = currentRank.toString().toRequestBodyPlain()
            val joinDateBody = joinDate.toRequestBodyPlain()

            // Xử lý ảnh đại diện nếu có
            val avatarPart = avatarFile?.let {
                val requestFile = it.asRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("Avatar", it.name, requestFile)
            }

            val response = apiService.updateProfile(
                "Bearer $token",
                id,
                phoneBody, fullNameBody, avatarPart, avatarUrlBody,
                genderBody, dobBody, addressBody, rankBody, joinDateBody
            )

            if (response.isSuccessful) {
                Log.d("ProfileRepository", "Cập nhật thành công")
                Result.success("Cập nhật thành công")
            } else {
                // 📌 Trích xuất lỗi từ JSON response
                val errorBody = response.errorBody()?.string()
                val errorMessage = extractErrorMessage(errorBody)
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Log.e("ProfileRepository", "Lỗi kết nối: ${e.message}", e)
            Result.failure(Exception("Lỗi kết nối: ${e.message}"))
        }
    }

    // Hàm mở rộng để chuyển String thành RequestBody
    private fun String.toRequestBodyPlain(): RequestBody {
        return this.toRequestBody("text/plain".toMediaTypeOrNull())
    }
    //  Hàm trích xuất lỗi từ JSON
    private fun extractErrorMessage(errorBody: String?): String {
        return try {
            val jsonObject = JSONObject(errorBody ?: "{}")
            val errorsObject = jsonObject.optJSONObject("errors")
            val errorMessages = mutableListOf<String>()

            errorsObject?.keys()?.forEach { key ->
                errorsObject.getJSONArray(key).let { array ->
                    for (i in 0 until array.length()) {
                        errorMessages.add(array.getString(i))
                    }
                }
            }

            errorMessages.joinToString("\n") // Gộp tất cả lỗi lại thành 1 chuỗi
        } catch (e: Exception) {
            "Lỗi không xác định"
        }
    }

}