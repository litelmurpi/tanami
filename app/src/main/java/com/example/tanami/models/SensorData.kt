package com.example.tanami.models

import com.google.gson.annotations.SerializedName

data class SensorData(
    @SerializedName("device_id")
    val deviceId: String,

    @SerializedName("device_name")
    val deviceName: String,

    @SerializedName("humidity")
    val humidity: Int,

    @SerializedName("ph")
    val ph: Double,

    @SerializedName("temperature")
    val temperature: Double,

    @SerializedName("watering_status")
    val wateringStatus: Boolean,

    @SerializedName("last_update")
    val lastUpdate: String
)

data class SensorResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("data")
    val data: SensorData,

    @SerializedName("message")
    val message: String?
)

data class DeviceList(
    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("type")
    val type: String,

    @SerializedName("is_active")
    val isActive: Boolean
)

data class DeviceListResponse(
    @SerializedName("success")
    val success: Boolean,

    @SerializedName("data")
    val data: List<DeviceList>,

    @SerializedName("message")
    val message: String?
)
