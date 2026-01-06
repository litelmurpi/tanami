package com.example.tanami

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar

class Bantuan : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bantuan) // Sesuaikan nama file XML kamu

        // 1. Tombol Kembali
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar4)
        toolbar.setNavigationOnClickListener { finish() }

        // 2. Hubungi Email
        findViewById<TextView>(R.id.btnEmail).setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:tanami.support@gmail.com") // Ganti email tujuan
                putExtra(Intent.EXTRA_SUBJECT, "Bantuan Aplikasi Tanami")
            }
            try {
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(this, "Tidak ada aplikasi Email terinstall", Toast.LENGTH_SHORT).show()
            }
        }

        // 3. Hubungi Instagram
        findViewById<TextView>(R.id.btnInstagram).setOnClickListener {
            bukaLink("https://www.instagram.com/tanami_tech")
        }

        // 4. Hubungi Twitter
        findViewById<TextView>(R.id.btnTwitter).setOnClickListener {
            bukaLink("https://twitter.com/tanami_tech")
        }

        // 5. Hubungi Facebook
        findViewById<TextView>(R.id.btnFacebook).setOnClickListener {
            bukaLink("https://www.facebook.com/tanami_tech")
        }
    }

    // Fungsi helper untuk membuka link di browser/aplikasi
    private fun bukaLink(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        try {
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Tidak dapat membuka link", Toast.LENGTH_SHORT).show()
        }
    }
}