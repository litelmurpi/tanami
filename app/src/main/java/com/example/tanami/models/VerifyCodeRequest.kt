package com.example.tanami.models

data class VerifyCodeRequest(
    val email: String,
    val code: String
)