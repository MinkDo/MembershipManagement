package com.example.membershipmanagement.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.membershipmanagement.data.repository.FinanceRepository
import com.example.membershipmanagement.data.repository.ReportData
import com.example.membershipmanagement.data.repository.ReportRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ReportUiState(
    val reportData: List<ReportData>? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String = ""
)

class ReportViewModel(private val reportRepository: ReportRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(ReportUiState())
    val uiState: StateFlow<ReportUiState> get() = _uiState

    // Gọi API lấy báo cáo theo filter ngày
    fun fetchReportData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = "")

            val result = reportRepository.getFinanceReport(_uiState.value.startDate, _uiState.value.endDate)
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(reportData = result.getOrDefault(emptyList()))
            } else {
                _uiState.value = _uiState.value.copy(errorMessage = result.exceptionOrNull()?.message ?: "Lỗi không xác định")
            }

            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    // Cập nhật bộ lọc ngày
    fun updateDateRange(start: String?, end: String?) {
        _uiState.value = _uiState.value.copy(startDate = start, endDate = end)
        fetchReportData()
    }
    fun resetMessage(){
        _uiState.value= _uiState.value.copy(errorMessage = "")
    }
}
