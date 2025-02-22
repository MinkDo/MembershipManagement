package com.example.membershipmanagement.finances.screens

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
import androidx.navigation.NavController
import com.example.membershipmanagement.finances.components.FinanceItem
import com.example.membershipmanagement.navigation.Screen
import com.example.membershipmanagement.viewmodel.CreateFinanceViewModel
import com.example.membershipmanagement.viewmodel.FinanceViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun FinanceScreen(
    navController: NavController,
    financeViewModel: FinanceViewModel,
    createFinanceViewModel: CreateFinanceViewModel
) {
    val uiState by financeViewModel.uiState.collectAsState()
    val finances = uiState.finances
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    Scaffold(
        topBar = { FinanceTopBar(navController) },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                navController.navigate(Screen.CreateFinance.route)
            }) {
                Icon(Icons.Default.Add, contentDescription = "Thêm giao dịch")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // 🔍 Thanh tìm kiếm danh mục
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { financeViewModel.updateSearchQuery(it) },
                label = { Text("Tìm kiếm theo danh mục") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 📅 Bộ lọc ngày giao dịch
            DateRangeFilter(
                startDate = uiState.startDate,
                endDate = uiState.endDate,
                onStartDateSelected = { financeViewModel.updateDateRange(it, uiState.endDate) },
                onEndDateSelected = { financeViewModel.updateDateRange(uiState.startDate, it) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 📌 Bộ lọc loại giao dịch
            TransactionTypeFilter(
                selectedType = uiState.selectedType,
                onTypeSelected = { financeViewModel.updateTransactionType(it) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // 🚀 Hiển thị danh sách giao dịch
            when {
                uiState.isLoading -> CircularProgressIndicator()
                uiState.errorMessage.isNotEmpty() -> Text("Lỗi: ${uiState.errorMessage}", color = MaterialTheme.colorScheme.error)
                finances.isEmpty() -> Text("Không có giao dịch nào", modifier = Modifier.padding(16.dp))
                else -> {
                    LazyColumn {
                        items(finances) { finance ->
                            FinanceItem(
                                finance = finance,
                                onClick = {
                                    coroutineScope.launch {
                                        createFinanceViewModel.getFinanceById(finance.id.toString())
                                        delay(500)
                                        navController.navigate(Screen.EditFinance.route)
                                    }
                                },
                                onDelete = { financeViewModel.deleteFinance(finance.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

// 📅 Bộ lọc ngày giao dịch
@Composable
fun DateRangeFilter(
    startDate: String?,
    endDate: String?,
    onStartDateSelected: (String) -> Unit,
    onEndDateSelected: (String) -> Unit
) {
    val context = LocalContext.current
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    fun showDatePicker(initialDate: String?, onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            context, { _, year, month, day ->
                calendar.set(year, month, day)
                onDateSelected(dateFormat.format(calendar.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Button(onClick = { showDatePicker(startDate, onStartDateSelected) }) {
            Text(if (startDate != null) "Bắt đầu: $startDate" else "Chọn ngày bắt đầu")
        }
        Button(onClick = { showDatePicker(endDate, onEndDateSelected) }) {
            Text(if (endDate != null) "Kết thúc: $endDate" else "Chọn ngày kết thúc")
        }
    }
}

// 📌 Bộ lọc loại giao dịch
@Composable
fun TransactionTypeFilter(selectedType: Int?, onTypeSelected: (Int?) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val typeOptions = listOf("Tất cả" to null, "Thu" to 0, "Chi" to 1)

    Column {
        Text("Lọc theo loại giao dịch", style = MaterialTheme.typography.bodyLarge)
        Button(onClick = { expanded = true }) {
            Text(typeOptions.find { it.second == selectedType }?.first ?: "Tất cả")
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            typeOptions.forEach { (label, value) ->
                DropdownMenuItem(
                    text = { Text(label) },
                    onClick = {
                        onTypeSelected(value)
                        expanded = false
                    }
                )
            }
        }
    }
}

// 🔝 Thanh điều hướng
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceTopBar(navController: NavController) {
    TopAppBar(
        title = { Text("Quản lý tài chính", style = MaterialTheme.typography.titleLarge) },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
            }
        }
    )
}
