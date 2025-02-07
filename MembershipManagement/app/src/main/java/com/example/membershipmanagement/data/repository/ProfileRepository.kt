package com.example.membershipmanagement.data.repository

import androidx.core.app.NotificationCompat.MessagingStyle.Message
import com.example.membershipmanagement.data.remote.ApiService
import com.example.membershipmanagement.utils.UserPreferences


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

}