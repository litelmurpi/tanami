package com.example.tanami

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.appbar.MaterialToolbar

class PrivasiAkses : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.privasiakses) // Pastikan nama file XML sesuai

        // 1. Siapkan SharedPreferences (Buku Catatan)
        val sharedPrefs = getSharedPreferences("SettingsPrivasi", Context.MODE_PRIVATE)
        val editor = sharedPrefs.edit()

        // 2. Kenalkan ke-4 Switch berdasarkan ID di XML
        val switchNama = findViewById<SwitchMaterial>(R.id.tampilkan_nama_profil)
        val switchRiwayat = findViewById<SwitchMaterial>(R.id.sembunyikan_riwayat_aktivitas)
        val switchSosmed = findViewById<SwitchMaterial>(R.id.scan_device_switch) // ID di XML kamu untuk "Bagikan ke Sosmed"
        val switchSensor = findViewById<SwitchMaterial>(R.id.tampilkan_sensor_aktif)

        // ====================================================================
        // SWITCH 1: TAMPILKAN NAMA PROFIL
        // ====================================================================
        val keyNama = "PREF_TAMPILKAN_NAMA"
        switchNama.isChecked = sharedPrefs.getBoolean(keyNama, false) // Load
        switchNama.setOnCheckedChangeListener { _, isChecked ->
            editor.putBoolean(keyNama, isChecked)
            editor.apply() // Save
        }

        // ====================================================================
        // SWITCH 2: SEMBUNYIKAN RIWAYAT AKTIVITAS
        // ====================================================================
        val keyRiwayat = "PREF_SEMBUNYIKAN_RIWAYAT"
        switchRiwayat.isChecked = sharedPrefs.getBoolean(keyRiwayat, false) // Load
        switchRiwayat.setOnCheckedChangeListener { _, isChecked ->
            editor.putBoolean(keyRiwayat, isChecked)
            editor.apply() // Save
        }

        // ====================================================================
        // SWITCH 3: BAGIKAN KE SOSIAL MEDIA
        // ====================================================================
        val keySosmed = "PREF_BAGIKAN_SOSMED"
        switchSosmed.isChecked = sharedPrefs.getBoolean(keySosmed, false) // Load
        switchSosmed.setOnCheckedChangeListener { _, isChecked ->
            editor.putBoolean(keySosmed, isChecked)
            editor.apply() // Save
        }

        // ====================================================================
        // SWITCH 4: TAMPILKAN SENSOR AKTIF
        // ====================================================================
        val keySensor = "PREF_TAMPILKAN_SENSOR"
        switchSensor.isChecked = sharedPrefs.getBoolean(keySensor, false) // Load
        switchSensor.setOnCheckedChangeListener { _, isChecked ->
            editor.putBoolean(keySensor, isChecked)
            editor.apply() // Save
        }

        // 3. Tombol Kembali (Toolbar)
        // Di XML kamu ID toolbar paling atas adalah 'toolbar3'
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar3)
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }
}