package com.example.membershipmanagement.viewmodel



import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.membershipmanagement.data.repository.AuthRepository

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File

data class AuthUiState(
    val fullName: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val avatarUri: String? = null,
    val role: Int = 0, // 0 = Member, 1 = Manager, 2 = Admin
    val errorMessage: String = "",
    val isLoading: Boolean = false
)


class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> get() = _uiState

    fun updateFullName(name: String) {
        _uiState.value = _uiState.value.copy(fullName = name)
    }

    fun updateEmail(email: String) {
        _uiState.value = _uiState.value.copy(email = email)
    }

    fun updatePhone(phone: String) {
        _uiState.value = _uiState.value.copy(phoneNumber = phone)
    }

    fun updatePassword(password: String) {
        _uiState.value = _uiState.value.copy(password = password)
    }

    fun updateConfirmPassword(confirmPassword: String) {
        _uiState.value = _uiState.value.copy(confirmPassword = confirmPassword)
    }

    fun updateAvatarUri(uri: String) {
        _uiState.value = _uiState.value.copy(avatarUri = uri)
    }
    fun updateRole(role: Int) {
        _uiState.value = _uiState.value.copy(role = role)
    }

    fun login(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val result = authRepository.login(_uiState.value.email, _uiState.value.password)
            Log.d("AuthViewModel","Result: $result")
            if (result.isSuccess) {
                onSuccess()
            } else {
                _uiState.value = _uiState.value.copy(errorMessage = result.exceptionOrNull()?.message ?: "Unknown error")
            }

            _uiState.value = _uiState.value.copy(isLoading = false, email = "", password = "")
        }
    }

    fun registerUser(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val avatarFile = _uiState.value.avatarUri?.let { File(it) }
            val result = authRepository.registerUser(
                roles = 0, // 🔹 Default role (Member)
                avatarFile = avatarFile,
                fullName = _uiState.value.fullName,
                email = _uiState.value.email,
                phoneNumber = _uiState.value.phoneNumber,
                password = _uiState.value.password,
                confirmPassword = _uiState.value.confirmPassword
            )

            if (result.isSuccess) {
                onSuccess()
                _uiState.value = _uiState.value.copy(isLoading = false,fullName = "",
                    email = "",
                    phoneNumber = "",
                    password = "",
                    confirmPassword = "",
                    avatarUri = null,
                    role= 0, // 0 = Member, 1 = Manager, 2 = Admin
                    errorMessage = "")
            } else {
                _uiState.value = _uiState.value.copy(errorMessage = result.exceptionOrNull()?.message ?: "Lỗi không xác định")
                Log.d("AuthViewModel","Error Register Response: ${_uiState.value.errorMessage}")
            }



        }
    }

}

