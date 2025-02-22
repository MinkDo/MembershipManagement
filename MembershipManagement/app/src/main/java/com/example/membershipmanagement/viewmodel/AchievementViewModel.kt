package com.example.membershipmanagement.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.membershipmanagement.data.repository.Achievement
import com.example.membershipmanagement.data.repository.AchievementRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class AchievementUiState(
    val achievements: List<Achievement> = emptyList(),
    val searchQuery: String = "",
    val selectedUserId: String? = null,
    val selectedEventId: Int? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String = ""
)

class AchievementViewModel(private val achievementRepository: AchievementRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(AchievementUiState())
    val uiState: StateFlow<AchievementUiState> get() = _uiState

    // 📌 Lấy danh sách thành tích với bộ lọc
    fun fetchFilteredAchievements(page: Int = 1, size: Int = 10) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = "")

            val result = achievementRepository.getFilteredAchievements(
                page = page,
                size = size,
                name = _uiState.value.searchQuery,
                userId = _uiState.value.selectedUserId,
                eventId = _uiState.value.selectedEventId,
                startDate = _uiState.value.startDate,
                endDate = _uiState.value.endDate
            )

            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(achievements = result.getOrDefault(emptyList()))
            } else {
                _uiState.value = _uiState.value.copy(errorMessage = result.exceptionOrNull()?.message ?: "Lỗi không xác định")
            }

            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    // 🔍 Cập nhật từ khóa tìm kiếm và lọc lại danh sách
    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        fetchFilteredAchievements()
    }

    // 🏅 Cập nhật bộ lọc User ID
    fun updateUserFilter(userId: String?) {
        _uiState.value = _uiState.value.copy(selectedUserId = userId)
        fetchFilteredAchievements()
    }

    // 🎟 Cập nhật bộ lọc Event ID
    fun updateEventFilter(eventId: Int?) {
        _uiState.value = _uiState.value.copy(selectedEventId = eventId)
        fetchFilteredAchievements()
    }

    // 📅 Cập nhật bộ lọc ngày tháng
    fun updateDateRange(startDate: String?, endDate: String?) {
        _uiState.value = _uiState.value.copy(startDate = startDate, endDate = endDate)
        fetchFilteredAchievements()
    }

    // 🗑 Xóa sự kiện (Admin)
    fun deleteAchievement(id: Int) {
        viewModelScope.launch {
            achievementRepository.deleteAchievement(id)
            fetchFilteredAchievements()
        }
    }
}
