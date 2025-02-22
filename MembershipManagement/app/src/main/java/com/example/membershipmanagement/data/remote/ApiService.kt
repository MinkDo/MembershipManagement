package com.example.membershipmanagement.data.remote

import com.example.membershipmanagement.data.repository.AccountResponse
import com.example.membershipmanagement.data.repository.Achievement
import com.example.membershipmanagement.data.repository.AchievementRequest
import com.example.membershipmanagement.data.repository.AchievementResponse
import com.example.membershipmanagement.data.repository.ChangePasswordRequest
import com.example.membershipmanagement.data.repository.CreateAchievementRequest
import com.example.membershipmanagement.data.repository.EditAchievementResponse
import com.example.membershipmanagement.data.repository.EditEventRequest
import com.example.membershipmanagement.data.repository.EditEventResponse
import com.example.membershipmanagement.data.repository.EditFinanceRequest
import com.example.membershipmanagement.data.repository.EditFinanceResponse
import com.example.membershipmanagement.data.repository.Event
import com.example.membershipmanagement.data.repository.EventRequest
import com.example.membershipmanagement.data.repository.EventResponse
import com.example.membershipmanagement.data.repository.FinanceRequest
import com.example.membershipmanagement.data.repository.FinanceResponse
import com.example.membershipmanagement.data.repository.LoginRequest
import com.example.membershipmanagement.data.repository.LoginResponse
import com.example.membershipmanagement.data.repository.RegistrationResponse
import com.example.membershipmanagement.data.repository.ReportData
import com.example.membershipmanagement.data.repository.ReportResponse

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
    @POST("events/{eventId}/register")
    suspend fun registerForEvent(
        @Header("Authorization") token: String,
        @Path("eventId") eventId: Int
    ): Response<EventResponse>
    @POST("events")
    suspend fun createEvent(
        @Header("Authorization") token: String,
        @Body request: EventRequest
    ): Response<EventResponse>
    @POST("achievements")
    suspend fun createAchievement(
        @Header("Authorization") token: String,
        @Body request: CreateAchievementRequest
    ): Response<AchievementResponse>
    @POST("finances")
    suspend fun createFinance(
        @Header("Authorization") token: String,
        @Body request: FinanceRequest
    ): Response<Unit>  // API không trả về data nên dùng `Unit`

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
    @GET("events")
    suspend fun getEvents(
        @Header("Authorization") token : String,
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 10
    ): Response<EventResponse>
    @GET("events/filter")
    suspend fun getFilteredEvents(
        @Header("Authorization") token : String,
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 10,
        @Query("name") name: String? = null,
        @Query("status") status: Int? = null
    ): Response<EventResponse>
    @GET("events/{id}")
    suspend fun getEventById(@Header("Authorization") token : String, @Path("id") id: String): Response<EditEventResponse>
    @GET("finances")
    suspend fun getFinances(
        @Header("Authorization") token : String,
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 10
    ): Response<FinanceResponse>
    @GET("finances/filter")
    suspend fun getFilteredFinances(
        @Header("Authorization") token : String,
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("type") type: Int?,
        @Query("start") start: String?,
        @Query("end") end: String?
    ): Response<FinanceResponse>
    @GET("finances/{id}")
    suspend fun getFinanceById(@Header("Authorization") token : String, @Path("id") id: String): Response<EditFinanceResponse>
    @GET("achievements")
    suspend fun getAchievements(
        @Header("Authorization") token : String,
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 10
    ): Response<AchievementResponse>
    @GET("achievements/{id}")
    suspend fun getAchievementById(@Header("Authorization") token : String, @Path("id") id: String): Response<EditAchievementResponse>
    @GET("achievements/filter")
    suspend fun getFilteredAchievements(
        @Header("Authorization") token: String,
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 10,
        @Query("name") name: String? = null,
        @Query("userId") userId: String? = null,
        @Query("eventId") eventId: Int? = null,
        @Query("start") startDate: String? = null,
        @Query("end") endDate: String? = null
    ): Response<AchievementResponse>
    @GET("events/{eventId}/registerations")
    suspend fun getEventRegistrations(
        @Header("Authorization") token: String,
        @Path("eventId") eventId: Int
    ): Response<RegistrationResponse>
    @GET("finances/report-year")
    suspend fun getFinanceReport(
        @Header("Authorization") token: String
    ): Response<ReportData>

    @GET("finances/report")
    suspend fun getFinanceReport(
        @Header("Authorization") token: String,
        @Query("start") startDate: String?,
        @Query("end") endDate: String?
    ): Response<ReportResponse>

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
    @PUT("users/{id}/set-password")
    suspend fun changePassword(
        @Header("Authorization") token : String,
        @Path("id") id: String,
        @Body request: ChangePasswordRequest
    ) : Response<Unit>
    @PUT("events/{id}")
    suspend fun updateEvent(
        @Header("Authorization") token: String,
        @Path("id") eventId: Int,
        @Body event: EditEventRequest
    ): Response<EditEventResponse>
    @PUT("achievements/{id}")
    suspend fun updateAchievement(
        @Header("Authorization") token: String,
        @Path("id") achievementId: Int,
        @Body achievement: AchievementRequest
    ): Response<EditAchievementResponse>
    @PUT("finances/{id}")
    suspend fun updateFinance(
        @Header("Authorization") token: String,
        @Path("id") achievementId: Int,
        @Body financeRequest: EditFinanceRequest
    ): Response<EditFinanceResponse>



    @DELETE("events/{id}")
    suspend fun deleteEvent(
        @Header("Authorization") token: String,
        @Path("id") eventId: Int
    ): Response<EventResponse>
    @DELETE("achievements/{id}")
    suspend fun deleteAchievement(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<AchievementResponse>
    @DELETE("finances/{id}")
    suspend fun deleteFinance(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<FinanceResponse>
    @DELETE("events/{eventId}/unregister")
    suspend fun unregisterFromEvent(
        @Header("Authorization") token: String,
        @Path("eventId") eventId: Int
    ): Response<EventResponse>
    @Headers("Accept: */*")
    @DELETE("users/{id}")
    suspend fun deleteUser(@Header("Authorization") token : String, @Path("id") userId: String): Response<Unit>

}