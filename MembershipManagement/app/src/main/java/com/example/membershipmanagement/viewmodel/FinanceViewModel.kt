package com.example.membershipmanagement.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.membershipmanagement.data.repository.Finance
import com.example.membershipmanagement.data.repository.FinanceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class FinanceUiState(
    val finances: List<Finance> = emptyList(),
    val searchQuery: String = "",
    val selectedType: Int? = null,
    val startDate: String? = null,
    val endDate: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val successMessage: String = ""
)

class FinanceViewModel(private val financeRepository: FinanceRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(FinanceUiState())
    val uiState: StateFlow<FinanceUiState> get() = _uiState

    // 📌 Lấy danh sách tài chính từ API
    fun fetchFinances(page: Int = 1, size: Int = 10) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = "", successMessage = "")

            val result = financeRepository.getFilteredFinances(
                page, size,
                _uiState.value.selectedType, _uiState.value.startDate, _uiState.value.endDate
            )

            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(finances = result.getOrDefault(emptyList()))
            } else {
                _uiState.value = _uiState.value.copy(errorMessage = result.exceptionOrNull()?.message ?: "Lỗi không xác định")
            }

            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    // 🔍 Cập nhật từ khóa tìm kiếm (Không sử dụng API, chỉ lọc nội bộ)
    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)

        if (query.isBlank()) {
            fetchFinances()  // 🔄 Nếu query trống, gọi API để lấy lại dữ liệu đầy đủ
        } else {
            val filteredFinances = _uiState.value.finances.filter { it.category.contains(query, ignoreCase = true) }
            if (filteredFinances.isEmpty()) {
                fetchFinances() // 🔄 Nếu không có kết quả, gọi lại API để đảm bảo dữ liệu không mất
            } else {
                _uiState.value = _uiState.value.copy(finances = filteredFinances)
            }
        }
    }
    // 🔄 Cập nhật bộ lọc loại giao dịch (0: Thu nhập, 1: Chi tiêu)
    fun updateTransactionType(type: Int?) {
        _uiState.value = _uiState.value.copy(selectedType = type)
        fetchFinances()
    }

    // 📅 Cập nhật bộ lọc ngày bắt đầu & ngày kết thúc
    fun updateDateRange(start: String?, end: String?) {
        _uiState.value = _uiState.value.copy(startDate = start, endDate = end)
        fetchFinances()
    }

    // ❌ Xóa giao dịch tài chính
    fun deleteFinance(id: Int) {
        viewModelScope.launch {
            val result = financeRepository.deleteFinance(id)
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(successMessage = "Giao dịch đã được xóa!")
                fetchFinances()  // Tải lại danh sách sau khi xóa
            } else {
                _uiState.value = _uiState.value.copy(errorMessage = result.exceptionOrNull()?.message ?: "Lỗi khi xóa")
            }
        }
    }
}
