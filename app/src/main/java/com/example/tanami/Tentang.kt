package com.example.tanami

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar

class Tentang : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Pastikan nama file XML kamu benar (misal: tentang.xml)
        setContentView(R.layout.tentang)

        // --- LOGIKA TOMBOL KEMBALI ---
        // Sesuai ID di XML kamu: android:id="@+id/toolbar5"
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar5)

        toolbar.setNavigationOnClickListener {
            finish() // Kembali ke halaman Profile
        }
    }
}