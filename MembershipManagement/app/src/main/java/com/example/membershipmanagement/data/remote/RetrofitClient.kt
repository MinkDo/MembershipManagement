package com.example.membershipmanagement.data.remote




import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://hdkhanh462-001-site1.ltempurl.com/api/" // Thay thế với URL thật của bạn



    val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())  // Gson converter factory
        .build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)

}
