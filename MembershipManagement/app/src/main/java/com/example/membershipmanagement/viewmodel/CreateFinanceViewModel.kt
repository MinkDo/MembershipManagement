package com.example.membershipmanagement.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.membershipmanagement.data.repository.Achievement
import com.example.membershipmanagement.data.repository.AchievementRequest
import com.example.membershipmanagement.data.repository.CreateFinanceRepository
import com.example.membershipmanagement.data.repository.EditFinanceRequest
import com.example.membershipmanagement.data.repository.Finance
import com.example.membershipmanagement.data.repository.FinanceRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class CreateFinanceUiState(
    val id: Int = 0,
    val type: Int = 0,
    val category: String = "",
    val amount: String = "",
    val transactionDate: String = "",
    val description: String = "",
    val isLoading: Boolean = false,
    val message: String = ""
)

class CreateFinanceViewModel(private val repository: CreateFinanceRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(CreateFinanceUiState())
    val uiState: StateFlow<CreateFinanceUiState> get() = _uiState

    fun updateField(field: (CreateFinanceUiState) -> CreateFinanceUiState) {
        _uiState.value = field(_uiState.value)
    }

    fun createFinance(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val request = FinanceRequest(
                type = _uiState.value.type,
                category = _uiState.value.category,
                amount = _uiState.value.amount.toIntOrNull() ?: 0,
                transactionDate = _uiState.value.transactionDate,
                description = _uiState.value.description
            )

            val result = repository.createFinance(request)

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                message = result.getOrElse { it.message ?: "Lỗi không xác định" }
            )

            if (result.isSuccess) {
                onSuccess()
            }
        }
    }
    private fun setFinanceData(finance: Finance) {
        _uiState.value = _uiState.value.copy(
            id = finance.id,
            type = finance.type,
            category = finance.category,
            amount = finance.amount.toString(),
            transactionDate = finance.transactionDate,
            description = finance.description ?: "",
        )
        Log.d("CreateFinanceViewModel","Set FinanceData ${_uiState.value}")
    }
    fun getFinanceById(id: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, message = "")

            val result = repository.getFinanceById(id)

            if (result != null && result.statusCode == 200) {
                result.data?.let { finance ->
                    setFinanceData(finance)
                }
            } else {
                _uiState.value = _uiState.value.copy(message = "Không tìm thấy sự kiện")
            }

            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }
    // 📌 Cập nhật sự kiện
    fun updateFinance(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            // ✅ Kiểm tra amount trước khi tạo request
            val amountInt = _uiState.value.amount.toIntOrNull()
            if (amountInt == null) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    message = "Số tiền không hợp lệ. Vui lòng nhập số hợp lệ."
                )
                return@launch
            }

            val request = EditFinanceRequest(
                id = _uiState.value.id,
                type = _uiState.value.type,
                category = _uiState.value.category,
                description = _uiState.value.description,
                amount = amountInt,
                transactionDate = _uiState.value.transactionDate
            )

            Log.d("CreateFinanceViewModel", "UpdateFinance: $request")

            // ✅ Gọi API và xử lý kết quả
            val result = repository.updateFinance(request)

            _uiState.value = _uiState.value.copy(
                isLoading = false,
                message = result.getOrElse { it.message ?: "Lỗi không xác định" }
            )

            // ✅ Nếu thành công, gọi onSuccess()
            if (result.isSuccess) {
                Log.d("CreateFinanceViewModel", "Cập nhật giao dịch thành công")
                onSuccess()
            }
        }
    }
    fun resetMessage(){
        _uiState.value= _uiState.value.copy(message = "")
    }

}
