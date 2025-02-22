package com.example.membershipmanagement.events.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.membershipmanagement.data.repository.RegisteredUser
import com.example.membershipmanagement.viewmodel.EventRegistrationViewModel
import com.example.membershipmanagement.viewmodel.EventViewModel
import kotlinx.coroutines.launch

@Composable
fun EventRegistrationScreen(
    navController: NavController,
    viewModel: EventRegistrationViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val coroutineScope = rememberCoroutineScope()
    Scaffold(
        topBar = { EventRegistrationTopBar(navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            when {
                uiState.isLoading -> CircularProgressIndicator()
                uiState.errorMessage.isNotEmpty() -> Text("Lỗi: ${uiState.errorMessage}", color = MaterialTheme.colorScheme.error)
                uiState.registeredUsers.isEmpty() -> Text("Không có ai đăng ký sự kiện này", modifier = Modifier.padding(16.dp))
                else -> {
                    LazyColumn {
                        items(uiState.registeredUsers) { user ->
                            RegisteredUserItem(user)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventRegistrationTopBar(navController: NavController) {
    TopAppBar(
        title = { Text("Danh sách đăng ký", style = MaterialTheme.typography.titleLarge) },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
            }
        }
    )
}

// 🏷️ Hiển thị từng người đăng ký
@Composable
fun RegisteredUserItem(user: RegisteredUser) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = user.fullName, style = MaterialTheme.typography.titleMedium)
            Text(text = "Email: ${user.email}", style = MaterialTheme.typography.bodySmall)
            Text(text = "Đăng ký vào: ${user.registeredAt}", style = MaterialTheme.typography.bodySmall)
        }
    }
}
