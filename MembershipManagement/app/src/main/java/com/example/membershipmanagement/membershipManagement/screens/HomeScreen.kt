package com.example.membershipmanagement.membershipManagement.screens

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter


import coil3.compose.rememberAsyncImagePainter
import com.example.membershipmanagement.R
import com.example.membershipmanagement.navigation.Screen
import com.example.membershipmanagement.viewmodel.ProfileViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    profileViewModel: ProfileViewModel
) {

    val profileState by profileViewModel.profileState.collectAsState()
    profileViewModel.getProfile()
    val avatarUrl = profileViewModel.getAvatarUrl()
    Scaffold(
        topBar = { HomeTopBar() }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Ảnh đại diện + Chỉnh sửa hồ sơ
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .clickable { /* Mở màn hình chỉnh sửa profile */ },
                contentAlignment = Alignment.Center
            ) {
                Log.d("HomeScreen","Url ${profileViewModel.getAvatarUrl()}")
                Image(

                    painter = rememberImagePainter(avatarUrl),
                    contentDescription = "Ảnh đại diện",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Tên  và Vai trò
            Text(
                text = profileViewModel.getName(),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text =
                    profileViewModel.getRole()
                ,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Danh sách chức năng
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    HomeButton(text = "Quản lý sự kiện") {
                        navController.navigate(Screen.Events.route)
                    }
                    HomeButton(text = "Thông tin cá nhân") {
                        navController.navigate(Screen.Profile.route)
                    }
                    HomeButton(text = "Đổi mật khẩu") {
                        navController.navigate(Screen.ChangePassword.route)
                    }

                    if (profileViewModel.getRole() == "Admin") {
                        HomeButton(text = "Đăng ký tài khoản") {
                            navController.navigate(Screen.Register.route)
                        }
                        HomeButton(text = "Quản lý hội viên") {
                            navController.navigate(Screen.Members.route)
                        }
                        HomeButton(text = "Quản lý tài chính") {
                            navController.navigate(Screen.Finance.route)
                        }
                        HomeButton(text = "Thống kê & Báo cáo") {
                            navController.navigate(Screen.Reports.route)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar() {
    TopAppBar(
        title = { Text("Trang chủ", style = MaterialTheme.typography.titleLarge) },
        actions = {
            IconButton(onClick = { /* Mở chỉnh sửa profile */ }) {
                Icon(Icons.Default.Edit, contentDescription = "Chỉnh sửa hồ sơ")
            }
        }
    )
}

@Composable
fun HomeButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .height(50.dp)
    ) {
        Text(text, fontSize = 16.sp)
    }
}
