package com.example.membershipmanagement.data.remote

import com.example.membershipmanagement.data.repository.AccountResponse
import com.example.membershipmanagement.data.repository.LoginRequest
import com.example.membershipmanagement.data.repository.LoginResponse
import com.example.membershipmanagement.data.repository.UserApiResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
    @Multipart
    @POST("auth/register")
    suspend fun registerUser(
        @Header("Authorization") token : String,
        @Part("Roles") roles: RequestBody,
        @Part avatar: MultipartBody.Part?,
        @Part("FullName") fullName: RequestBody,
        @Part("Email") email: RequestBody,
        @Part("PhoneNumber") phoneNumber: RequestBody,
        @Part("Password") password: RequestBody,
        @Part("ConfirmPassword") confirmPassword: RequestBody
    ): Response<AccountResponse>

    @GET("account/profile")
    suspend fun getProfile(@Header("Authorization") token : String): Response<AccountResponse>
    @GET("users/{id}")
    suspend fun getUserById(@Header("Authorization") token : String, @Path("id") id: String): Response<AccountResponse>
    @GET("users")
    suspend fun getUsers(
        @Header("Authorization") token : String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<UserApiResponse>
    @GET("users/filter")
    suspend fun filterUsers(
        @Header("Authorization") token : String,
        @Query("search") search: List<String>?, // 🔹 Cho phép truyền nhiều giá trị
        @Query("order") order: List<String>?,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<UserApiResponse>


    @Multipart
    @PUT("users/{id}/profile")
    suspend fun updateProfile(
        @Header("Authorization") token : String,
        @Path("id") id: String,
        @Part("PhoneNumber") phoneNumber: RequestBody,
        @Part("FullName") fullName: RequestBody,
        @Part avatar: MultipartBody.Part?,  // Ảnh đại diện
        @Part("AvatarUrl") avatarUrl: RequestBody,
        @Part("Gender") gender: RequestBody,
        @Part("DateOfBirth") dateOfBirth: RequestBody,
        @Part("Address") address: RequestBody,
        @Part("CurrentRank") currentRank: RequestBody,
        @Part("JoinDate") joinDate: RequestBody
    ): Response<AccountResponse>


    @Headers("Accept: */*")
    @DELETE("users/{id}")
    suspend fun deleteUser(@Header("Authorization") token : String, @Path("id") userId: String): Response<Unit>

}