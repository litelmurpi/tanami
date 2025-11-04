package com.example.tanami

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar

class Editakun : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.editakun)

        val toolbar: MaterialToolbar = findViewById(R.id.toolbar) // pastikan id toolbar di layout kamu benar
        toolbar.setNavigationOnClickListener {
            // Tutup activity ini, kembali ke Profile
            finish()
        }
    }
}
