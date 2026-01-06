package com.example.tanami

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
// import com.example.tanami.utils.TanamiPrefs (Jika ingin menampilkan nama asli)

class Profile : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile) // Pastikan nama file XML kamu adalah 'profile.xml'

        // 1. Tombol Kembali (Toolbar Atas)
        // Karena di XML kamu belum ada ID untuk toolbar paling atas, kita cari toolbar pertama
        // Atau tambahkan android:id="@+id/toolbarTop" di XML MaterialToolbar yang pertama
            val toolbarTop = findViewById<MaterialToolbar>(R.id.toolbarTop) // Asumsi sudah diberi ID
        toolbarTop?.setNavigationOnClickListener {
            finish() // Kembali ke Dashboard
        }

        // 2. Tombol Log Aktivitas (Sesuai ID di XML kamu: @+id/aktivitas)
        val btnLogAktivitas = findViewById<MaterialToolbar>(R.id.aktivitas)
        btnLogAktivitas.setOnClickListener {
            startActivity(Intent(this, LogAktivitas::class.java))
        }

        // 3. Tombol Edit Akun (Sesuai ID di XML: @+id/editakun)
        findViewById<MaterialToolbar>(R.id.editakun).setOnClickListener {
            startActivity(Intent(this, EditAkun::class.java))
        }

        // 4. Tombol Privasi & Akses(Sesuai ID di XML: @+id/editakun)
        findViewById<MaterialToolbar>(R.id.privakses).setOnClickListener {
            startActivity(Intent(this, PrivasiAkses::class.java))
        }

        // 5. Tombol Privasi & Akses(Sesuai ID di XML: @+id/editakun)
        findViewById<MaterialToolbar>(R.id.pusatbantuan).setOnClickListener {
            startActivity(Intent(this, Bantuan::class.java))
        }

        // 6. Tombol Privasi & Akses(Sesuai ID di XML: @+id/editakun)
        findViewById<MaterialToolbar>(R.id.tentang).setOnClickListener {
            startActivity(Intent(this, Tentang::class.java))
        }
    }
}