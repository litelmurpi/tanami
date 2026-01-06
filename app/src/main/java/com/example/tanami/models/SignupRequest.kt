package com.example.tanami.models

data class SignupRequest(
    val nama: String,
    val email: String,
    val password: String,
    val password_confirmation: String
)