package com.example.membershipmanagement.achievements.screens

import UserViewModel
import android.app.DatePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.membershipmanagement.viewmodel.AchievementViewModel
import com.example.membershipmanagement.viewmodel.CreateAchievementViewModel
import com.example.membershipmanagement.viewmodel.EventViewModel

import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CreateAchievementScreen(
    navController: NavController,
    viewModel: CreateAchievementViewModel,
    eventViewModel: EventViewModel,
    userViewModel: UserViewModel,
    achievementViewModel: AchievementViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val eventUiState by eventViewModel.uiState.collectAsState()
    val userUiState by userViewModel.uiState.collectAsState()
    val events = eventUiState.events
    val users = userUiState.users

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    var eventExpanded by remember { mutableStateOf(false) }
    var userExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        eventViewModel.fetchEvents() // Tải danh sách sự kiện
        userViewModel.fetchUsers()   // Tải danh sách thành viên
    }

    fun showDatePicker(onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(context, { _, year, month, day ->
            calendar.set(year, month, day)
            onDateSelected(dateFormat.format(calendar.time))
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    Scaffold(
        topBar = { CreateAchievementTopBar(navController,viewModel, eventViewModel, userViewModel, achievementViewModel) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = uiState.name,
                onValueChange = { newValue -> viewModel.updateField { it.copy(name = newValue) } },
                label = { Text("Tên thành tích") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.description,
                onValueChange = { newValue -> viewModel.updateField { it.copy(description = newValue) } },
                label = { Text("Mô tả") },
                modifier = Modifier.fillMaxWidth()
            )

            // 🔽 Dropdown chọn thành viên
            Column {
                Text("Thành viên", style = MaterialTheme.typography.bodyLarge)
                Button(onClick = { userExpanded = true }, modifier = Modifier.fillMaxWidth()) {
                    Text(users.find { it.id == uiState.userId }?.fullName ?: "Chọn thành viên")
                }
                DropdownMenu(expanded = userExpanded, onDismissRequest = { userExpanded = false }) {
                    users.forEach { user ->
                        DropdownMenuItem(
                            text = { Text(user.fullName) },
                            onClick = {
                                viewModel.updateField { it.copy(userId = user.id) }
                                userExpanded = false
                            }
                        )
                    }
                }
            }

            // 🔽 Dropdown chọn sự kiện
            Column {
                Text("Sự kiện", style = MaterialTheme.typography.bodyLarge)
                Button(onClick = { eventExpanded = true }, modifier = Modifier.fillMaxWidth()) {
                    Text(events.find { it.id == uiState.eventId.toIntOrNull() }?.name ?: "Chọn sự kiện")
                }
                DropdownMenu(expanded = eventExpanded, onDismissRequest = { eventExpanded = false }) {
                    events.forEach { event ->
                        DropdownMenuItem(
                            text = { Text(event.name) },
                            onClick = {
                                viewModel.updateField { it.copy(eventId = event.id.toString()) }
                                eventExpanded = false
                            }
                        )
                    }
                }
            }

            Button(
                onClick = { showDatePicker { newDate -> viewModel.updateField { it.copy(dateAchieved = newDate) } } },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (uiState.dateAchieved.isNotEmpty()) "Ngày đạt được: ${uiState.dateAchieved}" else "Chọn ngày đạt được")
            }

            if (uiState.message.isNotEmpty()) {
                Text(uiState.message, color = MaterialTheme.colorScheme.error)
            }

            Button(
                onClick = {
                    if (uiState.name.isBlank() || uiState.userId.isBlank() || uiState.eventId.isBlank()) {
                        viewModel.updateField { it.copy(message = "Hãy điền đầy đủ thông tin!") }
                    } else {
                        scope.launch {
                            viewModel.createAchievement {
                                achievementViewModel.fetchFilteredAchievements()
                                navController.popBackStack()
                            }
                        }
                    }
                },
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (uiState.isLoading) "Đang tạo..." else "Tạo thành tích")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateAchievementTopBar(navController: NavController,
                            viewModel: CreateAchievementViewModel,
                            eventViewModel: EventViewModel,
                            userViewModel: UserViewModel,
                            achievementViewModel: AchievementViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    TopAppBar(
        title = { Text("Tạo thành tích mới") },
        navigationIcon = {
            IconButton(onClick = {
                uiState.copy(message = "")
                navController.popBackStack()
            }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
            }
        }
    )
}
