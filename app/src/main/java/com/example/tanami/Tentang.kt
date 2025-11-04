 package com.example.tanami

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.appbar.MaterialToolbar

 class Tentang : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tentang)

        val toolbar: MaterialToolbar = findViewById(R.id.toolbar5) // pastikan id toolbar di layout kamu benar
        toolbar.setNavigationOnClickListener {

            finish()
        }

    }
}