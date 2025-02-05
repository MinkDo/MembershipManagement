package com.example.membershipmanagement.data.repository


data class Transaction(val description: String, val amount: Double, val type: String) // "income" hoặc "expense"
