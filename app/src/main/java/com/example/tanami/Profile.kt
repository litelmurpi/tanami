package com.example.tanami

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent
import android.util.Log
import android.widget.TextView
import com.google.android.material.appbar.MaterialToolbar


class Profile : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile)

        val akun: MaterialToolbar = findViewById(R.id.editakun)
        val aktivitas: MaterialToolbar = findViewById(R.id.aktivitas)
        val privasiAkses: MaterialToolbar = findViewById(R.id.privakses)
        val pusatbantuan: MaterialToolbar = findViewById(R.id.pusatbantuan)
        val tentang: MaterialToolbar = findViewById(R.id.tentang)

        akun.setNavigationOnClickListener {
            // Intent menuju EditProfileActivity
            Log.d("Profile", "Toolbar diklik")
            val pindah = Intent(this, Editakun::class.java)
            startActivity(pindah)
        }
        val tvEditAkun: TextView = findViewById(R.id.textEditAkun)
        tvEditAkun.setOnClickListener {
            Log.d("Profile", "TextView diklik")
            startActivity(Intent(this, Editakun::class.java))
        }

        aktivitas.setNavigationOnClickListener {
            // Intent menuju EditProfileActivity
            Log.d("Profile", "Toolbar diklik")
            val pindah = Intent(this, Logaktivitas::class.java)
            startActivity(pindah)
        }
        val tvLogAktivitas: TextView = findViewById(R.id.textAktivitas)
        tvLogAktivitas.setOnClickListener {
            Log.d("Profile", "TextView diklik")
            startActivity(Intent(this, Logaktivitas::class.java))
        }

        privasiAkses.setNavigationOnClickListener {
            // Intent menuju EditProfileActivity
            Log.d("Profile", "Toolbar diklik")
            val pindah = Intent(this, PrivasiAkses::class.java)
            startActivity(pindah)
        }
        val tvPrivAkses: TextView = findViewById(R.id.textPrivAkses)
        tvPrivAkses.setOnClickListener {
            Log.d("Profile", "TextView diklik")
            startActivity(Intent(this, PrivasiAkses::class.java))
        }

        pusatbantuan.setNavigationOnClickListener {
            // Intent menuju EditProfileActivity
            Log.d("Profile", "Toolbar diklik")
            val pindah = Intent(this, Bantuan::class.java)
            startActivity(pindah)
        }
        val tvPusatBantuan: TextView = findViewById(R.id.textPusatbantuan)
        tvPusatBantuan.setOnClickListener {
            Log.d("Profile", "TextView diklik")
            startActivity(Intent(this, Bantuan::class.java))
        }

        tentang.setNavigationOnClickListener {
            // Intent menuju EditProfileActivity
            Log.d("Profile", "Toolbar diklik")
            val pindah = Intent(this, Tentang::class.java)
            startActivity(pindah)
        }
        val tvTentang: TextView = findViewById(R.id.textTentang)
        tvTentang.setOnClickListener {
            Log.d("Profile", "TextView diklik")
            startActivity(Intent(this, Tentang::class.java))
        }







    }
}