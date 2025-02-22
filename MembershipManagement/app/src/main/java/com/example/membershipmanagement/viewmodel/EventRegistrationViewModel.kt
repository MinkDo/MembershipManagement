package com.example.membershipmanagement.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.membershipmanagement.data.repository.EventRegistrationRepository
import com.example.membershipmanagement.data.repository.RegisteredUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class EventRegistrationUiState(
    val eventId: Int = -1,
    val registeredUsers: List<RegisteredUser> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String = ""
)

class EventRegistrationViewModel(private val repository: EventRegistrationRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(EventRegistrationUiState())
    val uiState: StateFlow<EventRegistrationUiState> get() = _uiState

    // 📌 Lấy danh sách người đăng ký của sự kiện
    fun fetchEventRegistrations(eventId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(eventId = eventId,isLoading = true, errorMessage = "")

            val result = repository.getEventRegistrations(eventId)
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(registeredUsers = result.getOrDefault(emptyList()))
            } else {
                _uiState.value = _uiState.value.copy(errorMessage = result.exceptionOrNull()?.message ?: "Lỗi không xác định")
            }

            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    // ❌ Xóa người dùng khỏi danh sách đăng ký sự kiện
    fun deleteUserRegistration( userId: String) {
        viewModelScope.launch {
            val result = repository.deleteUserRegistration(_uiState.value.eventId, userId)
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    registeredUsers = _uiState.value.registeredUsers.filter { it.id != userId }
                )
            } else {
                _uiState.value = _uiState.value.copy(errorMessage = result.exceptionOrNull()?.message ?: "Lỗi khi xóa")
            }
        }
    }
    fun resetMessage(){
        _uiState.value= _uiState.value.copy(errorMessage = "")
    }
}
