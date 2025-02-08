package com.example.membershipmanagement.membershipManagement.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import coil3.compose.rememberAsyncImagePainter
import com.example.membershipmanagement.R
import com.example.membershipmanagement.data.repository.User

@Composable
fun MemberItem(
    member: User,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onclick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onclick() }
            .padding(vertical = 8.dp, horizontal = 16.dp), //  Tăng khoảng cách tạo sự thoáng đãng
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant) // 🌟 Màu nền nhẹ hơn
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically //  Căn giữa avatar và thông tin
        ) {
            // Avatar
            Image(
                painter = if (!member.avatarUrl.isNullOrEmpty())
                    rememberImagePainter(member.avatarUrl)
                else
                    painterResource(id = R.drawable.avatar),
                contentDescription = "Ảnh đại diện",
                modifier = Modifier
                    .size(64.dp) // 🌟 Định kích thước avatar chuẩn hơn
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(12.dp)) // 🌟 Tạo khoảng cách giữa avatar và thông tin

            // Thông tin hội viên
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = member.fullName,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Email: ${member.email}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Số điện thoại: ${member.phoneNumber ?: "Không có"}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        // Các nút chức năng
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = onEdit,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary) // 🌟 Màu đồng bộ theo theme
            ) {
                Text("Chỉnh sửa")
            }
            Button(
                onClick = onDelete,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Xóa")
            }
        }
    }
}

