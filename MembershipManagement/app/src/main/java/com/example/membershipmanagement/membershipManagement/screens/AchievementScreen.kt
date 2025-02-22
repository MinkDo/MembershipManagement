package com.example.membershipmanagement.achievements.screens

import UserViewModel
import android.app.DatePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.membershipmanagement.viewmodel.AchievementViewModel
import com.example.membershipmanagement.achievements.components.AchievementItem
import com.example.membershipmanagement.navigation.Screen
import com.example.membershipmanagement.viewmodel.CreateAchievementViewModel
import com.example.membershipmanagement.viewmodel.EventViewModel

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AchievementScreen(
    navController: NavController,
    achievementViewModel: AchievementViewModel ,
    createAchievementViewModel: CreateAchievementViewModel,
    eventViewModel: EventViewModel,
    userViewModel: UserViewModel
) {
    val uiState by achievementViewModel.uiState.collectAsState()
    val events by eventViewModel.uiState.collectAsState()
    val users by userViewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        eventViewModel.fetchEvents()
        userViewModel.fetchUsers()
    }

    Scaffold(
        topBar = { AchievementTopBar(navController) },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate(Screen.CreateAchievement.route) }) {
                Icon(Icons.Default.Add, contentDescription = "Thêm thành tích")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // 🔍 Bộ lọc
            AchievementFilterSection(achievementViewModel, userViewModel, eventViewModel)

            Spacer(modifier = Modifier.height(8.dp))

            when {
                uiState.isLoading -> CircularProgressIndicator()
                uiState.errorMessage.isNotEmpty() -> Text("Lỗi: ${uiState.errorMessage}", color = MaterialTheme.colorScheme.error)
                uiState.achievements.isEmpty() -> Text("Không có thành tích nào", modifier = Modifier.padding(16.dp))
                else -> {
                    LazyColumn {
                        items(uiState.achievements) { achievement ->
                            AchievementItem(
                                achievement = achievement,
                                onClick = {
                                    coroutineScope.launch {
                                        createAchievementViewModel.getAchievementById(achievement.id.toString())
                                        delay(500)
                                        navController.navigate(Screen.EditAchievement.route)
                                    }
                                },
                                onDelete = {
                                    achievementViewModel.deleteAchievement(achievement.id)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

// 📌 Bộ lọc với tên User và Event
@Composable
fun AchievementFilterSection(viewModel: AchievementViewModel, userViewModel: UserViewModel, eventViewModel: EventViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val users by userViewModel.uiState.collectAsState()
    val events by eventViewModel.uiState.collectAsState()
    val context = LocalContext.current
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    var selectedUserName by remember { mutableStateOf(users.users.find { it.id == uiState.selectedUserId }?.fullName ?: "") }
    var selectedEventName by remember { mutableStateOf(events.events.find { it.id.toString() == uiState.selectedEventId?.toString() }?.name ?: "") }
    var userExpanded by remember { mutableStateOf(false) }
    var eventExpanded by remember { mutableStateOf(false) }

    Column {
        // 🔍 Tìm kiếm theo tên thành tích
        OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = { viewModel.updateSearchQuery(it) },
            label = { Text("Tìm kiếm theo tên") },
            modifier = Modifier.fillMaxWidth()
        )

        // 🏅 Chọn thành viên
        Box {
            Button(onClick = { userExpanded = true }, modifier = Modifier.fillMaxWidth()) {
                Text(if (selectedUserName.isEmpty()) "Chọn thành viên" else selectedUserName)
            }
            DropdownMenu(expanded = userExpanded, onDismissRequest = { userExpanded = false }) {
                users.users.forEach { user ->
                    DropdownMenuItem(
                        text = { Text(user.fullName) },
                        onClick = {
                            selectedUserName = user.fullName
                            viewModel.updateUserFilter(user.id)
                            userExpanded = false
                        }
                    )
                }
            }
        }

        // 🎟 Chọn sự kiện
        Box {
            Button(onClick = { eventExpanded = true }, modifier = Modifier.fillMaxWidth()) {
                Text(if (selectedEventName.isEmpty()) "Chọn sự kiện" else selectedEventName)
            }
            DropdownMenu(expanded = eventExpanded, onDismissRequest = { eventExpanded = false }) {
                events.events.forEach { event ->
                    DropdownMenuItem(
                        text = { Text(event.name) },
                        onClick = {
                            selectedEventName = event.name
                            viewModel.updateEventFilter(event.id)
                            eventExpanded = false
                        }
                    )
                }
            }
        }

        // 📅 Chọn ngày bắt đầu và kết thúc
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Button(onClick = { showDatePicker(context, dateFormat) { viewModel.updateDateRange(it, uiState.endDate) } }) {
                Text(if (uiState.startDate.isNullOrEmpty()) "Chọn ngày bắt đầu" else "Bắt đầu: ${uiState.startDate}")
            }
            Button(onClick = { showDatePicker(context, dateFormat) { viewModel.updateDateRange(uiState.startDate, it) } }) {
                Text(if (uiState.endDate.isNullOrEmpty()) "Chọn ngày kết thúc" else "Kết thúc: ${uiState.endDate}")
            }
        }
    }
}

// 📅 Hàm hiển thị DatePicker
fun showDatePicker(context: android.content.Context, dateFormat: SimpleDateFormat, onDateSelected: (String) -> Unit) {
    val calendar = Calendar.getInstance()
    DatePickerDialog(
        context,
        { _, year, month, day ->
            calendar.set(year, month, day)
            onDateSelected(dateFormat.format(calendar.time))
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).show()
}

// 🔝 Thanh tiêu đề
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchievementTopBar(navController: NavController) {
    TopAppBar(
        title = { Text("Danh sách thành tích", style = MaterialTheme.typography.titleLarge) },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
            }
        }
    )
}
