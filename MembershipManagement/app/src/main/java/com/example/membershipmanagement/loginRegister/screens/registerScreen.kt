package com.example.membershipmanagement.loginRegister.screens

import android.app.DatePickerDialog
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.rememberAsyncImagePainter

import com.example.membershipmanagement.R
import com.example.membershipmanagement.navigation.Screen
import java.util.*

@Composable
fun RegisterScreen(navController: NavController) {
    val context = LocalContext.current

    // State lưu trữ thông tin hội viên
    var fullName by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Nam") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var joinDate by remember { mutableStateOf("") }
    var beltLevel by remember { mutableStateOf("") }
    var profileImageUri by remember { mutableStateOf<Uri?>(null) }

    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        profileImageUri = uri
    }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) {
            profileImageUri = Uri.parse(bitmap.toString())  // Chỉ để minh họa, cần xử lý ảnh đúng cách
        }
    }

    val showDatePicker = { onDateSelected: (String) -> Unit ->
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            context,
            { _, year, month, day ->
                onDateSelected("$day/${month + 1}/$year")
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    Scaffold(
        topBar = { RegisterTopBar(navController) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                // Ảnh đại diện
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .clickable { imagePicker.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (profileImageUri == null) {
                        Image(
                            painter = painterResource(id = R.drawable.avatar),
                            contentDescription = "Ảnh đại diện",
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Image(
                            painter = rememberAsyncImagePainter(profileImageUri),
                            contentDescription = "Ảnh đại diện",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
                TextButton(onClick = { cameraLauncher.launch(null) }) {
                    Text("Chụp ảnh")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Form nhập thông tin
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Họ và tên") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = birthDate, onValueChange = { },
                    label = { Text("Ngày sinh") },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker { birthDate = it } }) {
                            Icon(
                                painterResource(id = R.drawable.calendar),
                                contentDescription = "Chọn ngày"
                            )
                        }
                    }
                )

                // Giới tính
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Giới tính:")
                    Spacer(modifier = Modifier.width(8.dp))
                    GenderRadioButton("Nam", gender) { gender = it }
                    GenderRadioButton("Nữ", gender) { gender = it }
                }

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Số điện thoại") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Địa chỉ") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = joinDate, onValueChange = { },
                    label = { Text("Ngày tham gia") },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker { joinDate = it } }) {
                            Icon(
                                painterResource(id = R.drawable.calendar),
                                contentDescription = "Chọn ngày"
                            )
                        }
                    }
                )

                OutlinedTextField(
                    value = beltLevel,
                    onValueChange = { beltLevel = it },
                    label = { Text("Cấp đai hiện tại") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(onClick = {
                    // TODO: Xử lý đăng ký hội viên
                    navController.navigate(Screen.Home.route)
                }, modifier = Modifier.fillMaxWidth()) {
                    Text("Đăng ký")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterTopBar(navController: NavController) {
    TopAppBar(
        title = { Text("Đăng ký hội viên", style = MaterialTheme.typography.titleLarge) },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Text("←")
            }
        }
    )
}

@Composable
fun GenderRadioButton(label: String, selectedValue: String, onSelected: (String) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { onSelected(label) }) {
        RadioButton(selected = selectedValue == label, onClick = { onSelected(label) })
        Text(label)
    }
}
