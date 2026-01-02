package com.example.tanami

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DeviceAdapter(
    private val deviceList: MutableList<String>,
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<DeviceAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // Mengambil ID dari file itemscanperangkat.xml
        val tvName: TextView = view.findViewById(R.id.tv_device_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Memanggil layout custom Nobu-sama
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.itemscanperangkat, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val deviceName = deviceList[position]
        holder.tvName.text = deviceName

        holder.itemView.setOnClickListener {
            onItemClick(deviceName)
        }
    }

    override fun getItemCount() = deviceList.size

    // Fungsi helper untuk update data real-time
    fun updateData(newData: List<String>) {
        deviceList.clear()
        deviceList.addAll(newData)
        notifyDataSetChanged()
    }
}