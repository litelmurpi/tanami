package com.example.tanami.network

import com.example.tanami.models.TanamanDetailResponse
import com.example.tanami.models.TanamanResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

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
}
