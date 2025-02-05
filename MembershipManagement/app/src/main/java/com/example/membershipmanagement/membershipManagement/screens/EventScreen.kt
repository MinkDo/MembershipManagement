package com.example.membershipmanagement.membershipManagement.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.membershipmanagement.membershipManagement.components.EventItem

import androidx.navigation.NavController

@Composable
fun EventScreen(
    navController: NavController
) {
    var events by remember { mutableStateOf(listOf("Sự kiện A", "Sự kiện B", "Sự kiện C")) }

    Scaffold(
        topBar = {
            EventTopBar(navController)
                 },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                events = events + "Sự kiện mới"
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
                items(events) { event ->
                    EventItem(eventName = event)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventTopBar(
    navController: NavController
) {
    TopAppBar(
        title = { Text("Quản lý sự kiện", style = MaterialTheme.typography.titleLarge) },
        navigationIcon = {
            IconButton(onClick = {
                navController.popBackStack()
                }
            ) {
                Text("←")
            }
        }
    )
}
