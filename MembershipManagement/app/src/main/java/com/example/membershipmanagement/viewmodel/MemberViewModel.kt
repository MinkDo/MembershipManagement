package com.example.membershipmanagement.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.membershipmanagement.data.model.Member
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MemberViewModel : ViewModel() {
    private val _filteredMembers = MutableStateFlow<List<Member>>(listOf(
        Member(id = 1, fullName = "Nguyễn Văn A", gender = "Nam", beltLevel = "Đỏ", isActive = true),
        Member(id = 2, fullName = "Trần Thị B", gender = "Nữ", beltLevel = "Xanh", isActive = true),
        Member(id = 3, fullName = "Lê Văn C", gender = "Nam", beltLevel = "Đen", isActive = false),
        Member(id = 4, fullName = "Phạm Thị D", gender = "Nữ", beltLevel = "Vàng", isActive = true),
        Member(id = 5, fullName = "Hoàng Văn E", gender = "Nam", beltLevel = "Trắng", isActive = false)
    ))
    val filteredMembers: StateFlow<List<Member>> get() = _filteredMembers

    fun filterMembers(name: String, gender: String, belt: String, status: String) {
        viewModelScope.launch {
            val allMembers = listOf(
                Member(id = 1, fullName = "Nguyễn Văn A", gender = "Nam", beltLevel = "Đỏ", isActive = true),
                Member(id = 2, fullName = "Trần Thị B", gender = "Nữ", beltLevel = "Xanh", isActive = true),
                Member(id = 3, fullName = "Lê Văn C", gender = "Nam", beltLevel = "Đen", isActive = false),
                Member(id = 4, fullName = "Phạm Thị D", gender = "Nữ", beltLevel = "Vàng", isActive = true),
                Member(id = 5, fullName = "Hoàng Văn E", gender = "Nam", beltLevel = "Trắng", isActive = false)
            )

            _filteredMembers.value = allMembers.filter {
                (name.isBlank() || it.fullName.contains(name, ignoreCase = true)) &&
                        (gender == "Tất cả" || it.gender == gender) &&
                        (belt == "Tất cả" || it.beltLevel == belt) &&
                        (status == "Tất cả" || (status == "Đang hoạt động" && it.isActive) || (status == "Ngừng tham gia" && !it.isActive))
            }
        }
    }

    fun toggleMemberStatus(memberId: Int) {
        viewModelScope.launch {
            _filteredMembers.value = _filteredMembers.value.map { member ->
                if (member.id == memberId) member.copy(isActive = !member.isActive) else member
            }
        }
    }

    fun removeMember(memberId: Int) {
        viewModelScope.launch {
            _filteredMembers.value = _filteredMembers.value.filter { it.id != memberId }
        }
    }
}
