package com.example.tanami.models

import com.google.gson.annotations.SerializedName

data class TanamanResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("data")
    val data: List<Tanaman>,

    @SerializedName("message")
    val message: String?
)

data class TanamanDetailResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("data")
    val data: DetailData,

    @SerializedName("message")
    val message: String?
)
