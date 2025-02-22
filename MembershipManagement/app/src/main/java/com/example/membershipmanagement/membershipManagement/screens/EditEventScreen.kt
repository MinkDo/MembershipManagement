package com.example.membershipmanagement.events.screens

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.membershipmanagement.viewmodel.EditEventViewModel
import com.example.membershipmanagement.viewmodel.EventViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun EditEventScreen(navController: NavController,
                    viewModel: EditEventViewModel,
                    eventViewModel: EventViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())

    fun showDateTimePicker(onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(context, { _, year, month, day ->
            TimePickerDialog(context, { _, hour, minute ->
                calendar.set(year, month, day, hour, minute)
                onDateSelected(dateFormat.format(calendar.time))
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    Scaffold(
        topBar = { EditEventTopBar(navController,viewModel, eventViewModel) }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
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
                    label = { Text("Tên sự kiện") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = uiState.location,
                    onValueChange = { newValue -> viewModel.updateField { it.copy(location = newValue) } },
                    label = { Text("Địa điểm") },
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = { showDateTimePicker { newDate -> viewModel.updateField { it.copy(startDate = newDate) } } },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (uiState.startDate.isNotEmpty()) "Bắt đầu: ${uiState.startDate}" else "Chọn ngày bắt đầu")
                }

                Button(
                    onClick = { showDateTimePicker { newDate -> viewModel.updateField { it.copy(endDate = newDate) } } },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (uiState.endDate?.isNotEmpty() == true) "Kết thúc: ${uiState.endDate}" else "Chọn ngày kết thúc")
                }

                OutlinedTextField(
                    value = uiState.description ?: "",
                    onValueChange = { newValue -> viewModel.updateField { it.copy(description = newValue) } },
                    label = { Text("Mô tả") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = uiState.fee,
                    onValueChange = { newValue -> viewModel.updateField { it.copy(fee = newValue) } },
                    label = { Text("Phí tham gia") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = uiState.maxParticipants,
                    onValueChange = { newValue -> viewModel.updateField { it.copy(maxParticipants = newValue) } },
                    label = { Text("Số người tham gia tối đa") },
                    modifier = Modifier.fillMaxWidth()
                )

                if (uiState.message.isNotEmpty()) {
                    Text(uiState.message, color = MaterialTheme.colorScheme.error)
                }

                Button(
                    onClick = {
                        if (uiState.name.isBlank() || uiState.location.isBlank()) {
                            viewModel.updateField { it.copy(message = "Tên và địa điểm không được để trống!") }
                        } else {
                            scope.launch {
                                viewModel.updateEvent {
                                    eventViewModel.fetchEvents()
                                    navController.popBackStack() }
                            }
                        }
                    },
                    enabled = !uiState.isLoading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (uiState.isLoading) "Đang cập nhật..." else "Lưu thay đổi")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditEventTopBar(navController: NavController,
                    viewModel: EditEventViewModel,
                    eventViewModel: EventViewModel) {

    TopAppBar(
        title = { Text("Chỉnh sửa sự kiện") },
        navigationIcon = {
            IconButton(onClick = {
                viewModel.resetMessage()
                eventViewModel.resetMessage()

                navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
            }
        }
    )
}

