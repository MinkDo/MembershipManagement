package com.example.membershipmanagement.membershipManagement.screens

import UserViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.membershipmanagement.membershipManagement.components.MemberItem
import com.example.membershipmanagement.navigation.Screen
import com.example.membershipmanagement.viewmodel.ProfileViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun MemberScreen(
    navController: NavController,
    userViewModel: UserViewModel,
    profileViewModel: ProfileViewModel,
) {
    val uiState by userViewModel.uiState.collectAsState()
    val members = uiState.users
    val coroutineScope = rememberCoroutineScope()
    // ✅ Gọi API khi mở màn hình
    LaunchedEffect(Unit) {
        userViewModel.filterUsers()
    }

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
                value = uiState.searchQuery,
                onValueChange = { userViewModel.updateSearchQuery(it) },
                label = { Text("Tìm kiếm theo tên") },
                modifier = Modifier.fillMaxWidth()
            )


            Spacer(modifier = Modifier.height(8.dp))

            // Hiển thị trạng thái tải dữ liệu hoặc lỗi
            when {
                uiState.isLoading -> CircularProgressIndicator()
                uiState.errorMessage.isNotEmpty() -> Text("Lỗi: ${uiState.errorMessage}", color = MaterialTheme.colorScheme.error)
                members.isEmpty() -> Text("Không có hội viên nào", modifier = Modifier.padding(16.dp))
                else -> {
                    LazyColumn {
                        items(members) { member ->
                            MemberItem(
                                member = member,
                                onEdit = {
                                    coroutineScope.launch {
                                        profileViewModel.getUserById(member.id) // 🌟 Chờ API hoàn tất
                                        delay(1000)
                                        navController.navigate(Screen.EditProfile.route) // ✅ Chỉ điều hướng sau khi API hoàn tất
                                    } },
                                onDelete = { userViewModel.deleteUser(member.id) },
                                onclick = { coroutineScope.launch {
                                    profileViewModel.getUserById(member.id) // 🌟 Chờ API hoàn tất
                                    delay(1000)
                                    navController.navigate(Screen.Profile.route) // ✅ Chỉ điều hướng sau khi API hoàn tất
                                } }
                            )
                        }
                    }
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
                        onOptionSelected(option) // ✅ Cập nhật giá trị
                        expanded = false // ✅ Đóng dropdown
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
            IconButton(onClick = { navController.popBackStack() }) { // ✅ Fix lỗi không thể quay lại
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
            }
        }
    )
}
