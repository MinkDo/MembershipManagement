package com.example.membershipmanagement.finances.components

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
import com.example.membershipmanagement.data.repository.Finance

@Composable
fun FinanceItem(finance: Finance,
                onClick: ()-> Unit,
                onDelete: ()->Unit) {
    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // 📝 Thông tin giao dịch
            Text(text = finance.category, style = MaterialTheme.typography.titleLarge)
            Text(text = "Số tiền: ${finance.amount} VNĐ", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Ngày giao dịch: ${finance.transactionDate}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Loại: ${if (finance.type == 0) "Thu" else "Chi"}", style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(8.dp))

            // 📌 Hiển thị mô tả nếu có
            finance.description?.let {
                Text(text = "Mô tả: $it", style = MaterialTheme.typography.bodySmall)
            }
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
