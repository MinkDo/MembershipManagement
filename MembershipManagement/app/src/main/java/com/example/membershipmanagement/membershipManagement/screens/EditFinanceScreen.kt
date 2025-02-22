package com.example.membershipmanagement.finances.screens

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
import com.example.membershipmanagement.viewmodel.CreateFinanceViewModel
import com.example.membershipmanagement.viewmodel.FinanceViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun EditFinanceScreen(navController: NavController, viewModel: CreateFinanceViewModel, financeViewModel: FinanceViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())

    // Hiển thị DatePicker
    fun showDatePicker(onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(context, { _, year, month, day ->
            calendar.set(year, month, day)
            onDateSelected(dateFormat.format(calendar.time))
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    Scaffold(
        topBar = { EditFinanceTopBar(navController,viewModel, financeViewModel) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = uiState.category,
                onValueChange = { newValue -> viewModel.updateField { it.copy(category = newValue) } },
                label = { Text("Loại giao dịch") },
                modifier = Modifier.fillMaxWidth()
            )

            // 🔄 Chọn loại giao dịch (Thu nhập / Chi tiêu)
            var expanded by remember { mutableStateOf(false) }
            Box {
                Button(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
                    Text(if (uiState.type == 0) "Thu nhập" else "Chi tiêu")
                }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    DropdownMenuItem(
                        text = { Text("Thu nhập") },
                        onClick = {
                            viewModel.updateField { it.copy(type = 0) }
                            expanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Chi tiêu") },
                        onClick = {
                            viewModel.updateField { it.copy(type = 1) }
                            expanded = false
                        }
                    )
                }
            }

            OutlinedTextField(
                value = uiState.amount,
                onValueChange = { newValue -> viewModel.updateField { it.copy(amount = newValue) } },
                label = { Text("Số tiền") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = { showDatePicker { newValue -> viewModel.updateField { it.copy(transactionDate = newValue) } } },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (uiState.transactionDate.isNotEmpty()) "Ngày giao dịch: ${uiState.transactionDate}" else "Chọn ngày giao dịch")
            }

            OutlinedTextField(
                value = uiState.description,
                onValueChange = { newValue -> viewModel.updateField { it.copy(description = newValue) } },
                label = { Text("Mô tả") },
                modifier = Modifier.fillMaxWidth()
            )

            if (uiState.message.isNotEmpty()) {
                Text(uiState.message, color = MaterialTheme.colorScheme.error)
            }

            Button(
                onClick = {
                        viewModel.updateFinance {
                            financeViewModel.fetchFinances()
                            navController.popBackStack() }

                },
                enabled = !uiState.isLoading,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(if (uiState.isLoading) "Đang cập nhật..." else "💾 Lưu thay đổi")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditFinanceTopBar(navController: NavController,viewModel: CreateFinanceViewModel, financeViewModel: FinanceViewModel) {

    TopAppBar(
        title = { Text("Chỉnh sửa giao dịch") },
        navigationIcon = {
            IconButton(onClick = {
                viewModel.resetMessage()
                financeViewModel.resetMessage()
                navController.popBackStack()
            }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
            }
        }
    )
}
