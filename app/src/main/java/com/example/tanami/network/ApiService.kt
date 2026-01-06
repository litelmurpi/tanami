package com.example.tanami.network

import com.example.tanami.models.*
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ========== TANAMAN ENDPOINTS (EXISTING) ==========
    @GET("get_all_tanaman.php")
    fun getAllTanaman(): Call<TanamanResponse>

    @GET("get_tanaman_by_kategori.php")
    fun getTanamanByKategori(
        @Query("kategori_id") kategoriId: Int
    ): Call<TanamanResponse>

    @GET("search_tanaman.php")
    fun searchTanaman(
        @Query("query") query: String
    ): Call<TanamanResponse>

    @GET("get_detail_tanaman.php")
    fun getDetailTanaman(
        @Query("id") id: Int
    ): Call<TanamanDetailResponse>

    // ========== AUTH ENDPOINTS (NEW) ==========

    @POST("register.php")
    suspend fun signup(@Body request: SignupRequest): Response<SignupResponse>

    @POST("login.php")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("forgot_password.php")
    suspend fun forgotPassword(@Body email: Map<String, String>): Response<ForgotPasswordResponse>

    @POST("verify_code.php")
    suspend fun verifyCode(@Body data: VerifyCodeRequest): Response<VerifyCodeResponse>

    @POST("reset_password.php")
    suspend fun resetPassword(@Body data: ResetPasswordRequest): Response<ResetPasswordResponse>
}