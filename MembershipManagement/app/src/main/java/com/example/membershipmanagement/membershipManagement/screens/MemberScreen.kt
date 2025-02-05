package com.example.membershipmanagement.membershipManagement.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.membershipmanagement.data.model.Member
import com.example.membershipmanagement.membershipManagement.components.MemberItem
import com.example.membershipmanagement.viewmodel.MemberViewModel


@Composable
fun MemberScreen(
    navController: NavController,
    memberViewModel: MemberViewModel = viewModel()
) {
    val members by memberViewModel.filteredMembers.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedGender by remember { mutableStateOf("Tất cả") }
    var selectedBelt by remember { mutableStateOf("Tất cả") }
    var selectedStatus by remember { mutableStateOf("Tất cả") }

    Scaffold(
        topBar = { MemberTopBar(navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Thanh tìm kiếm
            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    memberViewModel.filterMembers(searchQuery, selectedGender, selectedBelt, selectedStatus)
                },
                label = { Text("Tìm kiếm theo tên") },
                modifier = Modifier.fillMaxWidth()
            )

            // Bộ lọc
            FilterRow(
                selectedGender = selectedGender,
                onGenderSelected = {
                    selectedGender = it
                    memberViewModel.filterMembers(searchQuery, selectedGender, selectedBelt, selectedStatus)
                },
                selectedBelt = selectedBelt,
                onBeltSelected = {
                    selectedBelt = it
                    memberViewModel.filterMembers(searchQuery, selectedGender, selectedBelt, selectedStatus)
                },
                selectedStatus = selectedStatus,
                onStatusSelected = {
                    selectedStatus = it
                    memberViewModel.filterMembers(searchQuery, selectedGender, selectedBelt, selectedStatus)
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Danh sách hội viên
            LazyColumn {
                items(members) { member ->
                    MemberItem(
                        member = member,
                        onEdit = { navController.navigate("") },
                        onDelete = { memberViewModel.removeMember(member.id) },
                        onToggleStatus = { memberViewModel.toggleMemberStatus(member.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun FilterRow(
    selectedGender: String,
    onGenderSelected: (String) -> Unit,
    selectedBelt: String,
    onBeltSelected: (String) -> Unit,
    selectedStatus: String,
    onStatusSelected: (String) -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        DropdownFilter("Giới tính", listOf("Tất cả", "Nam", "Nữ"), selectedGender, onGenderSelected)
        DropdownFilter("Cấp đai", listOf("Tất cả", "Trắng", "Vàng", "Xanh", "Đỏ", "Đen"), selectedBelt, onBeltSelected)
        DropdownFilter("Trạng thái", listOf("Tất cả", "Đang hoạt động", "Ngừng tham gia"), selectedStatus, onStatusSelected)
    }
}

@Composable
fun DropdownFilter(title: String, options: List<String>, selectedOption: String, onOptionSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(title, style = MaterialTheme.typography.bodySmall)
        Button(onClick = { expanded = true }) {
            Text(selectedOption)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemberTopBar(navController: NavController) {
    TopAppBar(
        title = { Text("Quản lý hội viên", style = MaterialTheme.typography.titleLarge) },
        navigationIcon = {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Quay lại"
            )
        }
    )
}
