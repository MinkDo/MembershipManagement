package com.example.membershipmanagement.membershipManagement.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.membershipmanagement.viewmodel.ChangePasswordViewModel
import com.example.membershipmanagement.viewmodel.ProfileViewModel

@Composable
fun ChangePasswordScreen(
    navController: NavController,
    changePasswordViewModel: ChangePasswordViewModel,
    profileViewModel: ProfileViewModel

) {
    val uiState by changePasswordViewModel.uiState.collectAsState()

    Scaffold(
        topBar = { ChangePasswordTopBar(navController, changePasswordViewModel, profileViewModel) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            PasswordTextField(
                label = "Mật khẩu cũ",
                password = uiState.oldPassword,
                onPasswordChange = { changePasswordViewModel.updateOldPassword(it) }
            )

            PasswordTextField(
                label = "Mật khẩu mới",
                password = uiState.newPassword,
                onPasswordChange = { changePasswordViewModel.updateNewPassword(it) }
            )

            PasswordTextField(
                label = "Xác nhận mật khẩu mới",
                password = uiState.confirmPassword,
                onPasswordChange = { changePasswordViewModel.updateConfirmPassword(it) }
            )

            if (uiState.errorMessage.isNotEmpty()) {
                Text(uiState.errorMessage, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { changePasswordViewModel.changePassword(profileViewModel.getUserId()) },
                modifier = Modifier.fillMaxWidth(),
                enabled = uiState.isButtonEnabled
            ) {
                Text("Đổi mật khẩu")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordTopBar(navController: NavController,
                         changePasswordViewModel: ChangePasswordViewModel,
                         profileViewModel: ProfileViewModel) {

    TopAppBar(
        title = { Text("Đổi mật khẩu", style = MaterialTheme.typography.titleLarge) },
        navigationIcon = {
            IconButton(onClick = {
                changePasswordViewModel.resetMessage()
                profileViewModel.resetMessage()
                navController.popBackStack() }
            ) {
                Icon(Icons.Default.ArrowBackIos, contentDescription = "Quay lại")
            }
        }
    )
}

@Composable
fun PasswordTextField(label: String, password: String, onPasswordChange: (String) -> Unit) {
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = password,
        onValueChange = onPasswordChange,
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(image, contentDescription = "Toggle password visibility")
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
}
