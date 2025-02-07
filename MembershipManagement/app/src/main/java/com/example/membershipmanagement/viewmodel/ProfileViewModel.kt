package com.example.membershipmanagement.viewmodel


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.membershipmanagement.data.repository.ProfileRepository
import com.example.membershipmanagement.data.repository.UserData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ProfileState(
    val userData: UserData? ,
    val errorMessage: String = "",
    val isLoading: Boolean = false
)

class ProfileViewModel(private val profileRepository: ProfileRepository) : ViewModel() {
    private val _profideState = MutableStateFlow(
        ProfileState(null)
    )
    val profileState: StateFlow<ProfileState> get() = _profideState
    fun getRole(): String {
        Log.d("ProfileViewModel","Role:  ${_profideState.value.userData?.roles}")
        return _profideState.value.userData?.roles?.get(0) ?: "Member"
    }
    fun getAvatarUrl(): String{
        Log.d("ProfileViewModel","Avatar: ${_profideState.value.userData?.avatarUrl}")
        return _profideState.value.userData?.avatarUrl ?: "D:\\Mobile\\MembershipManagement\\app\\src\\main\\res\\drawable\\avatar.jpg"
    }

    fun getName(): String {
        return _profideState.value.userData?.fullName ?: "Unknow"
    }

    fun getProfile() {
        viewModelScope.launch {
            _profideState.value = _profideState.value.copy(isLoading = true)

            val result = profileRepository.getProfile()
            Log.d("ProfileViewModel","Result: $result")
            if (result != null) {
                if (result.statusCode == 200) {
                    _profideState.value = _profideState.value.copy(result.data)
                } else {
                    _profideState.value = _profideState.value.copy(errorMessage = "Error")
                }
            }

            _profideState.value = _profideState.value.copy(isLoading = false)
        }
    }

}
