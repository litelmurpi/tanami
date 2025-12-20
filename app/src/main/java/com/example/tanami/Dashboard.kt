package com.example.tanami

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.progressindicator.CircularProgressIndicator
import java.text.SimpleDateFormat
import java.util.*
import kotlin.jvm.java
import kotlin.text.format

class Dashboard : AppCompatActivity() {

    // Views
    private lateinit var imgAvatar: ImageView
    private lateinit var cardDeviceSelector: MaterialCardView
    private lateinit var textDeviceName: TextView
    private lateinit var textDeviceId: TextView
    private lateinit var btnAddDevice: ImageView

    // Monitoring Components
    private lateinit var progressKelembaban: CircularProgressIndicator
    private lateinit var textKelembaban: TextView
    private lateinit var progressPH: CircularProgressIndicator
    private lateinit var textPH: TextView
    private lateinit var progressTemp: CircularProgressIndicator
    private lateinit var textTemp: TextView
    private lateinit var switchWatering: SwitchCompat

    private lateinit var textLastUpdate: TextView
    private lateinit var btnPanduan: MaterialButton
    private lateinit var btnTanamCare: MaterialButton

    // Data
    private var currentHumidity = 75
    private var currentPH = 6.8
    private var currentTemp = 22.3
    private var isWateringOn = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dashboard)

        initViews()
        setupListeners()
        loadDashboardData()
        updateLastUpdate()
    }

    private fun initViews() {
        // Header
        imgAvatar = findViewById(R.id.imgAvatar)

        // com.example.tanami.Device Selector
        cardDeviceSelector = findViewById(R.id.cardDeviceSelector)
        textDeviceName = findViewById(R.id.textDeviceName)
        textDeviceId = findViewById(R.id.textDeviceId)
        btnAddDevice = findViewById(R.id.btnAddDevice)

        // Monitoring
        progressKelembaban = findViewById(R.id.progressKelembaban)
        textKelembaban = findViewById(R.id.textKelembaban)
        progressPH = findViewById(R.id.progressPH)
        textPH = findViewById(R.id.textPH)
        progressTemp = findViewById(R.id.progressTemp)
        textTemp = findViewById(R.id.textTemp)
        switchWatering = findViewById(R.id.switchWatering)

        // Bottom
        textLastUpdate = findViewById(R.id.textLastUpdate)
        btnPanduan = findViewById(R.id.btnPanduan)
        btnTanamCare = findViewById(R.id.btnTanamCare)
    }

    private fun setupListeners() {
        // Avatar click
        imgAvatar.setOnClickListener {
            Toast.makeText(this, "Profile clicked", Toast.LENGTH_SHORT).show()
            // TODO: Navigate to Profile
        }

        // com.example.tanami.Device selector
        cardDeviceSelector.setOnClickListener {
            showDeviceSelector()
        }

        // Add device
        btnAddDevice.setOnClickListener {
            Toast.makeText(this, "Add new device", Toast.LENGTH_SHORT).show()
            // TODO: Navigate to Add com.example.tanami.Device
        }

        // Watering switch
        switchWatering.setOnCheckedChangeListener { _, isChecked ->
            isWateringOn = isChecked
            handleWatering(isChecked)
        }

        // Panduan button
        btnPanduan.setOnClickListener {
            val intent = Intent(this, ListPanduan::class.java)
            startActivity(intent)
        }

        // TanamCare button
        btnTanamCare.setOnClickListener {
            Toast.makeText(this, "TanamCare - Coming Soon!", Toast.LENGTH_SHORT).show()
            // TODO: Navigate to TanamCare (AI Assistant)
        }
    }

    private fun loadDashboardData() {
        // Simulate loading data from API or IoT device
        // TODO: Replace with actual API call

        // Update UI
        updateMonitoringData(
            humidity = currentHumidity,
            ph = currentPH,
            temperature = currentTemp
        )
    }

    private fun updateMonitoringData(humidity: Int, ph: Double, temperature: Double) {
        // Update Humidity
        currentHumidity = humidity
        progressKelembaban.progress = humidity
        textKelembaban.text = "$humidity%"

        // Update pH (convert to percentage: pH 0-14 -> 0-100%)
        currentPH = ph
        val phPercentage = ((ph / 14.0) * 100).toInt()
        progressPH.progress = phPercentage
        textPH.text = String.format("%.1f", ph)

        // Update Temperature (assume max 50°C -> 100%)
        currentTemp = temperature
        val tempPercentage = ((temperature / 50.0) * 100).toInt()
        progressTemp.progress = tempPercentage
        textTemp.text = String.format("%.1f", temperature)

        // Update last update time
        updateLastUpdate()
    }

    private fun updateLastUpdate() {
        val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale("id", "ID"))
        val currentTime = sdf.format(Date())
        textLastUpdate.text = "terakhir diperbarui: $currentTime WIB"
    }

    private fun showDeviceSelector() {
        // TODO: Show dialog with device list
        val devices = listOf(
            Device("Selada Hidroponik", "#A005"),
            Device("Tomat Kebun", "#A006"),
            Device("Cabai Rawit", "#A007")
        )

        // For now, just show toast
        Toast.makeText(this, "com.example.tanami.Device selector - Coming Soon!", Toast.LENGTH_SHORT).show()

        // TODO: Implement bottom sheet or dialog
        // val dialog = DeviceSelectorDialog(devices) { device ->
        //     textDeviceName.text = device.name
        //     textDeviceId.text = device.id
        // }
        // dialog.show(supportFragmentManager, "DeviceSelector")
    }

    private fun handleWatering(isOn: Boolean) {
        val status = if (isOn) "ON" else "OFF"
        Toast.makeText(this, "Penyiraman: $status", Toast.LENGTH_SHORT).show()

        // TODO: Send command to IoT device
        // sendWateringCommand(isOn)
    }

    // Auto refresh data every 5 seconds (optional)
    private fun startAutoRefresh() {
        // TODO: Implement auto refresh using Handler or WorkManager
    }

    override fun onResume() {
        super.onResume()
        // Refresh data when returning to screen
        loadDashboardData()
    }
}

// Data class for com.example.tanami.Device
data class Device(
    val name: String,
    val id: String
)
