import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.membershipmanagement.data.repository.User
import com.example.membershipmanagement.data.repository.UserRepository
import com.example.membershipmanagement.viewmodel.AchievementUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class UserUiState(
    val users: List<User> = emptyList(),
    val searchQuery: String = "",
    val selectedGender: String = "Tất cả",
    val selectedBelt: String = "Tất cả",
    val selectedStatus: String = "Tất cả",
    val errorMessage: String = "",
    val isLoading: Boolean = false
)

class UserViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(UserUiState())
    val uiState: StateFlow<UserUiState> get() = _uiState

    // ✅ Gọi API để lấy danh sách user
    fun fetchUsers(page: Int = 1, size: Int = 10) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            val result = userRepository.getUsers(page, size)
            Log.d("UserViewModel", "Result: $result")

            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(users = result.getOrDefault(emptyList()))
            } else {
                _uiState.value = _uiState.value.copy(errorMessage = result.exceptionOrNull()?.message ?: "Unknown error")
            }

            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }





    init {
        observeSearchQuery()
    }

    // ✅ Trì hoãn gọi API để tránh mất chữ khi nhập nhanh
    private fun observeSearchQuery() {
        viewModelScope.launch {
            var lastQuery = ""
            while (true) {
                delay(500) // 🔹 Chờ 500ms trước khi gọi API (Debounce)
                val currentQuery = _uiState.value.searchQuery
                if (currentQuery != lastQuery) {
                    lastQuery = currentQuery
                    filterUsers()
                }
            }
        }
    }

    // ✅ Cập nhật query tìm kiếm nhưng KHÔNG gọi API ngay
    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }
    fun updateUserRole(userId: String, role: Int, password: String) {
        viewModelScope.launch {
            val result = userRepository.updateUserRole(userId, role, password)
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(errorMessage = "Cập nhật quyền thành công!")
                fetchUsers() // Cập nhật danh sách
            } else {
                _uiState.value = _uiState.value.copy(errorMessage = result.exceptionOrNull()?.message ?: "Lỗi khi cập nhật")
            }
        }
    }

    // ✅ Gọi API với bộ lọc
    fun filterUsers(page: Int = 1, size: Int = 10) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            // 🔹 Chuyển `searchQuery` thành List<String> nếu có nội dung
            val searchFilters = if (_uiState.value.searchQuery.isNotBlank()) listOf(_uiState.value.searchQuery) else null

            val result = userRepository.filterUsers(search = searchFilters, order = null, page = page, size = size)

            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(users = result.getOrNull() ?: emptyList())
            } else {
                _uiState.value = _uiState.value.copy(errorMessage = result.exceptionOrNull()?.message ?: "Lỗi không xác định")
            }

            _uiState.value = _uiState.value.copy(isLoading = false)
        }
    }

    // ✅ Hàm xóa user
    fun deleteUser(userId: String) {
        viewModelScope.launch {
            val result = userRepository.deleteUser(userId)
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(users = _uiState.value.users.filter { it.id != userId })

            } else {
                _uiState.value = _uiState.value.copy(errorMessage = result.exceptionOrNull()?.message ?: "Lỗi không xác định")
            }

        }
    }
    fun resetMessage(){
        _uiState.value= _uiState.value.copy(errorMessage = "")
    }
}