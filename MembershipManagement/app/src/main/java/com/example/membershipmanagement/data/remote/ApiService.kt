package com.example.membershipmanagement.data.remote

import com.example.membershipmanagement.data.repository.AccountResponse
import com.example.membershipmanagement.data.repository.LoginRequest
import com.example.membershipmanagement.data.repository.LoginResponse
import retrofit2.Response

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("account/profile")
    suspend fun getProfile(@Header("Authorization") token : String): Response<AccountResponse>

}