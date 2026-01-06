package com.example.tanami.models

import com.google.gson.annotations.SerializedName

data class VerifyCodeResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("message")
    val message: String,

    @SerializedName("reset_token")
    val resetToken: String?
)
