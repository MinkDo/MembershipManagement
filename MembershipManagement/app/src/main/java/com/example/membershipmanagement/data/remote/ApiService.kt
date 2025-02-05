package com.example.membershipmanagement.data.remote

import com.example.membershipmanagement.data.repository.LoginRequest
import com.example.membershipmanagement.data.repository.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse


}