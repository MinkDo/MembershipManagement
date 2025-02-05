package com.example.membershipmanagement.data.model


data class Member(
    val id: Int,
    val fullName: String,
    val gender: String,
    val beltLevel: String,
    val isActive: Boolean
)
