package com.example.tanami.models

import com.google.gson.annotations.SerializedName

data class SignupResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("data")
    val data: UserData?
)

data class UserData(
    @SerializedName("id")
    val id: Int,

    @SerializedName("nama")
    val nama: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("token")
    val token: String? // Jika API return token saat signup
)