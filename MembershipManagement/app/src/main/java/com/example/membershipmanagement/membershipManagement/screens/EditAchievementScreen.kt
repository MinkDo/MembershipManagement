package com.example.membershipmanagement.membershipManagement.screens



import UserViewModel
import android.app.DatePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
fun EditAchievementScreen(
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

    LaunchedEffect(Unit) {
        eventViewModel.fetchEvents()
        userViewModel.fetchUsers()
    }

    fun showDatePicker(onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(context, { _, year, month, day ->
            calendar.set(year, month, day)
            onDateSelected(dateFormat.format(calendar.time))
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    Scaffold(
        topBar = { EditAchievementTopBar(navController,viewModel, eventViewModel, userViewModel, achievementViewModel) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 🔹 Cập nhật đúng giá trị nhập vào
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

            // 🔹 Chọn thành viên
            var selectedUserName by remember(uiState.userId, users) {
                mutableStateOf(users.find { it.id == uiState.userId }?.fullName ?: "Chọn thành viên")
            }
            var userExpanded by remember { mutableStateOf(false) }

            Box {
                Button(onClick = { userExpanded = true }, modifier = Modifier.fillMaxWidth()) {
                    Text(selectedUserName)
                }
                DropdownMenu(expanded = userExpanded, onDismissRequest = { userExpanded = false }) {
                    users.forEach { user ->
                        DropdownMenuItem(
                            text = { Text(user.fullName) },
                            onClick = {
                                selectedUserName = user.fullName
                                viewModel.updateField { it.copy(userId = user.id) }
                                userExpanded = false
                            }
                        )
                    }
                }
            }

            // 🔹 Chọn sự kiện
            var selectedEventName by remember(uiState.eventId, events) {
                mutableStateOf(events.find { it.id.toString() == uiState.eventId }?.name ?: "Chọn sự kiện")
            }
            var eventExpanded by remember { mutableStateOf(false) }

            Box {
                Button(onClick = { eventExpanded = true }, modifier = Modifier.fillMaxWidth()) {
                    Text(selectedEventName)
                }
                DropdownMenu(expanded = eventExpanded, onDismissRequest = { eventExpanded = false }) {
                    events.forEach { event ->
                        DropdownMenuItem(
                            text = { Text(event.name) },
                            onClick = {
                                selectedEventName = event.name
                                viewModel.updateField { it.copy(eventId = event.id.toString()) }
                                eventExpanded = false
                            }
                        )
                    }
                }
            }

            // 🔹 Chọn ngày đạt được
            Button(
                onClick = { showDatePicker { newValue -> viewModel.updateField { it.copy(dateAchieved = newValue) } } },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (uiState.dateAchieved.isNotEmpty()) "Ngày đạt được: ${uiState.dateAchieved}" else "Chọn ngày đạt được")
            }

            Button(
                onClick = { scope.launch {
                    achievementViewModel.fetchFilteredAchievements()
                    viewModel.updateAchievement { navController.popBackStack()
                    } } },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("💾 Lưu thay đổi")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditAchievementTopBar(navController: NavController,
                          viewModel: CreateAchievementViewModel,
                          eventViewModel: EventViewModel,
                          userViewModel: UserViewModel,
                          achievementViewModel: AchievementViewModel) {

    TopAppBar(
        title = { Text("Chỉnh sửa thành tích") },
        navigationIcon = {
            IconButton(onClick = {
                viewModel.resetMessage()
                eventViewModel.resetMessage()
                userViewModel.resetMessage()
                achievementViewModel.resetMessage()
                navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
            }
        }
    )
}
