package com.example.membershipmanagement.achievements.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.membershipmanagement.data.repository.Achievement
import com.example.membershipmanagement.viewmodel.EditEventViewModel

@Composable
fun AchievementItem(achievement: Achievement, onClick: ()->Unit, onDelete: ()->Unit) {
    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // 🏆 Thông tin thành tích
            Text(text = achievement.name, style = MaterialTheme.typography.titleLarge)
            Text(text = "Ngày đạt được: ${achievement.dateAchieved}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Mô tả: ${achievement.description ?: "Không có"}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Sự kiện: ${achievement.event.id}", style = MaterialTheme.typography.bodyMedium)
            // 🎯 Nút chức năng



        }
        Button(
            modifier = Modifier.align(alignment = Alignment.End),
            onClick = {
                onDelete()
            },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete",
                tint = MaterialTheme.colorScheme.onError
            )
        }
    }
}
