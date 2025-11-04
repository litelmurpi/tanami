package com.example.tanami

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.switchmaterial.SwitchMaterial
import android.widget.Toast

class PrivasiAkses : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.privasiakses)

        val toolbar: MaterialToolbar = findViewById(R.id.toolbar3) // pastikan id toolbar di layout kamu benar
        toolbar.setNavigationOnClickListener {
            // Tutup activity ini, kembali ke Profile
            finish()
        }

        val switch_namaprofile = findViewById<SwitchMaterial>(R.id.switch_namaprofile)

        switch_namaprofile.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                Toast.makeText(this, "Nama Profil DITAMPILKAN", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Nama Profil DISEMBUNYIKAN", Toast.LENGTH_SHORT).show()
            }
        }
    }
}


