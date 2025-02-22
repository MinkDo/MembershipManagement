package com.example.membershipmanagement.reports.screens

import android.app.DatePickerDialog
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
import com.example.membershipmanagement.viewmodel.ReportViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ReportScreen(navController: NavController, reportViewModel: ReportViewModel = viewModel()) {
    val uiState by reportViewModel.uiState.collectAsState()
    val context = LocalContext.current
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    LaunchedEffect(Unit) {
        reportViewModel.fetchReportData()
    }

    // Hàm chọn ngày
    fun showDatePicker(initialDate: String?, onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        initialDate?.let {
            dateFormat.parse(it)?.let { date -> calendar.time = date }
        }

        DatePickerDialog(context, { _, year, month, day ->
            calendar.set(year, month, day)
            onDateSelected(dateFormat.format(calendar.time))
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    Scaffold(
        topBar = { ReportTopBar(navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
        ) {
            // Bộ lọc ngày
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { showDatePicker(uiState.startDate) { reportViewModel.updateDateRange(it, uiState.endDate) } },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(uiState.startDate ?: "Chọn ngày bắt đầu")
                }

                Button(
                    onClick = { showDatePicker(uiState.endDate) { reportViewModel.updateDateRange(uiState.startDate, it) } },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(uiState.endDate ?: "Chọn ngày kết thúc")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else if (uiState.errorMessage.isNotEmpty()) {
                Text("Lỗi: ${uiState.errorMessage}", color = MaterialTheme.colorScheme.error)
            } else {
                uiState.reportData?.let { report ->
                    report.forEach { data ->
                        ReportItem(type = data.type, transactionCount = data.transactionCount, totalAmount = data.totalAmount, averageAmount = data.averageAmount)
                    }
                }
            }
        }
    }
}

@Composable
fun ReportItem(type: Int, transactionCount: Int, totalAmount: Int, averageAmount: Int) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = if (type == 0) "📥 Thu nhập" else "📤 Chi tiêu", style = MaterialTheme.typography.titleMedium)
            Text(text = "Số giao dịch: $transactionCount", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Tổng số tiền: ${formatCurrency(totalAmount)}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Trung bình mỗi giao dịch: ${formatCurrency(averageAmount)}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

// Định dạng số tiền
fun formatCurrency(amount: Int): String {
    return "%,d VND".format(amount)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportTopBar(navController: NavController) {
    TopAppBar(
        title = { Text("Báo cáo tài chính", style = MaterialTheme.typography.titleLarge) },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại")
            }
        }
    )
}
