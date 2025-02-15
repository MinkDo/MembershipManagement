package com.example.membershipmanagement.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.membershipmanagement.data.repository.Event
import com.example.membershipmanagement.data.repository.EventRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

enum class SortType { NAME, STATUS }

data class EventUiState(
    val events: List<Event> = emptyList(),
    val searchQuery: String = "",
    val selectedStatus: Int? = null,
    val sortType: SortType = SortType.NAME, // ✅ Mặc định sắp xếp theo tên
    val isLoading: Boolean = false,
    val message: String = ""
)

class EventViewModel(private val eventRepository: EventRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(EventUiState())
    val uiState: StateFlow<EventUiState> get() = _uiState

    // 📌 Lấy danh sách sự kiện và sắp xếp theo tùy chọn
    fun fetchEvents(page: Int = 1, size: Int = 10) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val result = eventRepository.getFilteredEvents(
                page, size, _uiState.value.searchQuery, _uiState.value.selectedStatus
            )

            if (result.isSuccess) {
                val sortedEvents = sortEvents(result.getOrDefault(emptyList()))
                _uiState.value = _uiState.value.copy(events = sortedEvents)
            }

            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    // 🔍 Cập nhật từ khóa tìm kiếm
    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        fetchEvents()
    }

    // 🔄 Cập nhật bộ lọc trạng thái
    fun updateStatusFilter(status: Int?) {
        _uiState.value = _uiState.value.copy(selectedStatus = status)
        fetchEvents()
    }

    // 🔽 Cập nhật kiểu sắp xếp
    fun updateSortType(sortType: SortType) {
        _uiState.value = _uiState.value.copy(sortType = sortType)
        _uiState.value = _uiState.value.copy(events = sortEvents(_uiState.value.events))
    }

    // 🏷 Sắp xếp danh sách sự kiện
    private fun sortEvents(events: List<Event>): List<Event> {
        return when (_uiState.value.sortType) {
            SortType.NAME -> events.sortedBy { it.name }
            SortType.STATUS -> events.sortedBy { it.status.firstOrNull() ?: Int.MAX_VALUE }
        }
    }

    // ✅ Đăng ký tham gia sự kiện
    fun registerForEvent(eventId: Int) {
        viewModelScope.launch {
            val result = eventRepository.registerForEvent(eventId)
            _uiState.value = _uiState.value.copy(
                message = if (result.isSuccess) "Đăng ký sự kiện thành công!" else result.exceptionOrNull()?.message ?: "Lỗi khi xóa sự kiện"
            )
            Log.d("EventViewModel","Register: ${_uiState.value.message}")

        }
    }

    // ❌ Hủy đăng ký sự kiện
    fun unregisterFromEvent(eventId: Int) {
        viewModelScope.launch {
            val result = eventRepository.unregisterFromEvent(eventId)
            _uiState.value = _uiState.value.copy(
                message = if (result.isSuccess) "Hủy đăng ký sự kiện thành công!" else result.exceptionOrNull()?.message ?: "Lỗi khi xóa sự kiện"
            )
            Log.d("EventViewModel","Unregister: ${_uiState.value.message}")

        }
    }

    // 🗑 Xóa sự kiện (Admin)
    fun deleteEvent(eventId: Int) {
        viewModelScope.launch {
            val result = eventRepository.deleteEvent(eventId)
            _uiState.value = _uiState.value.copy(
                message = if (result.isSuccess) "Xóa sự kiện thành công!" else result.exceptionOrNull()?.message ?: "Lỗi khi xóa sự kiện"
            )

        }
    }
}
