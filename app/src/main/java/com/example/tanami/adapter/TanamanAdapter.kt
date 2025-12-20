package com.example.tanami.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tanami.R
import com.example.tanami.models.Tanaman

class TanamanAdapter(
    private var tanamanList: List<Tanaman>,
    private val onItemClick: (Tanaman) -> Unit
) : RecyclerView.Adapter<TanamanAdapter.TanamanViewHolder>() {

    inner class TanamanViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardView: CardView = itemView.findViewById(R.id.cardViewTanaman)
        val textNamaTanaman: TextView = itemView.findViewById(R.id.textNamaTanaman)
        val textNamaLatin: TextView = itemView.findViewById(R.id.textNamaLatin)
        val imageTanaman: ImageView = itemView.findViewById(R.id.imageTanaman)
        val iconArrow: ImageView = itemView.findViewById(R.id.iconArrow)

        fun bind(tanaman: Tanaman) {
            textNamaTanaman.text = tanaman.namaUmum
            textNamaLatin.text = tanaman.namaLatin

            // Load image dengan Glide
            if (!tanaman.gambarUrl.isNullOrEmpty()) {
                imageTanaman.visibility = View.VISIBLE
                Glide.with(itemView.context)
                    .load("http://192.168.1.72/tanami-api/${tanaman.gambarUrl}")
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .into(imageTanaman)
            } else {
                imageTanaman.visibility = View.GONE
            }

            cardView.setOnClickListener {
                onItemClick(tanaman)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TanamanViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.itemtanamancard, parent, false)
        return TanamanViewHolder(view)
    }

    override fun onBindViewHolder(holder: TanamanViewHolder, position: Int) {
        holder.bind(tanamanList[position])
    }

    override fun getItemCount(): Int = tanamanList.size

    fun updateData(newList: List<Tanaman>) {
        tanamanList = newList
        notifyDataSetChanged()
    }
}
