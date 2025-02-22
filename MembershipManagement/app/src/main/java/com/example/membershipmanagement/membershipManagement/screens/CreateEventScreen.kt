package com.example.membershipmanagement.membershipManagement.screens


import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.membershipmanagement.viewmodel.CreateEventViewModel
import com.example.membershipmanagement.viewmodel.EventViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CreateEventScreen(navController: NavController, viewModel: CreateEventViewModel, eventViewModel: EventViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    val context = LocalContext.current
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
        topBar = { CreateEventTopBar(navController,viewModel, eventViewModel) }
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
                onValueChange = { viewModel.updateName(it) },
                label = { Text("Tên sự kiện") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.location,
                onValueChange = { viewModel.updateLocation(it) },
                label = { Text("Địa điểm") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = { showDateTimePicker { viewModel.updateStartDate(it) } },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (uiState.startDate.isNotEmpty()) "Bắt đầu: ${uiState.startDate}" else "Chọn ngày bắt đầu")
            }

            Button(
                onClick = { showDateTimePicker { viewModel.updateEndDate(it) } },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (uiState.endDate.isNotEmpty()) "Kết thúc: ${uiState.endDate}" else "Chọn ngày kết thúc")
            }

            OutlinedTextField(
                value = uiState.description,
                onValueChange = { viewModel.updateDescription(it) },
                label = { Text("Mô tả") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.fee,
                onValueChange = { viewModel.updateFee(it) },
                label = { Text("Phí tham gia") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.maxParticipants,
                onValueChange = { viewModel.updateMaxParticipants(it) },
                label = { Text("Số người tham gia tối đa") },
                modifier = Modifier.fillMaxWidth()
            )

            if (uiState.message.isNotEmpty()) {
                Text(uiState.message, color = MaterialTheme.colorScheme.primary)
            }

            Button(
                onClick = {


                    viewModel.createEvent {
                        eventViewModel.fetchEvents()
                        navController.popBackStack() }
                          },
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (uiState.isLoading) "Đang tạo..." else "Tạo sự kiện")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEventTopBar(navController: NavController,
                      viewModel: CreateEventViewModel,
                      eventViewModel: EventViewModel) {

    TopAppBar(
        title = { Text("Tạo sự kiện mới") },
        navigationIcon = {
            IconButton(onClick = {
                viewModel.resetMessage()
                eventViewModel.resetMessage()
                navController.popBackStack()
            }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
            }
        }
    )
}
