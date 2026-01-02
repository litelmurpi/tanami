package com.example.tanami.network

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.util.Log
import java.net.InetAddress

class TanamiDeviceFinder(context: Context, private val onDeviceFound: (String, String) -> Unit) {

    private val nsdManager = context.getSystemService(Context.NSD_SERVICE) as NsdManager
    // Service Type standar HTTP. Sesuaikan jika ESP32 menggunakan protokol lain.
    private val SERVICE_TYPE = "_http._tcp."
    // Filter: Hanya ambil device yang namanya mengandung kata "Tanami" atau kode project
    private val SERVICE_NAME_FILTER = "Tanami"

    private var discoveryListener: NsdManager.DiscoveryListener? = null

    fun startDiscovery() {
        if (discoveryListener != null) return // Jangan start kalau sudah jalan

        discoveryListener = object : NsdManager.DiscoveryListener {
            override fun onDiscoveryStarted(regType: String) {
                Log.d("TanamiNSD", "Pencarian dimulai...")
            }

            override fun onServiceFound(service: NsdServiceInfo) {
                Log.d("TanamiNSD", "Layanan ditemukan: ${service.serviceName}")
                // Cek apakah nama servicenya sesuai target kita
                if (service.serviceType.contains("http") || service.serviceName.contains(SERVICE_NAME_FILTER)) {
                    nsdManager.resolveService(service, createResolveListener())
                }
            }

            override fun onServiceLost(service: NsdServiceInfo) {
                Log.e("TanamiNSD", "Layanan hilang: ${service.serviceName}")
            }

            override fun onDiscoveryStopped(serviceType: String) {
                Log.i("TanamiNSD", "Pencarian berhenti")
            }

            override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
                Log.e("TanamiNSD", "Gagal start: $errorCode")
                stopDiscovery()
            }

            override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {
                Log.e("TanamiNSD", "Gagal stop: $errorCode")
            }
        }

        try {
            nsdManager.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, discoveryListener)
        } catch (e: Exception) {
            Log.e("TanamiNSD", "Error in startDiscovery: ${e.message}")
        }
    }

    fun stopDiscovery() {
        discoveryListener?.let {
            try {
                nsdManager.stopServiceDiscovery(it)
            } catch (e: Exception) {
                // Ignore if already stopped
            }
            discoveryListener = null
        }
    }

    private fun createResolveListener(): NsdManager.ResolveListener {
        return object : NsdManager.ResolveListener {
            override fun onResolveFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
                Log.e("TanamiNSD", "Gagal resolve: $errorCode")
            }

            override fun onServiceResolved(serviceInfo: NsdServiceInfo) {
                val host: InetAddress = serviceInfo.host
                val ipAddress = host.hostAddress
                val name = serviceInfo.serviceName

                Log.d("TanamiNSD", "Resolved: $name at $ipAddress")
                if (ipAddress != null) {
                    onDeviceFound(name, ipAddress)
                }
            }
        }
    }
}