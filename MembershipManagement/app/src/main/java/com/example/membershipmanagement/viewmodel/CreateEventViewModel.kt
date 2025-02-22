package com.example.membershipmanagement.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.membershipmanagement.data.repository.EventRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class CreateEventUiState(
    val name: String = "",
    val location: String = "",
    val startDate: String = "",
    val endDate: String = "",
    val description: String = "",
    val fee: String = "",
    val maxParticipants: String = "",
    val isLoading: Boolean = false,
    val message: String = ""
)

class CreateEventViewModel(private val eventRepository: EventRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(CreateEventUiState())
    val uiState: StateFlow<CreateEventUiState> get() = _uiState

    fun updateName(value: String) { _uiState.value = _uiState.value.copy(name = value) }
    fun updateLocation(value: String) { _uiState.value = _uiState.value.copy(location = value) }
    fun updateStartDate(value: String) { _uiState.value = _uiState.value.copy(startDate = value) }
    fun updateEndDate(value: String) { _uiState.value = _uiState.value.copy(endDate = value) }
    fun updateDescription(value: String) { _uiState.value = _uiState.value.copy(description = value) }
    fun updateFee(value: String) { _uiState.value = _uiState.value.copy(fee = value) }
    fun updateMaxParticipants(value: String) { _uiState.value = _uiState.value.copy(maxParticipants = value) }

    fun reset(){
        _uiState.value.copy(    name = "",
         location = "",
         startDate = "",
         endDate = "",
         description = "",
         fee = "",
         maxParticipants = "",
         isLoading = false,
         message = "")
    }
    fun createEvent(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val result = eventRepository.createEvent(
                name = _uiState.value.name,
                location = _uiState.value.location,
                startDate = _uiState.value.startDate,
                endDate = _uiState.value.endDate,
                description = _uiState.value.description,
                fee = _uiState.value.fee.toIntOrNull() ?: 0,
                maxParticipants = _uiState.value.maxParticipants.toIntOrNull() ?: 0
            )

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                message = result.getOrNull() ?: result.exceptionOrNull()?.message ?: "Lỗi không xác định"
            )

            if (result.isSuccess)
            {

                onSuccess()
                reset()
            }
        }
    }
    fun resetMessage(){
        _uiState.value= _uiState.value.copy(message = "")
    }
}
