package com.example.membershipmanagement.membershipManagement.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.membershipmanagement.data.model.Member

@Composable
fun MemberItem(
    member: Member,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggleStatus: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Tên: ${member.fullName}")
            Text("Giới tính: ${member.gender}")
            Text("Cấp đai: ${member.beltLevel}")
            Text("Trạng thái: ${if (member.isActive) "Đang hoạt động" else "Ngừng tham gia"}")

            Row {
                Button(onClick = onEdit) {
                    Text("Chỉnh sửa")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = onToggleStatus) {
                    Text(if (member.isActive) "Ngừng tham gia" else "Kích hoạt lại")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = onDelete, colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error)) {
                    Text("Xóa")
                }
            }
        }
    }
}


