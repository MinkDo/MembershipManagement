package com.example.membershipmanagement.events.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.HowToReg
import androidx.compose.material.icons.filled.PersonRemove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.membershipmanagement.data.repository.Event
import com.example.membershipmanagement.viewmodel.EventViewModel

@Composable
fun EventItem(
    event: Event,
    isAdmin: Boolean,
    eventViewModel: EventViewModel,
    onClick: () -> Unit,
    onRegister: () -> Unit,
    onUnregister: () -> Unit,
    onDelete: (() -> Unit)? = null,
    onApprove: (() -> Unit)? = null
) {
    var message by remember { mutableStateOf("") } // ✅ Trạng thái cục bộ chỉ cho sự kiện này

    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            // 🏆 Tiêu đề sự kiện
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Event,
                    contentDescription = "Sự kiện",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = event.name,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 📌 Chi tiết sự kiện
            EventDetailRow(label = "📍 Địa điểm", value = event.location ?: "Chưa có")
            EventDetailRow(label = "📅 Thời gian", value = "${event.startDate} - ${event.endDate ?: "?"}")
            EventDetailRow(label = "📌 Trạng thái", value = event.status.joinToString(", "))
            EventDetailRow(label = "💰 Phí tham gia", value = "${event.fee} VNĐ")
            EventDetailRow(label = "📝 Mô tả", value = event.description ?: "Không có mô tả")

            Spacer(modifier = Modifier.height(12.dp))

            // 🎯 Nút chức năng
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ActionButton(
                    text = "Đăng ký",
                    icon = Icons.Default.HowToReg,
                    color = MaterialTheme.colorScheme.primary,
                    onClick = { onRegister() }
                )
                Spacer(modifier = Modifier.weight(1f))

                ActionButton(
                    text = "Hủy đăng ký",
                    icon = Icons.Default.PersonRemove,
                    color = MaterialTheme.colorScheme.error,
                    onClick = { onUnregister() }
                )


            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (isAdmin) {
                    ActionButton(
                        text = "Duyệt",
                        icon = Icons.Default.Done,
                        color = MaterialTheme.colorScheme.tertiary,
                        onClick = { onApprove?.invoke() }
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    ActionButton(
                        text = "Xóa",
                        icon = Icons.Default.Delete,
                        color = MaterialTheme.colorScheme.error,
                        onClick = { onDelete?.invoke() }
                    )
                }


            }

            // 🔔 Hiển thị thông báo chỉ cho sự kiện này
            if (message.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

// 🔹 Hàng thông tin sự kiện
@Composable
fun EventDetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),

    ) {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f) // Đẩy phần còn lại về bên phải
        )
        Text(
            text = value,
            color = Color.Gray,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f) // Giúp `value` căn về phải
        )
    }
}

// 🔹 Nút chức năng với biểu tượng
@Composable
fun ActionButton(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = color),

    ) {
        Icon(imageVector = icon, contentDescription = text, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text)
    }
}
