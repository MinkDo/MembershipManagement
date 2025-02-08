package com.example.membershipmanagement.membershipManagement.screens



import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import coil3.compose.rememberAsyncImagePainter
import com.example.membershipmanagement.R
import com.example.membershipmanagement.navigation.Screen
import com.example.membershipmanagement.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    navController: NavController,
    profileViewModel: ProfileViewModel
) {
    val userProfile by profileViewModel.profileState.collectAsState()
    val avatarUrl = profileViewModel.getAvatarUrl()
    Scaffold(
        topBar = { ProfileTopBar(navController,profileViewModel) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                // Ảnh đại diện
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .clickable { /* Mở màn hình chỉnh sửa avatar */ },
                    contentAlignment = Alignment.Center
                ) {
                    Log.d("EditProfileScreen","Image: $avatarUrl")
                    Image(
                        painter = if (avatarUrl != "") rememberImagePainter(avatarUrl)
                        else painterResource(id = R.drawable.avatar),
                        contentDescription = "Ảnh đại diện",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Thông tin cá nhân
                userProfile.userData?.let {
                    Text(
                        text = it.fullName,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                userProfile.userData?.let {
                    Text(
                        text = it.email,
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))



                ProfileInfoItem(
                    label = "Giới tính",
                    value = userProfile.userData?.profile?.gender.toString()
                )
                ProfileInfoItem(
                    label = "Địa chỉ",
                    value = userProfile.userData?.profile?.address.toString()
                )
                ProfileInfoItem(
                    label = "Cấp đai",
                    value = userProfile.userData?.profile?.currentRank.toString()
                )
                ProfileInfoItem(
                    label = "Ngày sinh",
                    value = userProfile.userData?.profile?.dateOfBirth.toString()
                )
                ProfileInfoItem(
                    label = "Ngày tham gia",
                    value = userProfile.userData?.profile?.joinDate.toString()
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        navController.navigate(Screen.EditProfile.route)
                        userProfile.userData?.let { profileViewModel.getUserById(it.id) }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Chỉnh sửa hồ sơ")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileTopBar(navController: NavController, profileViewModel: ProfileViewModel) {
    TopAppBar(
        title = { Text("Hồ sơ cá nhân", style = MaterialTheme.typography.titleLarge) },
        navigationIcon = {
            IconButton(onClick = {
                profileViewModel.getProfile()
                navController.popBackStack() }) {
                Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Quay lại")
            }
        }
    )
}

@Composable
fun ProfileInfoItem(label: String, value: String, valueColor: Color = Color.Black) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        Text(text = if(value == "null")"" else value, fontSize = 16.sp, color = valueColor)
    }
}


