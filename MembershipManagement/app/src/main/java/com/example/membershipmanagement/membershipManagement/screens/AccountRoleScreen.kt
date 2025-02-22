package com.example.membershipmanagement.account.screens

import UserViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.membershipmanagement.viewmodel.ProfileViewModel

import kotlinx.coroutines.launch

@Composable
fun AccountRoleScreen(navController: NavController, userViewModel: UserViewModel, profileViewModel: ProfileViewModel) {
    val uiState by userViewModel.uiState.collectAsState()
    val users = uiState.users
    val coroutineScope = rememberCoroutineScope()

    var selectedUser by remember { mutableStateOf<String?>(null) }
    var selectedRole by remember { mutableStateOf<Int?>(null) }
    var password by remember { mutableStateOf("") }
    var isDialogOpen by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { AccountRoleTopBar(navController,userViewModel, profileViewModel) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // 🔍 Tìm kiếm User
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { userViewModel.updateSearchQuery(it) },
                label = { Text("Tìm kiếm người dùng") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            when {
                uiState.isLoading -> CircularProgressIndicator()
                uiState.errorMessage.isNotEmpty() -> Text("Lỗi: ${uiState.errorMessage}", color = MaterialTheme.colorScheme.error)
                users.isEmpty() -> Text("Không tìm thấy người dùng", modifier = Modifier.padding(16.dp))
                else -> {

                }

            }
            LazyColumn {
                items(users) { user ->
                    profileViewModel.getUserById(user.id)
                    UserRoleItem(
                        user = user.fullName,
                        currentRole = profileViewModel.getHighestRole(),
                        onSelect = { selectedUser = user.id; isDialogOpen = true }
                    )
                }
            }

            // 🔹 Hộp thoại xác nhận thay đổi quyền
            if (isDialogOpen) {
                RoleChangeDialog(
                    selectedUser = selectedUser,
                    selectedRole = selectedRole,
                    password = password,
                    onRoleSelected = { selectedRole = it },
                    onPasswordChange = { password = it },
                    onConfirm = {
                        coroutineScope.launch {
                            if (selectedUser != null && selectedRole != null) {
                                userViewModel.updateUserRole(selectedUser!!, selectedRole!!, password)
                            }
                            isDialogOpen = false
                        }
                    },
                    onDismiss = { isDialogOpen = false }
                )
            }
        }
    }
}

// 🔝 Thanh điều hướng
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountRoleTopBar(navController: NavController, userViewModel: UserViewModel, profileViewModel: ProfileViewModel) {

    TopAppBar(
        title = { Text("Phân quyền tài khoản") },
        navigationIcon = {
            IconButton(onClick = {
                userViewModel.resetMessage()
                profileViewModel.resetMessage()
                navController.popBackStack() }
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
            }
        }
    )
}

// 🧑‍💼 Danh sách người dùng
@Composable
fun UserRoleItem(user: String, currentRole: String, onSelect: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = user, style = MaterialTheme.typography.titleMedium)
                Text(text = "Quyền hiện tại: $currentRole", style = MaterialTheme.typography.bodySmall)
            }
            Spacer(modifier = Modifier.weight(1f))
            IconButton(onClick =  onSelect ) {
                Icon(Icons.Default.Edit, contentDescription = "Change Role")
            }
        }
    }
}

// 🔐 Hộp thoại xác nhận thay đổi quyền
@Composable
fun RoleChangeDialog(
    selectedUser: String?,
    selectedRole: Int?,
    password: String,
    onRoleSelected: (Int) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val roleOptions = listOf("Member" to 0, "Manager" to 1, "Admin" to 2)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Thay đổi quyền tài khoản") },
        text = {
            Column {
                Text("Chọn quyền mới:")
                roleOptions.forEach { (label, value) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(label)
                        RadioButton(
                            selected = selectedRole == value,
                            onClick = { onRoleSelected(value) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = onPasswordChange,
                    label = { Text("Nhập mật khẩu để xác nhận") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Xác nhận")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Hủy")
            }
        }
    )
}
