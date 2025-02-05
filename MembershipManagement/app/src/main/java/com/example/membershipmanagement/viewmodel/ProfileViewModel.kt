package com.example.membershipmanagement.viewmodel


import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class UserProfile(
    val fullName: String,
    val email: String,
    val birthDate: String,
    val gender: String,
    val beltLevel: String,
    val isActive: Boolean
)

class ProfileViewModel : ViewModel() {
    private val _userProfile = MutableStateFlow(
        UserProfile(
            fullName = "Nguyễn Văn A",
            email = "nguyenvana@example.com",
            birthDate = "01/01/1990",
            gender = "Nam",
            beltLevel = "Đen",
            isActive = true
        )
    )
    val userProfile: StateFlow<UserProfile> get() = _userProfile
}
