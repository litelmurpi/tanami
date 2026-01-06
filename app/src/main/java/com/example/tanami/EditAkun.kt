package com.example.tanami

import android.os.Bundle
import android.view.View // Tambahkan import ini
import android.widget.ImageView // Tambahkan jika tombolmu berupa ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.android.material.appbar.MaterialToolbar // Tambahkan jika pakai Toolbar

class EditAkun : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.editakun)

        // =========================================================
        // LOGIKA TOMBOL KEMBALI (BACK)
        // =========================================================

        // OPSI 1: Jika kamu pakai IMAGEVIEW (Seperti di LogAktivitas)
        // Cek file editakun.xml, cari id tombol back (misal: @+id/btnBack atau @+id/iconBack)
        val toolbarTop = findViewById<MaterialToolbar>(R.id.toolbar) // Asumsi sudah diberi ID
        toolbarTop?.setNavigationOnClickListener {
            finish() // Kembali ke Dashboard
        }

        /* // OPSI 2: Jika kamu pakai MATERIAL TOOLBAR (Seperti di Profile)
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbarEditAkun) // <--- Ganti ID-nya
        toolbar.setNavigationOnClickListener {
            finish()
        }
        */
    }
}