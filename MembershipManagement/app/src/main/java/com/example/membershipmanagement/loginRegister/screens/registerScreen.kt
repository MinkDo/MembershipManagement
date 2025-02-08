package com.example.membershipmanagement.loginRegister.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil3.compose.rememberAsyncImagePainter
import com.example.membershipmanagement.R
import com.example.membershipmanagement.navigation.Screen
import com.example.membershipmanagement.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel()
) {
    val uiState by authViewModel.uiState.collectAsState()
    val context = LocalContext.current
    var showSuccessDialog by remember { mutableStateOf(false) }
    val isFormValid = uiState.fullName.isNotBlank() && uiState.email.isNotBlank() &&
            uiState.phoneNumber.isNotBlank() && uiState.password.isNotBlank() &&
            uiState.confirmPassword.isNotBlank() && uiState.password == uiState.confirmPassword

    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { authViewModel.updateAvatarUri(it.toString()) }
    }

    Scaffold(
        topBar = { RegisterTopBar(navController) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                // Ảnh đại diện
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .clickable { imagePicker.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (uiState.avatarUri == null) {
                        Image(
                            painter = painterResource(id = R.drawable.avatar),
                            contentDescription = "Ảnh đại diện",
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Image(
                            painter = rememberAsyncImagePainter(uiState.avatarUri),
                            contentDescription = "Ảnh đại diện",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Form nhập thông tin
                OutlinedTextField(
                    value = uiState.fullName,
                    onValueChange = { authViewModel.updateFullName(it) },
                    label = { Text("Họ và tên *") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = uiState.email,
                    onValueChange = { authViewModel.updateEmail(it) },
                    label = { Text("Email *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = uiState.phoneNumber,
                    onValueChange = { authViewModel.updatePhone(it) },
                    label = { Text("Số điện thoại *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = uiState.password,
                    onValueChange = { authViewModel.updatePassword(it) },
                    label = { Text("Mật khẩu *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = uiState.confirmPassword,
                    onValueChange = { authViewModel.updateConfirmPassword(it) },
                    label = { Text("Xác nhận mật khẩu *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )

                // Hiển thị lỗi nếu mật khẩu không khớp
                if (uiState.password.isNotEmpty() && uiState.confirmPassword.isNotEmpty() && uiState.password != uiState.confirmPassword) {
                    Text("Mật khẩu xác nhận không khớp!", color = Color.Red)
                }

                // Chọn Role
                RoleDropdown(uiState.role) { authViewModel.updateRole(it) }

                // **Hiển thị lỗi từ API**
                if (uiState.errorMessage.isNotEmpty()) {
                    Text(uiState.errorMessage, color = Color.Red, modifier = Modifier.padding(top = 8.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (isFormValid) {
                            authViewModel.registerUser(
                                onSuccess = {
                                    showSuccessDialog = true // ✅ Hiển thị hộp thoại khi thành công
                                }
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = isFormValid
                ) {
                    Text("Đăng ký")
                }

                // ✅ Hộp thoại "Đăng ký thành công"
                if (showSuccessDialog) {
                    AlertDialog(
                        onDismissRequest = { showSuccessDialog = false },
                        title = { Text("Thành công!") },
                        text = { Text("Bạn đã đăng ký thành công. Vui lòng đăng nhập.") },
                        confirmButton = {
                            Button(onClick = { showSuccessDialog = false }) {
                                Text("OK")
                            }
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterTopBar(navController: NavController) {
    TopAppBar(
        title = { Text("Đăng ký hội viên", style = MaterialTheme.typography.titleLarge) },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
            }
        }
    )
}

@Composable
fun RoleDropdown(selectedRole: Int, onRoleSelected: (Int) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val roles = listOf("Member" to 0, "Manager" to 1, "Admin" to 2)
    val roleName = roles.find { it.second == selectedRole }?.first ?: "Member"

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Chọn vai trò:")
        Button(onClick = { expanded = true }) {
            Text(roleName)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            roles.forEach { (name, value) ->
                DropdownMenuItem(
                    text = { Text(name) },
                    onClick = {
                        onRoleSelected(value)
                        expanded = false
                    }
                )
            }
        }
    }
}
