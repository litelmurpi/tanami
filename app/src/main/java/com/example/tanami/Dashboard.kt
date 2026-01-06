package com.example.tanami

import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tanami.utils.TanamiPrefs
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.progressindicator.CircularProgressIndicator
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

// Model Data untuk Dropdown
data class DeviceItem(val name: String, val id: String, val isActive: Boolean)

class Dashboard : AppCompatActivity() {

    // --- VIEWS ---
    private lateinit var imgAvatar: ImageView
    private lateinit var cardDeviceSelector: MaterialCardView
    private lateinit var textDeviceName: TextView
    private lateinit var textDeviceId: TextView
    private lateinit var btnAddDevice: ImageView
    private lateinit var iconArrow: ImageView
    private lateinit var cardDropdownContainer: MaterialCardView
    private lateinit var rvDropdownList: RecyclerView

    // --- MONITORING VIEWS ---
    private lateinit var progressKelembaban: CircularProgressIndicator
    private lateinit var textKelembaban: TextView
    private lateinit var progressPH: CircularProgressIndicator
    private lateinit var textPH: TextView
    private lateinit var progressTemp: CircularProgressIndicator
    private lateinit var textTemp: TextView
    private lateinit var switchWatering: SwitchCompat
    private lateinit var textLastUpdate: TextView
    private lateinit var btnPanduan: MaterialButton
    private lateinit var btnTanamCare: MaterialButton

    // --- VARIABEL UTAMA ---
    private lateinit var prefs: TanamiPrefs
    private var currentDeviceIp: String? = null
    private var isDropdownOpen = false

    // --- HANDLER UNTUK DATA REAL-TIME (LOOPING) ---
    private val handler = Handler(Looper.getMainLooper())
    private val fetchRunnable = object : Runnable {
        override fun run() {
            fetchRealtimeData() // Ambil data dari ESP32
            handler.postDelayed(this, 2000) // Ulangi setiap 2 detik
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        // Force Light Mode agar tampilan konsisten
        androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO)

        setContentView(R.layout.dashboard)

        prefs = TanamiPrefs(this)
        initViews()
        setupListeners()
        updateLastUpdate()
    }

    override fun onResume() {
        super.onResume()
        refreshDashboardState()
        if (isDropdownOpen) closeDropdown()
    }

    override fun onPause() {
        super.onPause()
        // PENTING: Matikan pengambilan data saat aplikasi diminimize agar hemat baterai
        handler.removeCallbacks(fetchRunnable)
    }

    private fun initViews() {
        imgAvatar = findViewById(R.id.imgAvatar)
        cardDeviceSelector = findViewById(R.id.cardDeviceSelector)
        textDeviceName = findViewById(R.id.textDeviceName)
        textDeviceId = findViewById(R.id.textDeviceId)
        btnAddDevice = findViewById(R.id.btnAddDevice)
        iconArrow = findViewById(R.id.iconArrow)
        cardDropdownContainer = findViewById(R.id.cardDropdownContainer)
        rvDropdownList = findViewById(R.id.rvDropdownList)
        progressKelembaban = findViewById(R.id.progressKelembaban)
        textKelembaban = findViewById(R.id.textKelembaban)
        progressPH = findViewById(R.id.progressPH)
        textPH = findViewById(R.id.textPH)
        progressTemp = findViewById(R.id.progressTemp)
        textTemp = findViewById(R.id.textTemp)
        switchWatering = findViewById(R.id.switchWatering)
        textLastUpdate = findViewById(R.id.textLastUpdate)
        btnPanduan = findViewById(R.id.btnPanduan)
        btnTanamCare = findViewById(R.id.btnTanamCare)
    }

    private fun refreshDashboardState() {
        val savedName = prefs.getDeviceName()
        val savedIp = prefs.getDeviceIp()

        // Hapus antrian lama agar tidak double loop
        handler.removeCallbacks(fetchRunnable)

        if (savedIp != null) {
            textDeviceName.text = savedName
            textDeviceId.text = "IP: $savedIp"
            currentDeviceIp = savedIp
            switchWatering.isEnabled = true

            // MULAI LOOPING PENGAMBILAN DATA REAL-TIME
            handler.post(fetchRunnable)

        } else {
            textDeviceName.text = "Pilih Kebun"
            textDeviceId.text = "Belum terhubung"
            currentDeviceIp = null
            switchWatering.isEnabled = false
            switchWatering.isChecked = false
            updateMonitoringUI(0, 0.0, 0.0)
        }
    }

    // --- FUNGSI BARU: AMBIL DATA REAL DARI ESP32 ---
    private fun fetchRealtimeData() {
        if (currentDeviceIp == null) return

        Thread {
            try {
                // 1. Panggil URL ESP32
                val url = URL("http://$currentDeviceIp/status")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                conn.connectTimeout = 1500 // Timeout cepat (1.5 detik)

                if (conn.responseCode == 200) {
                    val stream = conn.inputStream
                    // Baca respon string
                    val response = Scanner(stream).useDelimiter("\\A").next()

                    // 2. Parsing JSON {"moisture": 45, "pump": "OFF", "mode": "AUTO"}
                    val json = JSONObject(response)
                    val moisture = json.optInt("moisture", 0)
                    val temp = json.optDouble("temperature", 0.0)

                    // (Opsional) Ambil status pompa jika ingin sinkronisasi switch
                    // val pumpStatus = json.optString("pump")

                    // 3. Update UI di Main Thread
                    runOnUiThread {
                        // Masukkan data asli ke UI
                        // Karena hardware ESP32 belum ada sensor PH & Suhu, kita set 0 atau dummy dulu
                        // Fokus ke Moisture (Sensor Tanah)
                        updateMonitoringUI(moisture, 0.0, temp)
                        updateLastUpdate() // Update jam
                    }
                }
                conn.disconnect()
            } catch (e: Exception) {
                // Jangan crash kalau gagal konek (misal alat mati), diam saja atau log
                e.printStackTrace()
            }
        }.start()
    }

    private fun setupListeners() {
        btnAddDevice.setOnClickListener { startActivity(Intent(this, TambahPerangkat::class.java)) }
        cardDeviceSelector.setOnClickListener { toggleDropdown() }
        switchWatering.setOnCheckedChangeListener { _, isChecked ->
            if (currentDeviceIp == null) {
                Toast.makeText(this, "Pilih kebun dulu!", Toast.LENGTH_SHORT).show()
                switchWatering.isChecked = false
                return@setOnCheckedChangeListener
            }
            if (switchWatering.isPressed) sendCommandToEsp32(isChecked)
        }

        btnPanduan.setOnClickListener { startActivity(Intent(this, ListPanduan::class.java)) }

        // --- MODIFIKASI UTAMA DISINI: LINK KE HALAMAN SCAN AI ---
        btnTanamCare.setOnClickListener {
            startActivity(Intent(this, TanamCare::class.java))
        }
    }

    private fun toggleDropdown() {
        if (isDropdownOpen) closeDropdown() else openDropdown()
    }

    private fun openDropdown() {
        cardDropdownContainer.visibility = View.VISIBLE
        ObjectAnimator.ofFloat(iconArrow, "rotation", 0f, 180f).apply { duration = 300; start() }
        setupDropdownList()
        isDropdownOpen = true
    }

    private fun closeDropdown() {
        cardDropdownContainer.visibility = View.GONE
        ObjectAnimator.ofFloat(iconArrow, "rotation", 180f, 0f).apply { duration = 300; start() }
        isDropdownOpen = false
    }

    private fun setupDropdownList() {
        rvDropdownList.layoutManager = LinearLayoutManager(this)

        val historyList = prefs.getDeviceHistory()
        val dropdownItems = mutableListOf<DeviceItem>()

        if (historyList.isNotEmpty()) {
            historyList.forEach {
                val isActive = (it.second == prefs.getDeviceIp())
                dropdownItems.add(DeviceItem(it.first, it.second, isActive))
            }
        } else {
            // Data Dummy jika kosong
            dropdownItems.add(DeviceItem("Tanami Device 1", "0.0.0.0", false))
        }

        val currentName = prefs.getDeviceName()

        rvDropdownList.adapter = DashboardDropdownAdapter(
            list = dropdownItems,
            currentActiveName = currentName,
            onClick = { selected ->
                prefs.saveCurrentDevice(selected.name, selected.id)
                refreshDashboardState()
                closeDropdown()
                Toast.makeText(this, "Berpindah ke ${selected.name}", Toast.LENGTH_SHORT).show()
            },
            onEdit = { itemToEdit ->
                showRenameDialog(itemToEdit)
            },
            onDelete = { itemToDelete ->
                showDeleteConfirmation(itemToDelete)
            }
        )
    }

    private fun showRenameDialog(item: DeviceItem) {
        val input = EditText(this)
        input.setText(item.name)
        input.setSelection(input.text.length)

        val container = android.widget.FrameLayout(this)
        val params = android.widget.FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        params.leftMargin = 60
        params.rightMargin = 60
        input.layoutParams = params
        container.addView(input)

        AlertDialog.Builder(this)
            .setTitle("Ubah Nama")
            .setMessage("Ganti nama untuk: ${item.name}")
            .setView(container)
            .setPositiveButton("Simpan") { _, _ ->
                val newName = input.text.toString().trim()
                if (newName.isNotEmpty()) {
                    prefs.renameDevice(item.id, newName)
                    setupDropdownList()
                    refreshDashboardState()
                    Toast.makeText(this, "Nama berhasil diubah!", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun showDeleteConfirmation(item: DeviceItem) {
        AlertDialog.Builder(this)
            .setTitle("Hapus Perangkat?")
            .setMessage("Apakah Anda yakin ingin menghapus '${item.name}'? Data tidak bisa dikembalikan.")
            .setPositiveButton("Hapus") { _, _ ->
                prefs.removeDevice(item.id)
                setupDropdownList()
                refreshDashboardState()
                Toast.makeText(this, "${item.name} dihapus.", Toast.LENGTH_SHORT).show()

                if (prefs.getDeviceHistory().isEmpty()) {
                    closeDropdown()
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun sendCommandToEsp32(isOn: Boolean) {
        val state = if (isOn) "ON" else "OFF"
        val targetUrl = "http://$currentDeviceIp/control?pompa=$state"
        Toast.makeText(this, "Mengirim $state...", Toast.LENGTH_SHORT).show()
        Thread {
            try {
                val url = URL(targetUrl)
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                conn.connectTimeout = 3000
                val code = conn.responseCode
                runOnUiThread {
                    if (code == 200) {
                        Toast.makeText(this, "Sukses: Pompa $state ✅", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Gagal: Error $code ❌", Toast.LENGTH_SHORT).show()
                        switchWatering.isChecked = !isOn
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this, "Gagal Konek ke Alat ⚠️", Toast.LENGTH_SHORT).show()
                    switchWatering.isChecked = !isOn
                }
            }
        }.start()
    }

    private fun updateMonitoringUI(humidity: Int, ph: Double, temperature: Double) {
        // Update Kelembaban (Data Asli)
        progressKelembaban.progress = humidity
        textKelembaban.text = "$humidity%"

        // Update PH (Data Dummy / 0)
        val phProgress = ((ph / 14.0) * 100).toInt()
        progressPH.progress = phProgress
        textPH.text = String.format("%.1f", ph)

        // Update Suhu (Data Dummy / 0)
        val tempProgress = ((temperature / 50.0) * 100).toInt()
        progressTemp.progress = tempProgress
        textTemp.text = String.format("%.1f", temperature)
    }

    private fun updateLastUpdate() {
        val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale("id", "ID"))
        textLastUpdate.text = "terakhir diperbarui: ${sdf.format(Date())} WIB"
    }
}

// --- ADAPTER TIDAK PERLU DIUBAH ---
class DashboardDropdownAdapter(
    private val list: List<DeviceItem>,
    private val currentActiveName: String?,
    private val onClick: (DeviceItem) -> Unit,
    private val onEdit: (DeviceItem) -> Unit,
    private val onDelete: (DeviceItem) -> Unit
) : RecyclerView.Adapter<DashboardDropdownAdapter.Holder>() {

    class Holder(v: View) : RecyclerView.ViewHolder(v) {
        val card: MaterialCardView = v.findViewById(R.id.card_root)
        val tvName: TextView = v.findViewById(R.id.tv_device_name)
        val tvId: TextView = v.findViewById(R.id.tv_device_id)
        val indicator: View = v.findViewById(R.id.indicator_selected)
        val dot: ImageView = v.findViewById(R.id.img_status_dot)
        val btnEdit: ImageView = v.findViewById(R.id.btn_edit)
        val btnDelete: ImageView = v.findViewById(R.id.btn_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.itempilihanperangkat, parent, false)
        return Holder(v)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = list[position]
        holder.tvName.text = item.name
        holder.tvId.text = item.id

        if (item.isActive) {
            holder.dot.setColorFilter(Color.parseColor("#4CAF50"))
        } else {
            holder.dot.setColorFilter(Color.parseColor("#BDBDBD"))
        }

        val isSelected = (item.name == currentActiveName)
        if (isSelected) {
            holder.card.setCardBackgroundColor(Color.WHITE)
            holder.indicator.visibility = View.VISIBLE
            holder.card.elevation = 8f
        } else {
            holder.card.setCardBackgroundColor(Color.parseColor("#F5F5F5"))
            holder.indicator.visibility = View.GONE
            holder.card.elevation = 0f
        }

        holder.itemView.setOnClickListener { onClick(item) }
        holder.btnEdit.setOnClickListener { onEdit(item) }
        holder.btnDelete.setOnClickListener { onDelete(item) }
    }

    override fun getItemCount() = list.size
}