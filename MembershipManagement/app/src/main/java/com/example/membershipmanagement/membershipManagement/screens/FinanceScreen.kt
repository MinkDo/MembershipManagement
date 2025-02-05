package com.example.membershipmanagement.membershipManagement.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.membershipmanagement.data.repository.Transaction
import com.example.membershipmanagement.membershipManagement.components.FinanceItem

import androidx.navigation.NavController
@Composable
fun FinanceScreen(
    navController: NavController
) {
    var transactions by remember { mutableStateOf(listOf(
        Transaction("Đóng quỹ tháng 1", 500000.0, "income"),
        Transaction("Mua dụng cụ", -200000.0, "expense"),
        Transaction("Đóng quỹ tháng 2", 500000.0, "income")
    )) }

    Scaffold(
        topBar = {
            FinanceTopBar(navController)
                 },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                transactions = transactions + Transaction("Giao dịch mới", 100000.0, "income")
            }) {
                Text("+")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            LazyColumn {
                items(transactions) { transaction ->
                    FinanceItem(transaction)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanceTopBar(
    navController: NavController
) {
    TopAppBar(
        title = { Text("Quản lý tài chính", style = MaterialTheme.typography.titleLarge) },
        navigationIcon = {
            IconButton(onClick = {
                navController.popBackStack()
            }) {
                Text("←")
            }
        }
    )
}
