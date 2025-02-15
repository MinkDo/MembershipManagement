package com.example.membershipmanagement.events.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.membershipmanagement.data.repository.Event
import com.example.membershipmanagement.viewmodel.EventViewModel

@Composable
fun EventItem(
    event: Event,
    isAdmin: Boolean,
    eventViewModel: EventViewModel,
    onRegister: () -> Unit,
    onUnregister: () -> Unit,
    onDelete: (() -> Unit)? = null,
    onApprove: (() -> Unit)? = null
) {
    var message by remember { mutableStateOf("") } // ✅ Trạng thái cục bộ chỉ cho sự kiện này
    val uiState by eventViewModel.uiState.collectAsState()

    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // 📝 Thông tin sự kiện
            Text(text = event.name, style = MaterialTheme.typography.titleLarge)
            Text(text = "Địa điểm: ${event.location ?: "Chưa có"}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Thời gian: ${event.startDate} - ${event.endDate ?: "?"}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Trạng thái: ${event.status.joinToString(", ")}", style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(8.dp))

            // 🎯 Nút chức năng
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                if (isAdmin) {
                    Button(onClick = {
                        onApprove?.invoke()

                    }) {
                        Text("Duyệt thành viên")
                    }

                    Button(
                        onClick = {
                            onDelete?.invoke()

                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Xóa")
                    }
                } else {
                    Button(onClick = {
                        onRegister()

                    }) {
                        Text("Đăng ký")
                    }

                    Button(
                        onClick = {
                            onUnregister()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Hủy đăng ký")
                    }
                }
            }

            // 🔔 Hiển thị thông báo chỉ cho sự kiện này
            if (message.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = message, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}
