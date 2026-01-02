package com.example.tanami.utils

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

class TanamiPrefs(context: Context) {
    private val prefs = context.getSharedPreferences("tanami_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_CURRENT_NAME = "current_device_name"
        private const val KEY_CURRENT_IP = "current_device_ip"
        private const val KEY_DEVICE_LIST = "saved_device_list"
    }

    // --- BAGIAN 1: PERANGKAT AKTIF ---
    fun saveCurrentDevice(name: String, ip: String) {
        prefs.edit().apply {
            putString(KEY_CURRENT_NAME, name)
            putString(KEY_CURRENT_IP, ip)
            apply()
        }
        addDeviceToHistory(name, ip)
    }

    // Jika perangkat dihapus, kita reset current device jadi kosong
    fun clearCurrentDevice() {
        prefs.edit().apply {
            remove(KEY_CURRENT_NAME)
            remove(KEY_CURRENT_IP)
            apply()
        }
    }

    fun getDeviceIp(): String? = prefs.getString(KEY_CURRENT_IP, null)
    fun getDeviceName(): String? = prefs.getString(KEY_CURRENT_NAME, "Belum ada alat")

    // --- BAGIAN 2: HISTORY ---

    fun addDeviceToHistory(name: String, ip: String) {
        val currentList = getDeviceHistory()
        val index = currentList.indexOfFirst { it.second == ip }
        if (index != -1) {
            if (currentList[index].first != name) renameDevice(ip, name)
        } else {
            currentList.add(Pair(name, ip))
            saveHistoryList(currentList)
        }
    }

    fun renameDevice(targetIp: String, newName: String) {
        val list = getDeviceHistory()
        val updatedList = list.map {
            if (it.second == targetIp) Pair(newName, it.second) else it
        }
        saveHistoryList(updatedList)
        if (getDeviceIp() == targetIp) {
            prefs.edit().putString(KEY_CURRENT_NAME, newName).apply()
        }
    }

    // FUNGSI BARU: HAPUS DEVICE
    fun removeDevice(targetIp: String) {
        val list = getDeviceHistory()
        // Filter list, buang yang IP-nya sama dengan target
        val newList = list.filter { it.second != targetIp }
        saveHistoryList(newList)

        // Jika yang dihapus adalah perangkat yang sedang AKTIF dipakai, reset dashboard
        if (getDeviceIp() == targetIp) {
            clearCurrentDevice()
        }
    }

    fun getDeviceHistory(): MutableList<Pair<String, String>> {
        val jsonString = prefs.getString(KEY_DEVICE_LIST, "[]")
        val list = mutableListOf<Pair<String, String>>()
        try {
            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(Pair(obj.getString("name"), obj.getString("ip")))
            }
        } catch (e: Exception) { e.printStackTrace() }
        return list
    }

    private fun saveHistoryList(list: List<Pair<String, String>>) {
        val jsonArray = JSONArray()
        list.forEach {
            val obj = JSONObject()
            obj.put("name", it.first)
            obj.put("ip", it.second)
            jsonArray.put(obj)
        }
        prefs.edit().putString(KEY_DEVICE_LIST, jsonArray.toString()).apply()
    }
}