package com.example.tanami.utils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class HistoryItem(
    val title: String,
    val date: String,
    val explanation: String,
    val solution: String
)

class HistoryManager(context: Context) {
    private val prefs = context.getSharedPreferences("tanami_history", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveHistory(title: String, explanation: String, solution: String) {
        val list = getHistory().toMutableList()
        val date = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID")).format(Date())

        // Memasukkan 4 data lengkap ke dalam record
        list.add(0, HistoryItem(title, date, explanation, solution))
        saveList(list)
    }

    fun getHistory(): List<HistoryItem> {
        val json = prefs.getString("data", null) ?: return emptyList()
        val type = object : TypeToken<List<HistoryItem>>() {}.type
        return gson.fromJson(json, type)
    }

    private fun saveList(list: List<HistoryItem>) {
        val json = gson.toJson(list)
        prefs.edit().putString("data", json).apply()
    }

    fun deleteHistory(position: Int) {
        val list = getHistory().toMutableList()
        if (position in list.indices) {
            list.removeAt(position)
            saveList(list)
        }
    }
}