package com.example.membershipmanagement.membershipManagement.screens

import android.content.Context
import android.graphics.pdf.PdfDocument
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.graphics.drawscope.drawRoundRect
import androidx.compose.ui.unit.dp
import java.time.LocalDate

import androidx.navigation.NavController

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ReportScreen(
    navController: NavController
) {
    var selectedMonth by remember { mutableStateOf(LocalDate.now().monthValue) }
    val months = (1..12).toList()

    // Giả lập dữ liệu thu/chi theo tháng
    val reports = mapOf(
        1 to Pair(2000000.0, 800000.0),
        2 to Pair(1500000.0, 900000.0),
        3 to Pair(1800000.0, 700000.0),
    )
    val (totalIncome, totalExpense) = reports[selectedMonth] ?: Pair(0.0, 0.0)

    Scaffold(
        topBar = {
            ReportTopBar(navController)
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Dropdown chọn tháng
            MonthDropdown(selectedMonth, months) { selectedMonth = it }

            Spacer(modifier = Modifier.height(16.dp))

            ReportCard("Tổng thu", totalIncome, Color.Green)
            ReportCard("Tổng chi", totalExpense, Color.Red)

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "Chi tiết giao dịch tháng ${selectedMonth}", style = MaterialTheme.typography.titleMedium)

            ReportChart(income = totalIncome, expense = totalExpense)

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { exportReportToPDF(selectedMonth, totalIncome, totalExpense) }) {
                Text("Xuất báo cáo PDF")
            }
        }
    }
}

@Composable
fun MonthDropdown(selectedMonth: Int, months: List<Int>, onMonthSelected: (Int) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        Button(onClick = { expanded = true }) {
            Text("Tháng ${selectedMonth}")
        }

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            months.forEach { month ->
                DropdownMenuItem(
                    text = { Text("Tháng $month") },
                    onClick = {
                        onMonthSelected(month)
                        expanded = false
                    }
                )
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportTopBar(
    navController: NavController
) {
    TopAppBar(
        title = { Text("Báo cáo", style = MaterialTheme.typography.titleLarge) },
        navigationIcon = {
            IconButton(onClick = {
                navController.popBackStack()
            }) {
                Text("←")
            }
        }
    )
}

@Composable
fun ReportCard(title: String, amount: Double, color: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(title, style = MaterialTheme.typography.bodyLarge)
            Text("$amount đ", color = color, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
fun ReportChart(income: Double, expense: Double) {
    val total = income + expense
    val incomeRatio = if (total > 0) (income / total).toFloat() else 0f
    val expenseRatio = if (total > 0) (expense / total).toFloat() else 0f

    Canvas(modifier = Modifier.fillMaxWidth().height(40.dp)) {
        drawRoundRect(
            color = Color.Green,
            size = Size(width = size.width * incomeRatio, height = size.height),
            cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
        )
        drawRoundRect(
            color = Color.Red,
            size = Size(width = size.width * expenseRatio, height = size.height),
            topLeft = androidx.compose.ui.geometry.Offset(size.width * incomeRatio, 0f),
            cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
        )
    }
}

fun exportReportToPDF(month: Int, income: Double, expense: Double) {
    val pdfDocument = PdfDocument()
    val pageInfo = PdfDocument.PageInfo.Builder(300, 600, 1).create()
    val page = pdfDocument.startPage(pageInfo)

    val canvas = page.canvas
    val paint = android.graphics.Paint()

    paint.textSize = 16f
    canvas.drawText("Báo cáo tài chính tháng $month", 50f, 50f, paint)

    paint.textSize = 14f
    canvas.drawText("Tổng thu: ${income}đ", 50f, 100f, paint)
    canvas.drawText("Tổng chi: ${expense}đ", 50f, 150f, paint)

    pdfDocument.finishPage(page)

    val directory = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Reports")
    if (!directory.exists()) directory.mkdirs()

    val file = File(directory, "BaoCao_Thang_$month.pdf")
    try {
        val fos = FileOutputStream(file)
        pdfDocument.writeTo(fos)
        fos.close()
    } catch (e: IOException) {
        e.printStackTrace()
    }

    pdfDocument.close()
}
