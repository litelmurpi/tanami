package com.example.tanami.utils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.Serializable
import java.util.ArrayList

// 1. Model Data (Bentuk datanya)
data class LogModel(
    val judul: String,   // Contoh: "Penyiraman Otomatis"
    val jam: String,     // Contoh: "09:00"
    val tanggal: String, // Contoh: "13 Juli 2025"
    val tipe: String     // "MANUAL" atau "OTOMATIS"
) : Serializable

// 2. Log Manager (Pengelola Penyimpanan)
class LogManager(context: Context) {
    private val prefs = context.getSharedPreferences("TanamiLogDB", Context.MODE_PRIVATE)
    private val gson = Gson()

    // Ambil semua data
    fun getLogs(): ArrayList<LogModel> {
        val json = prefs.getString("DATA_LOG", null)
        val type = object : TypeToken<ArrayList<LogModel>>() {}.type
        return if (json != null) gson.fromJson(json, type) else ArrayList()
    }

    // Simpan data baru
    fun simpanLog(logBaru: LogModel) {
        val list = getLogs()
        list.add(0, logBaru) // Tambah di paling atas

        val jsonBaru = gson.toJson(list)
        prefs.edit().putString("DATA_LOG", jsonBaru).apply()
    }
}