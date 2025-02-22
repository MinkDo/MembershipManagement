package com.example.membershipmanagement.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.membershipmanagement.data.repository.EditEventRepository
import com.example.membershipmanagement.data.repository.EditEventRequest
import com.example.membershipmanagement.data.repository.Event
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class EditEventUiState(
    val id: Int = 0,
    val name: String = "",
    val location: String = "",
    val startDate: String = "",
    val endDate: String? = null,
    val description: String? = "",
    val fee: String = "",
    val maxParticipants: String = "",
    val isLoading: Boolean = false,
    val message: String = ""
)

class EditEventViewModel(private val editEventRepository: EditEventRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(EditEventUiState())
    val uiState: StateFlow<EditEventUiState> get() = _uiState

    // 🏷 Cập nhật từng trường dữ liệu
    fun updateField(field: (EditEventUiState) -> EditEventUiState) {
        _uiState.value = field(_uiState.value)
    }

    // 📌 Cập nhật sự kiện
    fun updateEvent(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val request = EditEventRequest(
                id = _uiState.value.id,
                name = _uiState.value.name,
                location = _uiState.value.location,
                startDate = _uiState.value.startDate,
                endDate = _uiState.value.endDate,
                description = _uiState.value.description,
                fee = _uiState.value.fee.toIntOrNull(),
                maxParticipants = _uiState.value.maxParticipants.toIntOrNull()
            )

            val result = editEventRepository.updateEvent(request)

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                message = result.getOrElse { it.message ?: "Lỗi không xác định" }
            )

            if (result.isSuccess) {
                Log.d("EditEventViewModel", "Cập nhật sự kiện thành công")
                onSuccess()
            }
        }
    }

    // 📌 Lấy thông tin sự kiện theo ID
    fun getEventById(id: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, message = "")

            val result = editEventRepository.getEventById(id)

            if (result != null && result.statusCode == 200) {
                result.data?.let { event ->
                    setEventData(event)
                }
            } else {
                _uiState.value = _uiState.value.copy(message = "Không tìm thấy sự kiện")
            }

            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    // 📌 Set dữ liệu sự kiện vào UI State
    private fun setEventData(event: Event) {
        _uiState.value = _uiState.value.copy(
            id = event.id,
            name = event.name,
            location = event.location,
            startDate = event.startDate,
            endDate = event.endDate,
            description = event.description ?: "",
            fee = event.fee?.toString() ?: "",
            maxParticipants = event.maxParticipants?.toString() ?: ""
        )
    }
    fun resetMessage(){
        _uiState.value= _uiState.value.copy(message = "")
    }
}
