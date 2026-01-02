package com.example.tanami

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.recyclerview.widget.RecyclerView
import com.example.tanami.network.TanamiDeviceFinder
import com.example.tanami.utils.TanamiPrefs
import com.google.android.material.switchmaterial.SwitchMaterial

class TambahPerangkat : AppCompatActivity() {

    private lateinit var switchScan: SwitchMaterial
    private lateinit var rvScanResults: RecyclerView
    private lateinit var rvPairedDevices: RecyclerView
    private lateinit var tvEmptyPaired: TextView
    private lateinit var tvRefresh: TextView
    private lateinit var progressScan: ProgressBar
    private lateinit var btnBack: View

    private lateinit var scanAdapter: DeviceAdapter
    private val scannedDevicesList = mutableListOf<String>()

    private lateinit var deviceFinder: TanamiDeviceFinder
    private lateinit var prefs: TanamiPrefs

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tambahperangkat)

        prefs = TanamiPrefs(this)

        initViews()
        setupAdapters()
        setupListeners()
        loadPairedDevices()

        // Init NSD Finder (REAL SCANNING)
        deviceFinder = TanamiDeviceFinder(this) { deviceName, ipAddress ->
            runOnUiThread {
                val displayText = "$deviceName ($ipAddress)"
                if (!scannedDevicesList.contains(displayText)) {
                    scannedDevicesList.add(displayText)
                    scanAdapter.notifyDataSetChanged()
                    progressScan.visibility = View.GONE
                }
            }
        }
    }

    private fun initViews() {
        switchScan = findViewById(R.id.scan_device_switch)
        rvScanResults = findViewById(R.id.rv_scan_results)
        rvPairedDevices = findViewById(R.id.rv_paired_devices)
        tvEmptyPaired = findViewById(R.id.tv_empty_paired)
        tvRefresh = findViewById(R.id.tv_refresh)
        progressScan = findViewById(R.id.progress_scan)
        btnBack = findViewById(R.id.btn_back)
    }

    private fun setupAdapters() {
        scanAdapter = DeviceAdapter(scannedDevicesList) { selectedDevice ->
            saveAndConnect(selectedDevice)
        }
        rvScanResults.adapter = scanAdapter
    }

    private fun setupListeners() {
        btnBack.setOnClickListener { finish() }

        switchScan.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) startScanning() else stopScanning()
        }

        tvRefresh.setOnClickListener {
            Toast.makeText(this, "Menyegarkan data...", Toast.LENGTH_SHORT).show()
            loadPairedDevices()
        }
    }

    private fun loadPairedDevices() {
        val historyList = prefs.getDeviceHistory()

        if (historyList.isNotEmpty()) {
            val displayList = historyList.map { "${it.first} (${it.second})" }
            val pairedAdapter = DeviceAdapter(displayList.toMutableList()) { deviceString ->
                saveAndConnect(deviceString)
            }
            rvPairedDevices.adapter = pairedAdapter
            rvPairedDevices.visibility = View.VISIBLE
            tvEmptyPaired.visibility = View.GONE
        } else {
            // Tampilkan Dummy jika kosong (biar konsisten dengan Dashboard)
            val dummyList = listOf(
                "Pakcoy Balkon (192.168.1.50)",
                "Kebun Atas (192.168.1.51)",
                "Kangkung Kolam (192.168.1.52)"
            )
            val pairedAdapter = DeviceAdapter(dummyList.toMutableList()) { deviceString ->
                saveAndConnect(deviceString)
            }
            rvPairedDevices.adapter = pairedAdapter
            rvPairedDevices.visibility = View.VISIBLE
            tvEmptyPaired.visibility = View.GONE
        }
    }

    private fun saveAndConnect(deviceString: String) {
        try {
            val parts = deviceString.split("(")
            val name = parts[0].trim()
            val ip = parts[1].replace(")", "").trim()

            prefs.saveCurrentDevice(name, ip)
            Toast.makeText(this, "Disimpan: $name", Toast.LENGTH_SHORT).show()

            loadPairedDevices()
            switchScan.isChecked = false
            stopScanning()

        } catch (e: Exception) {
            Toast.makeText(this, "Format data error", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startScanning() {
        scannedDevicesList.clear()
        scanAdapter.notifyDataSetChanged()
        progressScan.visibility = View.VISIBLE
        rvScanResults.visibility = View.VISIBLE

        deviceFinder.startDiscovery() // Scan Asli Jalan

        // SIMULASI DUMMY SCAN RESULT (Agar demo scanning terlihat berhasil)
        Handler(Looper.getMainLooper()).postDelayed({
            val dummies = listOf("Pakcoy Balkon (192.168.1.50)", "Kebun Atas (192.168.1.51)")
            dummies.forEach {
                if (!scannedDevicesList.contains(it)) scannedDevicesList.add(it)
            }
            scanAdapter.notifyDataSetChanged()
            progressScan.visibility = View.GONE
        }, 1500)
    }

    private fun stopScanning() {
        deviceFinder.stopDiscovery()
        progressScan.visibility = View.GONE
        rvScanResults.visibility = View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        deviceFinder.stopDiscovery()
    }
}