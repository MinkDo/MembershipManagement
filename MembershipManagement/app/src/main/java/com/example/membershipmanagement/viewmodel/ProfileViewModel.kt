package com.example.membershipmanagement.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.membershipmanagement.data.repository.ProfileRepository
import com.example.membershipmanagement.data.repository.UserData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

data class ProfileState(
    val userData: UserData? = null,
    val errorMessage: String = "",
    val isLoading: Boolean = false
)

class ProfileViewModel(private val profileRepository: ProfileRepository) : ViewModel() {

    private val _profileState = MutableStateFlow(ProfileState())
    val profileState: StateFlow<ProfileState> get() = _profileState

    private val _updateResult = MutableStateFlow<Result<String>?>(null)
    val updateResult: StateFlow<Result<String>?> get() = _updateResult

    // Getter cho UserData
    fun getUserId() = _profileState.value.userData?.id ?: "Unknown"
    fun getUserName() = _profileState.value.userData?.userName ?: "Unknown"
    fun getEmail() = _profileState.value.userData?.email ?: ""
    fun getPhoneNumber() = _profileState.value.userData?.phoneNumber ?: ""
    fun getFullName() = _profileState.value.userData?.fullName ?: "Unknown"
    fun getAvatarUrl() = _profileState.value.userData?.avatarUrl ?: ""

    fun getHighestRole(): String {
        val priorityList = listOf("Admin", "Manager", "Member")
        val userRoles = _profileState.value.userData?.roles ?: listOf("Member")
        return priorityList.firstOrNull { it in userRoles } ?: "Member"
    }

    // Getter cho UserProfile
    fun getProfileId() = _profileState.value.userData?.profile?.id ?: -1
    fun getGender() = _profileState.value.userData?.profile?.gender ?: 0
    fun getDateOfBirth() = _profileState.value.userData?.profile?.dateOfBirth ?: ""
    fun getAddress() = _profileState.value.userData?.profile?.address ?: ""
    fun getCurrentRank() = _profileState.value.userData?.profile?.currentRank ?: 0
    fun getJoinDate() = _profileState.value.userData?.profile?.joinDate ?: ""

    // Setter cho UserData
    fun setFullName(newFullName: String) {
        _profileState.value = _profileState.value.copy(
            userData = _profileState.value.userData?.copy(fullName = newFullName)
        )
    }

    fun setEmail(newEmail: String) {
        _profileState.value = _profileState.value.copy(
            userData = _profileState.value.userData?.copy(email = newEmail)
        )
    }

    fun setPhoneNumber(newPhone: String) {
        _profileState.value = _profileState.value.copy(
            userData = _profileState.value.userData?.copy(phoneNumber = newPhone)
        )
    }

    fun setAvatarUrl(newAvatarUrl: String) {
        _profileState.value = _profileState.value.copy(
            userData = _profileState.value.userData?.copy(avatarUrl = newAvatarUrl)
        )
    }

    // Setter cho UserProfile (Tránh dùng `!!` để tránh crash)
    fun setGender(newGender: Int) {
        _profileState.value = _profileState.value.copy(
            userData = _profileState.value.userData?.copy(
                profile = _profileState.value.userData?.profile?.copy(gender = newGender) ?: return
            )
        )
    }

    fun setDateOfBirth(newDateOfBirth: String) {
        _profileState.value = _profileState.value.copy(
            userData = _profileState.value.userData?.copy(
                profile = _profileState.value.userData?.profile?.copy(dateOfBirth = newDateOfBirth) ?: return
            )
        )
    }

    fun setAddress(newAddress: String) {
        _profileState.value = _profileState.value.copy(
            userData = _profileState.value.userData?.copy(
                profile = _profileState.value.userData?.profile?.copy(address = newAddress) ?: return
            )
        )
    }

    fun setCurrentRank(newRank: Int) {
        _profileState.value = _profileState.value.copy(
            userData = _profileState.value.userData?.copy(
                profile = _profileState.value.userData?.profile?.copy(currentRank = newRank) ?: return
            )
        )
    }

    fun setJoinDate(newJoinDate: String) {
        _profileState.value = _profileState.value.copy(
            userData = _profileState.value.userData?.copy(
                profile = _profileState.value.userData?.profile?.copy(joinDate = newJoinDate) ?: return
            )
        )
    }

    fun resetUpdateResult() {
        _updateResult.value = null // ✅ Đặt lại kết quả cập nhật để tránh cập nhật liên tục
    }


    // ✅ API: Lấy thông tin user từ server
    fun getProfile() {
        viewModelScope.launch {
            _profileState.value = _profileState.value.copy(isLoading = true)

            val result = profileRepository.getProfile()
            Log.d("ProfileViewModel", "Result: $result")

            if (result != null && result.statusCode == 200) {
                _profileState.value = ProfileState(userData = result.data)
            } else {
                _profileState.value = _profileState.value.copy(errorMessage = "Lỗi khi lấy dữ liệu")
            }

            _profileState.value = _profileState.value.copy(isLoading = false)
        }
    }

    // ✅ API: Lấy user theo ID từ server
    fun getUserById(id: String) {
        viewModelScope.launch {
            _profileState.value = _profileState.value.copy(isLoading = true)

            val result = profileRepository.getUserById(id)
            Log.d("ProfileViewModel", "Result By Id: $result")

            if (result != null && result.statusCode == 200) {
                _profileState.value = ProfileState(userData = result.data)
            } else {
                _profileState.value = _profileState.value.copy(errorMessage = "Không tìm thấy người dùng")
            }

            _profileState.value = _profileState.value.copy(isLoading = false)
        }
    }

    // ✅ API: Cập nhật hồ sơ lên server
    fun updateProfile(
        id: String,
        phoneNumber: String,
        fullName: String,
        avatarFile: File?,
        avatarUrl: String,
        gender: Int,
        dateOfBirth: String,
        address: String,
        currentRank: Int,
        joinDate: String
    ) {
        viewModelScope.launch {
            Log.d("ProfileViewModel","Starting Update Profile...")
            _updateResult.value = profileRepository.updateProfile(id,
                phoneNumber, fullName, avatarFile, avatarUrl,
                gender, dateOfBirth, address, currentRank, joinDate
            )
            Log.d("ProfileViewModel","Update Profile done...")
            // Cập nhật dữ liệu sau khi cập nhật thành công
            _updateResult.value?.let {
                if (it.isSuccess) {
                    getUserById(id) // ✅ Load lại dữ liệu từ server sau khi cập nhật
                }
            }
        }
    }
}
