package com.example.membershipmanagement.data.remote

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://hdkhanh462-001-site1.ltempurl.com/api/" // URL API của bạn

    private val okHttpClient: OkHttpClient by lazy {
        val loggingInterceptor = HttpLoggingInterceptor { message ->
            Log.d("RetrofitLog", message) // Đặt tag tùy chỉnh
        }.apply {
            level = HttpLoggingInterceptor.Level.BODY // Ghi lại toàn bộ thông tin request/response
        }
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }
    // Lazy initialization của Retrofit instance
    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())  // Cần để chuyển đổi JSON
            .build()
            .create(ApiService::class.java)
    }
}
