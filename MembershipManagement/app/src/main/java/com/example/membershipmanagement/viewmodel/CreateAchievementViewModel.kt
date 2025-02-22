package com.example.membershipmanagement.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.membershipmanagement.data.repository.Achievement
import com.example.membershipmanagement.data.repository.AchievementRequest
import com.example.membershipmanagement.data.repository.CreateAchievementRepository
import com.example.membershipmanagement.data.repository.CreateAchievementRequest
import com.example.membershipmanagement.data.repository.EditEventRequest
import com.example.membershipmanagement.data.repository.Event
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class CreateAchievementUiState(
    val id: Int = 0,
    val name: String = "",
    val dateAchieved: String = "",
    val description: String = "",
    val userId: String = "",
    val eventId: String = "",
    val isLoading: Boolean = false,
    val message: String = ""
)

class CreateAchievementViewModel(private val repository: CreateAchievementRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(CreateAchievementUiState())
    val uiState: StateFlow<CreateAchievementUiState> get() = _uiState

    fun updateField(field: (CreateAchievementUiState) -> CreateAchievementUiState) {
        _uiState.value = field(_uiState.value)
    }

    fun createAchievement(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val request = CreateAchievementRequest(
                name = _uiState.value.name,
                dateAchieved = _uiState.value.dateAchieved,
                description = _uiState.value.description,
                userId = _uiState.value.userId,
                eventId = _uiState.value.eventId.toIntOrNull() ?: 0
            )

            val result = repository.createAchievement(request)

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                message = result.getOrElse { it.message ?: "Lỗi không xác định" }
            )

            if (result.isSuccess) {
                onSuccess()
            }
        }
    }
    // 📌 Lấy thông tin sự kiện theo ID
    fun getAchievementById(id: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, message = "")

            val result = repository.getAchievementById(id)

            if (result != null && result.statusCode == 200) {
                result.data?.let { achievement ->
                    setAchievementData(achievement)
                }
            } else {
                _uiState.value = _uiState.value.copy(message = "Không tìm thấy sự kiện")
            }

            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }
    // 📌 Cập nhật sự kiện
    fun updateAchievement(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val request = AchievementRequest(
                id = _uiState.value.id,
                name = _uiState.value.name,
                dateAchieved = _uiState.value.dateAchieved,
                description = _uiState.value.description,
                userId = _uiState.value.userId,
                eventId = _uiState.value.eventId.toInt()
            )

            val result = repository.updateAchievement(request)

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                message = result.getOrElse { it.message ?: "Lỗi không xác định" }
            )

            if (result.isSuccess) {
                Log.d("CreateAchievementViewModel", "Cập nhật sự kiện thành công")
                onSuccess()
            }
        }
    }
    // 📌 Set dữ liệu sự kiện vào UI State
    private fun setAchievementData(achievement: Achievement) {
        _uiState.value = _uiState.value.copy(
            id = achievement.id,
            name = achievement.name,
            dateAchieved = achievement.dateAchieved,
            userId = achievement.user.id,
            eventId = achievement.event.id.toString(),
            description = achievement.description ?: "",
        )
        Log.d("CreateAchievementViewModel","Set AchievementData ${_uiState.value}")
    }
}
