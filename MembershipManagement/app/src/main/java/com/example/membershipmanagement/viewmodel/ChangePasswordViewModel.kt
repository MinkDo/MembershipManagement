package com.example.membershipmanagement.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import androidx.navigation.NavController

data class ChangePasswordUiState(
    val oldPassword: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val errorMessage: String = "",
    val isButtonEnabled: Boolean = false
)

class ChangePasswordViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ChangePasswordUiState())
    val uiState: StateFlow<ChangePasswordUiState> get() = _uiState

    fun updateOldPassword(password: String) {
        _uiState.value = _uiState.value.copy(oldPassword = password)
        validateForm()
    }

    fun updateNewPassword(password: String) {
        _uiState.value = _uiState.value.copy(newPassword = password)
        validateForm()
    }

    fun updateConfirmPassword(password: String) {
        _uiState.value = _uiState.value.copy(confirmPassword = password)
        validateForm()
    }

    private fun validateForm() {
        val state = _uiState.value
        val isValid = state.oldPassword.isNotBlank() &&
                state.newPassword.isNotBlank() &&
                state.confirmPassword == state.newPassword &&
                state.newPassword.length >= 6

        val errorMessage = when {
            state.newPassword.length < 6 -> "Mật khẩu mới phải có ít nhất 6 ký tự"
            state.confirmPassword != state.newPassword -> "Mật khẩu xác nhận không khớp"
            else -> ""
        }

        _uiState.value = state.copy(
            isButtonEnabled = isValid,
            errorMessage = errorMessage
        )
    }

    fun changePassword(navController: NavController) {
        viewModelScope.launch {
            // TODO: Gửi API thay đổi mật khẩu ở đây
            navController.popBackStack() // Quay lại màn trước sau khi đổi mật khẩu
        }
    }
}
