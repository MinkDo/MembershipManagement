package com.example.membershipmanagement.finances.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.membershipmanagement.viewmodel.CreateFinanceViewModel
import com.example.membershipmanagement.viewmodel.FinanceViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CreateFinanceScreen(
    navController: NavController,
    viewModel: CreateFinanceViewModel,
    financeViewModel: FinanceViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())

    fun showDatePicker(onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(context, { _, year, month, day ->
            calendar.set(year, month, day)
            onDateSelected(dateFormat.format(calendar.time))
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    Scaffold(
        topBar = { CreateFinanceTopBar(navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 🔹 Chọn loại giao dịch (Thu nhập / Chi tiêu)
            var expanded by remember { mutableStateOf(false) }
            val transactionTypes = listOf("Thu nhập", "Chi tiêu")
            Box {
                Button(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
                    Text(transactionTypes[uiState.type])
                }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    transactionTypes.forEachIndexed { index, label ->
                        DropdownMenuItem(
                            text = { Text(label) },
                            onClick = {
                                viewModel.updateField { it.copy(type = index) }
                                expanded = false
                            }
                        )
                    }
                }
            }

            // 🔹 Nhập danh mục
            OutlinedTextField(
                value = uiState.category,
                onValueChange = { newValue -> viewModel.updateField { it.copy(category = newValue) } },
                label = { Text("Danh mục") },
                modifier = Modifier.fillMaxWidth()
            )

            // 🔹 Nhập số tiền
            OutlinedTextField(
                value = uiState.amount,
                onValueChange = { newValue -> viewModel.updateField { it.copy(amount = newValue) } },
                label = { Text("Số tiền") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            // 🔹 Nhập mô tả
            OutlinedTextField(
                value = uiState.description,
                onValueChange = { newValue -> viewModel.updateField { it.copy(description = newValue) } },
                label = { Text("Mô tả") },
                modifier = Modifier.fillMaxWidth()
            )

            // 🔹 Chọn ngày giao dịch
            Button(
                onClick = { showDatePicker { newValue -> viewModel.updateField { it.copy(transactionDate = newValue) } } },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (uiState.transactionDate.isNotEmpty()) "Ngày giao dịch: ${uiState.transactionDate}" else "Chọn ngày giao dịch")
            }

            if (uiState.message.isNotEmpty()) {
                Text(uiState.message, color = MaterialTheme.colorScheme.error)
            }

            // ✅ Nút tạo giao dịch
            Button(
                onClick = {
                    if (uiState.category.isBlank() || uiState.amount.isBlank()) {
                        viewModel.updateField { it.copy(message = "Hãy điền đầy đủ thông tin!") }
                    } else {
                        scope.launch {
                            viewModel.createFinance {
                                financeViewModel.fetchFinances()
                                navController.popBackStack()
                            }
                        }
                    }
                },
                enabled = !uiState.isLoading,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(if (uiState.isLoading) "Đang tạo..." else "💰 Tạo giao dịch")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateFinanceTopBar(navController: NavController) {
    TopAppBar(
        title = { Text("Tạo giao dịch") },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
            }
        }
    )
}
