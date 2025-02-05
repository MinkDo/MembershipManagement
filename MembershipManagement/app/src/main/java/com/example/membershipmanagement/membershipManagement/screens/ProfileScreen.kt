package com.example.membershipmanagement.membershipManagement.screens



import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.membershipmanagement.R
import com.example.membershipmanagement.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    navController: NavController,
    profileViewModel: ProfileViewModel = viewModel()
) {
    val userProfile by profileViewModel.userProfile.collectAsState()

    Scaffold(
        topBar = { ProfileTopBar(navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Ảnh đại diện
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .clickable { /* Mở màn hình chỉnh sửa avatar */ },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.avatar),
                    contentDescription = "Ảnh đại diện",
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Thông tin cá nhân
            Text(text = userProfile.fullName, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text(text = userProfile.email, fontSize = 16.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(20.dp))

            ProfileInfoItem(label = "Ngày sinh", value = userProfile.birthDate)
            ProfileInfoItem(label = "Giới tính", value = userProfile.gender)
            ProfileInfoItem(label = "Cấp đai", value = userProfile.beltLevel)
            ProfileInfoItem(
                label = "Trạng thái",
                value = if (userProfile.isActive) "Đang hoạt động" else "Ngừng tham gia",
                valueColor = if (userProfile.isActive) Color.Green else Color.Red
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { navController.navigate("edit_profile") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Chỉnh sửa hồ sơ")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileTopBar(navController: NavController) {
    TopAppBar(
        title = { Text("Hồ sơ cá nhân", style = MaterialTheme.typography.titleLarge) },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.Default.Edit, contentDescription = "Quay lại")
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
        Text(text = value, fontSize = 16.sp, color = valueColor)
    }
}
