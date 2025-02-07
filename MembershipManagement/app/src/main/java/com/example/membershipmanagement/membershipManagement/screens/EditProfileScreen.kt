package com.example.membershipmanagement.membershipManagement.screens

import com.example.membershipmanagement.viewmodel.ProfileViewModel

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil3.compose.rememberAsyncImagePainter
import com.example.membershipmanagement.R


//@Composable
//fun EditProfileScreen(
//    navController: NavController,
//    editProfileViewModel: ProfileViewModel
//) {
//    val uiState by editProfileViewModel.profileState.collectAsState()
//
//    val context = LocalContext.current
//    val imagePickerLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.GetContent()
//    ) { uri: Uri? ->
//        uri?.let { TODO() }
//    }
//
//    Scaffold(
//        topBar = { EditProfileTopBar(navController) }
//    ) { paddingValues ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues)
//                .padding(16.dp),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            // Ảnh đại diện
//            Box(
//                modifier = Modifier
//                    .size(120.dp)
//                    .clip(CircleShape)
//                    .clickable { imagePickerLauncher.launch("image/*") },
//                contentAlignment = Alignment.Center
//            ) {
//                Image(
//                    painter = if (TODO()) rememberAsyncImagePainter(TODO())
//                    else painterResource(id = R.drawable.avatar),
//                    contentDescription = "Ảnh đại diện",
//                    modifier = Modifier.fillMaxSize()
//                )
//                Icon(Icons.Default.CameraAlt, contentDescription = "Chọn ảnh", tint = Color.White)
//            }
//
//            Spacer(modifier = Modifier.height(16.dp))
//
//            // Họ và tên
//            OutlinedTextField(
//                value = uiState.fullName,
//                onValueChange = { TODO() },
//                label = {
//                    "Họ và tên" },
//                modifier = Modifier.fillMaxWidth()
//            )
//
//            // Ngày sinh
//            OutlinedTextField(
//                value = uiState.birthDate,
//                onValueChange = { TODO()) },
//                label = { "Ngày sinh" },
//                modifier = Modifier.fillMaxWidth()
//            )
//
//            // Giới tính
//            DropdownField(
//                label = "Giới tính",
//                options = listOf("Nam", "Nữ"),
//                selectedOption = uiState.gender,
//                onOptionSelected = { TODO() }
//            )
//
//            // Cấp đai
//            DropdownField(
//                label = "Cấp đai",
//                options = listOf("Trắng", "Vàng", "Xanh", "Đỏ", "Đen"),
//                selectedOption = uiState.beltLevel,
//                onOptionSelected = { TODO() }
//            )
//
//            Spacer(modifier = Modifier.height(24.dp))
//
//            // Nút Lưu thay đổi
//            Button(
//                onClick = { TODO() },
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                Text("Lưu thay đổi")
//            }
//        }
//    }
//}
//
//@Composable
//fun DropdownField(label: String, options: List<String>, selectedOption: String, onOptionSelected: (String) -> Unit) {
//    var expanded by remember { mutableStateOf(false) }
//
//    Column {
//        Text(label, fontWeight = FontWeight.Medium)
//        Button(onClick = { expanded = true }) {
//            Text(selectedOption)
//        }
//        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
//            options.forEach { option ->
//                DropdownMenuItem(
//                    text = { Text(option) },
//                    onClick = {
//                        onOptionSelected(option)
//                        expanded = false
//                    }
//                )
//            }
//        }
//    }
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun EditProfileTopBar(navController: NavController) {
//    TopAppBar(
//        title = { Text("Chỉnh sửa hồ sơ", style = MaterialTheme.typography.titleLarge) },
//        navigationIcon = {
//            IconButton(onClick = { navController.popBackStack() }) {
//                Icon(Icons.Default.CameraAlt, contentDescription = "Quay lại")
//            }
//        }
//    )
//}
