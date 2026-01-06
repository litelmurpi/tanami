package com.example.tanami.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tanami.R
import com.example.tanami.utils.HistoryItem

class HistoryAdapter(
    private var list: List<HistoryItem>,
    private val onDelete: (Int) -> Unit,
    private val onItemClick: (HistoryItem) -> Unit // Parameter baru untuk fitur klik review
) : RecyclerView.Adapter<HistoryAdapter.Holder>() {

    class Holder(v: View) : RecyclerView.ViewHolder(v) {
        val tvTitle: TextView = v.findViewById(R.id.tvTitle)
        val tvDate: TextView = v.findViewById(R.id.tvDate)
        val btnDelete: ImageView = v.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        // Memastikan menggunakan layout item yang sudah Anda buat
        val v = LayoutInflater.from(parent.context).inflate(R.layout.itemtanamcarehistory, parent, false)
        return Holder(v)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = list[position]
        holder.tvTitle.text = item.title
        holder.tvDate.text = item.date

        // --- FITUR KLIK REVIEW ---
        // Saat kartu ditekan, jalankan fungsi onItemClick
        holder.itemView.setOnClickListener {
            onItemClick(item)
        }

        // Tombol hapus tetap berfungsi normal
        holder.btnDelete.setOnClickListener {
            onDelete(position)
        }
    }

    override fun getItemCount() = list.size

    fun updateData(newList: List<HistoryItem>) {
        list = newList
        notifyDataSetChanged()
    }
}