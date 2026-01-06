package com.example.tanami.models

import com.google.gson.annotations.SerializedName

data class ResetPasswordRequest(
    val email: String,
    val reset_token: String,
    val password: String,
    val password_confirmation: String
)

data class ResetPasswordResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String
)