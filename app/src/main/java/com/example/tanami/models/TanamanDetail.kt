package com.example.tanami.models

import com.google.gson.annotations.SerializedName

data class BibitMedia(
    @SerializedName("id")
    val id: Int,

    @SerializedName("tanaman_id")
    val tanamanId: Int,

    @SerializedName("jenis_bibit")
    val jenisBibit: String?,

    @SerializedName("sumber_bibit")
    val sumberBibit: String?,

    @SerializedName("jenis_media")
    val jenisMedia: String?,

    @SerializedName("rasio_media")
    val rasioMedia: String?,

    @SerializedName("drainase")
    val drainase: String?,

    @SerializedName("ukuran_pot")
    val ukuranPot: String?
)

data class Penyiraman(
    @SerializedName("id")
    val id: Int,

    @SerializedName("tanaman_id")
    val tanamanId: Int,

    @SerializedName("frekuensi")
    val frekuensi: String?,

    @SerializedName("waktu_penyiraman")
    val waktuPenyiraman: String?,

    @SerializedName("cara_penyiraman")
    val caraPenyiraman: String?,

    @SerializedName("kondisi_khusus")
    val kondisiKhusus: String?
)

data class Pemupukan(
    @SerializedName("id")
    val id: Int,

    @SerializedName("tanaman_id")
    val tanamanId: Int,

    @SerializedName("jenis_pupuk")
    val jenisPupuk: String?,

    @SerializedName("dosis")
    val dosis: String?,

    @SerializedName("frekuensi")
    val frekuensi: String?,

    @SerializedName("cara_aplikasi")
    val caraAplikasi: String?,

    @SerializedName("catatan")
    val catatan: String?
)

data class Perawatan(
    @SerializedName("id")
    val id: Int,

    @SerializedName("tanaman_id")
    val tanamanId: Int,

    @SerializedName("jenis_perawatan")
    val jenisPerawatan: String?,

    @SerializedName("frekuensi")
    val frekuensi: String?,

    @SerializedName("cara_perawatan")
    val caraPerawatan: String?,

    @SerializedName("waktu_pelaksanaan")
    val waktuPelaksanaan: String?,

    @SerializedName("peralatan")
    val peralatan: String?
)

data class MasaPanen(
    @SerializedName("id")
    val id: Int,

    @SerializedName("tanaman_id")
    val tanamanId: Int,

    @SerializedName("durasi_tanam")
    val durasiTanam: String?,

    @SerializedName("ciri_siap_panen")
    val ciriSiapPanen: String?,

    @SerializedName("cara_panen")
    val caraPanen: String?,

    @SerializedName("frekuensi_panen")
    val frekuensiPanen: String?,

    @SerializedName("hasil_panen")
    val hasilPanen: String?
)

data class DetailData(
    @SerializedName("tanaman")
    val tanaman: Tanaman,

    @SerializedName("bibit_media")
    val bibitMedia: BibitMedia?,

    @SerializedName("penyiraman")
    val penyiraman: Penyiraman?,

    @SerializedName("pemupukan")
    val pemupukan: Pemupukan?,

    @SerializedName("perawatan")
    val perawatan: List<Perawatan>?,

    @SerializedName("masa_panen")
    val masaPanen: MasaPanen?
)
