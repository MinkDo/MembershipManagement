package com.example.membershipmanagement.events.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.membershipmanagement.viewmodel.EventViewModel
import com.example.membershipmanagement.events.components.EventItem
import com.example.membershipmanagement.navigation.Screen
import com.example.membershipmanagement.viewmodel.ProfileViewModel
import com.example.membershipmanagement.viewmodel.SortType

@Composable
fun EventScreen(
    navController: NavController,
    eventViewModel: EventViewModel,
    profileViewModel: ProfileViewModel
) {
    val uiState by eventViewModel.uiState.collectAsState()
    val events = uiState.events
    val userRole by profileViewModel.profileState.collectAsState()
    val isAdmin = userRole.userData?.roles?.contains("admin") == true

    Scaffold(
        topBar = { EventTopBar(navController) },
        floatingActionButton = {
            if (isAdmin) {
                FloatingActionButton(onClick = { navController.navigate(Screen.CreateEvent.route) }) {
                    Icon(Icons.Default.Add, contentDescription = "Tạo sự kiện")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // 🔍 Thanh tìm kiếm
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { eventViewModel.updateSearchQuery(it) },
                label = { Text("Tìm kiếm sự kiện") },
                modifier = Modifier.fillMaxWidth()
            )

            // 📌 Bộ lọc trạng thái
            StatusFilterDropdown(
                selectedStatus = uiState.selectedStatus,
                onStatusSelected = { eventViewModel.updateStatusFilter(it) }
            )

            // 🔽 Bộ lọc sắp xếp
            SortFilterDropdown(
                selectedSort = uiState.sortType,
                onSortSelected = { eventViewModel.updateSortType(it) }
            )

            // ✅ Hiển thị thông báo đăng ký / hủy đăng ký
            if (uiState.message.isNotEmpty()) {
                Text(uiState.message, color = MaterialTheme.colorScheme.primary)
            }

            Spacer(modifier = Modifier.height(8.dp))

            when {
                uiState.isLoading -> CircularProgressIndicator()
                events.isEmpty() -> Text("Không có sự kiện nào", modifier = Modifier.padding(16.dp))
                else -> {
                    LazyColumn {
                        items(events) { event ->
                            EventItem(
                                event = event,
                                isAdmin = isAdmin,
                                eventViewModel = eventViewModel,
                                onRegister = { eventViewModel.registerForEvent(event.id) },
                                onUnregister = { eventViewModel.unregisterFromEvent(event.id) },
                                onDelete = if (isAdmin) ({ eventViewModel.deleteEvent(event.id) }) else null
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SortFilterDropdown(selectedSort: SortType, onSortSelected: (SortType) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val sortOptions = listOf("Tên" to SortType.NAME, "Trạng thái" to SortType.STATUS)

    Column {
        Text("Sắp xếp", style = MaterialTheme.typography.bodyLarge)
        Button(onClick = { expanded = true }) {
            Text(sortOptions.find { it.second == selectedSort }?.first ?: "Tên")
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            sortOptions.forEach { (label, value) ->
                DropdownMenuItem(
                    text = { Text(label) },
                    onClick = {
                        onSortSelected(value)
                        expanded = false
                    }
                )
            }
        }
    }
}


@Composable
fun StatusFilterDropdown(selectedStatus: Int?, onStatusSelected: (Int?) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val statusOptions = listOf("Tất cả" to null, "Chưa diễn ra" to 0, "Đang diễn ra" to 1, "Đã kết thúc" to 2)

    Column {
        Text("Lọc theo trạng thái", style = MaterialTheme.typography.bodyLarge)
        Button(onClick = { expanded = true }) {
            Text(statusOptions.find { it.second == selectedStatus }?.first ?: "Tất cả")
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            statusOptions.forEach { (label, value) ->
                DropdownMenuItem(
                    text = { Text(label) },
                    onClick = {
                        onStatusSelected(value)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventTopBar(navController: NavController) {
    TopAppBar(
        title = { Text("Quản lý sự kiện", style = MaterialTheme.typography.titleLarge) },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
            }
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventTopBar(navController: NavController, isAdmin: Boolean) {
    TopAppBar(
        title = { Text("Quản lý sự kiện", style = MaterialTheme.typography.titleLarge) },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
            }
        }
    )
}

