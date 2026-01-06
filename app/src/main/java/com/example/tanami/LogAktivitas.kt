package com.example.tanami

import android.animation.ObjectAnimator
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.tanami.utils.LogManager
import com.example.tanami.utils.LogModel
import com.google.android.material.card.MaterialCardView
import java.text.SimpleDateFormat
import java.util.*

class LogAktivitas : AppCompatActivity() {

    private lateinit var logManager: LogManager
    private lateinit var containerLog: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.logaktivitas) // Pastikan nama file XML-nya benar

        logManager = LogManager(this)
        containerLog = findViewById(R.id.containerLog)

        // --- PERUBAHAN DI SINI ---
        // Kita cari ImageView tombol kembali (btnBackLog)
        val btnBack = findViewById<ImageView>(R.id.btnBackLog)

        // Pasang aksi klik
        btnBack.setOnClickListener {
            finish() // Kembali ke menu sebelumnya (Profile)
        }
        // -------------------------

        // 1. Jalankan Simulasi Data
        generateDummyData()

        // 2. Render Tampilan Dropdown
        renderTampilanDropdown()
    }

    // --- (BAGIAN KE BAWAH INI SAMA PERSIS SEPERTI SEBELUMNYA) ---

    private fun generateDummyData() {
        val today = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID")).format(Date())
        val logs = logManager.getLogs()
        val hasAutoToday = logs.any { it.tanggal == today && it.tipe == "OTOMATIS" }

        if (!hasAutoToday) {
            logManager.simpanLog(LogModel("Penyiraman Otomatis", "16:00", today, "OTOMATIS"))
            logManager.simpanLog(LogModel("Penyiraman Otomatis", "10:30", today, "OTOMATIS"))
            logManager.simpanLog(LogModel("Cek Sensor Rutin", "08:00", today, "OTOMATIS"))
        }
    }

    private fun renderTampilanDropdown() {
        containerLog.removeAllViews()
        val logs = logManager.getLogs()

        // Kelompokkan data berdasarkan TANGGAL
        val groupedLogs = logs.groupBy { it.tanggal }

        for ((tanggal, items) in groupedLogs) {

            // 1. KARTU UTAMA
            val card = MaterialCardView(this).apply {
                radius = 30f
                cardElevation = 4f
                setCardBackgroundColor(Color.WHITE)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply { setMargins(0, 0, 0, 24) }
            }

            val mainLayout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(40, 30, 40, 30)
            }

            // 2. HEADER (TANGGAL + PANAH)
            val headerLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                weightSum = 1f
                isClickable = true
                setBackgroundColor(Color.TRANSPARENT)
            }

            val textTanggal = TextView(this).apply {
                text = tanggal
                textSize = 16f
                setTypeface(null, Typeface.BOLD)
                setTextColor(Color.parseColor("#212121"))
                layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            }

            val arrowIcon = ImageView(this).apply {
                setImageResource(R.drawable.icon_dropdown) // Pastikan ikon ini ada
                setColorFilter(Color.GRAY)
                layoutParams = LinearLayout.LayoutParams(48, 48)
            }

            headerLayout.addView(textTanggal)
            headerLayout.addView(arrowIcon)
            mainLayout.addView(headerLayout)

            // 3. WADAH DETAIL (LOG AKTIVITAS)
            val detailLayout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                visibility = View.GONE
                setPadding(0, 20, 0, 0)
            }

            for (log in items) {
                val itemRow = LinearLayout(this).apply {
                    orientation = LinearLayout.HORIZONTAL
                    setPadding(10, 12, 0, 12)
                }

                val textJudul = TextView(this).apply {
                    text = "•  ${log.judul}"
                    textSize = 14f
                    setTextColor(if(log.tipe == "MANUAL") Color.parseColor("#4CAF50") else Color.parseColor("#616161"))
                    layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                }

                val textJam = TextView(this).apply {
                    text = log.jam
                    textSize = 12f
                    setTextColor(Color.LTGRAY)
                }

                itemRow.addView(textJudul)
                itemRow.addView(textJam)
                detailLayout.addView(itemRow)
            }

            mainLayout.addView(detailLayout)
            card.addView(mainLayout)
            containerLog.addView(card)

            // 4. LOGIKA KLIK
            headerLayout.setOnClickListener {
                TransitionManager.beginDelayedTransition(card, AutoTransition())

                if (detailLayout.visibility == View.VISIBLE) {
                    detailLayout.visibility = View.GONE
                    ObjectAnimator.ofFloat(arrowIcon, "rotation", 180f, 0f).start()
                } else {
                    detailLayout.visibility = View.VISIBLE
                    ObjectAnimator.ofFloat(arrowIcon, "rotation", 0f, 180f).start()
                }
            }
        }
    }
}