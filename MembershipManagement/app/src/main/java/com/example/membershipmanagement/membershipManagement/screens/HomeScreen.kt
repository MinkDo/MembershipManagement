package com.example.membershipmanagement.membershipManagement.screens

import UserViewModel
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Logout
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
import com.example.membershipmanagement.viewmodel.AchievementViewModel
import com.example.membershipmanagement.viewmodel.AuthViewModel
import com.example.membershipmanagement.viewmodel.EventViewModel
import com.example.membershipmanagement.viewmodel.FinanceViewModel
import com.example.membershipmanagement.viewmodel.ProfileViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    profileViewModel: ProfileViewModel,
    userViewModel: UserViewModel,
    eventViewModel: EventViewModel,
    financeViewModel: FinanceViewModel,
    achievementViewModel: AchievementViewModel,
    authViewModel: AuthViewModel
) {



    val avatarUrl = profileViewModel.getAvatarUrl()
    Scaffold(
        topBar = { HomeTopBar(navController, authViewModel) }
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

                    painter = if (avatarUrl != "") rememberImagePainter(avatarUrl)
                    else painterResource(id = R.drawable.avatar),
                    contentDescription = "Ảnh đại diện",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Tên  và Vai trò
            Text(
                text = profileViewModel.getFullName()   ,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = profileViewModel.getHighestRole(),
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
                        eventViewModel.fetchEvents()
                        navController.navigate(Screen.Events.route)
                    }
                    HomeButton(text = "Thông tin cá nhân") {
                        navController.navigate(Screen.Profile.route)
                    }
                    HomeButton(text = "Đổi mật khẩu") {
                        navController.navigate(Screen.ChangePassword.route)
                    }

                    if (profileViewModel.getHighestRole() != "Member") {

                        HomeButton(text = "Quản lý hội viên") {
                            navController.navigate(Screen.Members.route)
                            userViewModel.fetchUsers()
                        }
                        HomeButton(text = "Quản lý thành tích") {
                            achievementViewModel.fetchFilteredAchievements()
                            navController.navigate(Screen.Achievement.route)
                        }
                        if (profileViewModel.getHighestRole() == "Admin") {
                            HomeButton(text = "Phân quyền") {
                                userViewModel.fetchUsers()
                                navController.navigate(Screen.AccountRole.route)
                            }
                            HomeButton(text = "Đăng ký tài khoản") {
                                navController.navigate(Screen.Register.route)
                            }
                            HomeButton(text = "Quản lý tài chính") {
                                financeViewModel.fetchFinances()
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(navController: NavController, authViewModel: AuthViewModel) {
    TopAppBar(
        title = { Text("Trang chủ", style = MaterialTheme.typography.titleLarge) },
        actions = {
            IconButton(onClick = {
                authViewModel.logout()
                navController.navigate(Screen.Login.route)
            }) {
                Icon(Icons.Default.Logout, contentDescription = "Chỉnh sửa hồ sơ")
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
