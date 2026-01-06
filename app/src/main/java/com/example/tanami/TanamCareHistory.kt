package com.example.tanami

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tanami.adapter.HistoryAdapter
import com.example.tanami.utils.HistoryManager
import com.example.tanami.utils.HistoryItem
import com.google.android.material.bottomsheet.BottomSheetDialog

class TanamCareHistory : AppCompatActivity() {
    private lateinit var historyManager: HistoryManager
    private lateinit var adapter: HistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tanamcarehistory)

        historyManager = HistoryManager(this)
        val rv = findViewById<RecyclerView>(R.id.rvHistory)
        rv.layoutManager = LinearLayoutManager(this)

        // Inisialisasi Adapter dengan logika baru
        adapter = HistoryAdapter(
            list = historyManager.getHistory(),
            onDelete = { position ->
                historyManager.deleteHistory(position)
                adapter.updateData(historyManager.getHistory())
            },
            onItemClick = { item ->
                // Panggil fungsi review saat item di list ditekan
                showReviewDetail(item)
            }
        )
        rv.adapter = adapter

        findViewById<View>(R.id.btnBack).setOnClickListener { finish() }
    }

    /**
     * Menampilkan kembali detail diagnosa dari memori
     * Menggunakan layout yang sama dengan hasil scan agar desain konsisten
     */

    private fun showReviewDetail(item: com.example.tanami.utils.HistoryItem) {
        val dialog = BottomSheetDialog(this, R.style.TransparentBottomSheetDialog)
        val view = layoutInflater.inflate(R.layout.tanamcareresult, null)
        dialog.setContentView(view)

        dialog.window?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            ?.setBackgroundResource(android.R.color.transparent)

        view.findViewById<android.widget.TextView>(R.id.tvDiseaseTitle).text = item.title
        view.findViewById<android.widget.TextView>(R.id.tvSolution).text = item.solution

        // MENGGABUNGKAN SAPAAN DENGAN ISI DIAGNOSA ASLI
        val fullExplanation = "Hai sobat kebun! Ini catatan diagnosa kamu tanggal ${item.date}:\n\n${item.explanation}"
        view.findViewById<android.widget.TextView>(R.id.tvExplanation).text = fullExplanation

        view.findViewById<android.widget.TextView>(R.id.tvConfidence).text = "Saved"
        view.findViewById<android.widget.ProgressBar>(R.id.progressBarConfidence).progress = 100

        dialog.show()
    }
}