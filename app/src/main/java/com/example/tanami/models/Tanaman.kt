package com.example.tanami.models

import com.google.gson.annotations.SerializedName

data class Tanaman(
    @SerializedName("id")
    val id: Int,

    @SerializedName("nama_umum")
    val namaUmum: String,

    @SerializedName("nama_latin")
    val namaLatin: String,

    @SerializedName("deskripsi")
    val deskripsi: String?,

    @SerializedName("gambar_url")
    val gambarUrl: String?,

    @SerializedName("kategori_id")
    val kategoriId: Int?,

    @SerializedName("nama_kategori")
    val namaKategori: String?
)
