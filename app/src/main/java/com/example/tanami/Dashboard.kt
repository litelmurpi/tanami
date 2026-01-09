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
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tanami.network.TanamiDeviceFinder
import com.example.tanami.utils.TanamiPrefs
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.progressindicator.CircularProgressIndicator
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

data class DeviceItem(
    val name: String,
    val id: String,
    val isActive: Boolean,
    val moisture: Int = 0,
    val ph: Double = 0.0,
    val temperature: Double = 0.0
)

class Dashboard : AppCompatActivity() {

    private lateinit var iconManual: ImageView
    private lateinit var switchWatering: SwitchCompat
    private lateinit var tvModeTitle: TextView
    private lateinit var tvStatusMode: TextView
    private lateinit var textDeviceName: TextView
    private lateinit var textDeviceId: TextView
    private lateinit var cardDeviceSelector: MaterialCardView
    private lateinit var cardDropdownContainer: MaterialCardView
    private lateinit var rvDropdownList: RecyclerView
    private lateinit var progressKelembaban: CircularProgressIndicator
    private lateinit var textKelembaban: TextView
    private lateinit var progressPH: CircularProgressIndicator
    private lateinit var textPH: TextView
    private lateinit var progressTemp: CircularProgressIndicator
    private lateinit var textTemp: TextView
    private lateinit var textLastUpdate: TextView

    private lateinit var prefs: TanamiPrefs
    private lateinit var sessionManager: SessionManager
    private lateinit var deviceFinder: TanamiDeviceFinder
    private var currentDeviceIp: String? = null
    private var isDropdownOpen = false
    private var isAutoMode = false
    private val dummyDevices = mutableListOf<DeviceItem>()

    private val handler = Handler(Looper.getMainLooper())
    private val fetchRunnable = object : Runnable {
        override fun run() {
            fetchRealtimeData()
            handler.postDelayed(this, 2000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dashboard)

        prefs = TanamiPrefs(this)
        sessionManager = SessionManager(this)

        deviceFinder = TanamiDeviceFinder(this) { ip, name ->
            runOnUiThread {
                if (currentDeviceIp == null || currentDeviceIp != ip) {
                    currentDeviceIp = ip
                    refreshDashboardState()
                }
            }
        }

        initViews()
        initDummyData()
        setupListeners()
        displayUserData()
        refreshDashboardState()
    }

    private fun initViews() {
        iconManual = findViewById(R.id.iconManual)
        switchWatering = findViewById(R.id.switchWatering)
        tvModeTitle = findViewById(R.id.tvModeTitle)
        tvStatusMode = findViewById(R.id.tvStatusMode)
        textDeviceName = findViewById(R.id.textDeviceName)
        textDeviceId = findViewById(R.id.textDeviceId)
        cardDeviceSelector = findViewById(R.id.cardDeviceSelector)
        cardDropdownContainer = findViewById(R.id.cardDropdownContainer)
        rvDropdownList = findViewById(R.id.rvDropdownList)
        progressKelembaban = findViewById(R.id.progressKelembaban)
        textKelembaban = findViewById(R.id.textKelembaban)
        progressPH = findViewById(R.id.progressPH)
        textPH = findViewById(R.id.textPH)
        progressTemp = findViewById(R.id.progressTemp)
        textTemp = findViewById(R.id.textTemp)
        textLastUpdate = findViewById(R.id.textLastUpdate)
    }

    private fun initDummyData() {
        dummyDevices.clear()
        dummyDevices.add(DeviceItem("Kebun Tomat", "DEV-001", true, 75, 9.9, 14.2)) // Sensor pH/Temp 0
        dummyDevices.add(DeviceItem("Cabai Rawit", "DEV-002", false, 40, 2.9, 58.7))
        dummyDevices.add(DeviceItem("Tanaman Hias", "DEV-003", false, 90, 5.8, 2.3))
    }


    // --- FITUR SIRAM OTOMATIS DAN MANUAL ---
    private fun setupListeners() {
        findViewById<ImageView>(R.id.btnAddDevice).setOnClickListener { startActivity(Intent(this, TambahPerangkat::class.java)) }
        cardDeviceSelector.setOnClickListener { toggleDropdown() }

        iconManual.setOnClickListener {
            isAutoMode = !isAutoMode
            if (isAutoMode) {
                iconManual.setColorFilter(Color.parseColor("#4CAF50"))
                tvModeTitle.text = "Siram\nOtomatis"
                tvStatusMode.text = "Mode: Otomatis"
                switchWatering.alpha = 0.5f
                Toast.makeText(this, "Mode Otomatis Aktif", Toast.LENGTH_SHORT).show()
                sendModeCommandToEsp32(false)
            } else {
                iconManual.setColorFilter(Color.parseColor("#2D2D2D"))
                tvModeTitle.text = "Siram\nManual"
                tvStatusMode.text = "Mode: Manual"
                switchWatering.alpha = 1.0f
                Toast.makeText(this, "Mode Manual Aktif", Toast.LENGTH_SHORT).show()
                sendModeCommandToEsp32(true)
            }
        }

        switchWatering.setOnClickListener {
            if (isAutoMode) {
                switchWatering.isChecked = !switchWatering.isChecked
                Toast.makeText(this, "Matikan mode otomatis dulu", Toast.LENGTH_SHORT).show()
            } else if (currentDeviceIp != null) {
                sendCommandToEsp32(switchWatering.isChecked)
            }
        }
    }

    // --- FITUR EDIT & HAPUS PULIH ---
    private fun setupDropdownList() {
        rvDropdownList.layoutManager = LinearLayoutManager(this)
        val historyList = prefs.getDeviceHistory()
        val dropdownItems = mutableListOf<DeviceItem>()

        dropdownItems.addAll(dummyDevices)
        historyList.forEach {
            if (dummyDevices.none { d -> d.id == it.second }) {
                dropdownItems.add(DeviceItem(it.first, it.second, it.second == prefs.getDeviceIp()))
            }
        }

        rvDropdownList.adapter = DashboardDropdownAdapter(dropdownItems, prefs.getDeviceName(),
            { selected -> // OnClick
                prefs.saveCurrentDevice(selected.name, selected.id)
                refreshDashboardState()
                closeDropdown()
            },
            { item -> showRenameDialog(item) }, // OnEdit PULIH
            { item -> showDeleteConfirmation(item) } // OnDelete PULIH
        )
    }

    private fun showRenameDialog(item: DeviceItem) {
        if (item.id.startsWith("DEV")) return // Jangan edit dummy
        val input = EditText(this).apply { setText(item.name) }
        AlertDialog.Builder(this).setTitle("Ubah Nama").setView(input)
            .setPositiveButton("Simpan") { _, _ ->
                prefs.renameDevice(item.id, input.text.toString())
                setupDropdownList()
                refreshDashboardState()
            }.setNegativeButton("Batal", null).show()
    }

    private fun showDeleteConfirmation(item: DeviceItem) {
        if (item.id.startsWith("DEV")) return // Jangan hapus dummy
        AlertDialog.Builder(this).setTitle("Hapus Perangkat")
            .setMessage("Yakin ingin menghapus ${item.name}?")
            .setPositiveButton("Hapus") { _, _ ->
                prefs.removeDevice(item.id)
                setupDropdownList()
                refreshDashboardState()
            }.setNegativeButton("Batal", null).show()
    }

    private fun refreshDashboardState() {
        val savedName = prefs.getDeviceName()
        val savedIpOrId = prefs.getDeviceIp()
        handler.removeCallbacks(fetchRunnable)

        val dummyMatch = dummyDevices.find { it.name == savedName }
        if (dummyMatch != null) {
            textDeviceName.text = dummyMatch.name
            textDeviceId.text = "ID: ${dummyMatch.id}"
            currentDeviceIp = null
            updateMonitoringUI(dummyMatch.moisture, dummyMatch.ph, dummyMatch.temperature)
        } else if (savedIpOrId != null && savedIpOrId != "0.0.0.0") {
            textDeviceName.text = savedName
            textDeviceId.text = "IP: $savedIpOrId"
            currentDeviceIp = savedIpOrId
            handler.post(fetchRunnable)
        } else {
            deviceFinder.startDiscovery()
        }
    }

    private fun fetchRealtimeData() {
        if (currentDeviceIp == null) return
        Thread {
            try {
                val conn = URL("http://$currentDeviceIp/status").openConnection() as HttpURLConnection
                if (conn.responseCode == 200) {
                    val response = conn.inputStream.bufferedReader().use { it.readText() }
                    val json = JSONObject(response)
                    runOnUiThread {
                        // Ambil data dari ESP, jika 0 tampilkan 0
                        updateMonitoringUI(json.optInt("moisture"), json.optDouble("ph"), json.optDouble("temperature"))
                        updateLastUpdate()
                    }
                }
                conn.disconnect()
            } catch (e: Exception) { e.printStackTrace() }
        }.start()
    }

    private fun updateMonitoringUI(humidity: Int, ph: Double, temperature: Double) {
        progressKelembaban.progress = humidity
        textKelembaban.text = if (humidity > 0) "$humidity%" else "0"

        progressPH.progress = (ph * 7).toInt()
        textPH.text = if (ph > 0.0) String.format("%.1f", ph) else "0,0"

        progressTemp.progress = (temperature * 2).toInt()
        textTemp.text = if (temperature > 0.0) String.format("%.1f°C", temperature) else "0,0"
    }

    private fun updateLastUpdate() {
        textLastUpdate.text = "terakhir diperbarui: ${SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())} WIB"
    }

    private fun displayUserData() {
        findViewById<TextView>(R.id.tvGreetingTitle).text = "Hai, ${sessionManager.getUserName()}!"
    }

    private fun sendModeCommandToEsp32(isManual: Boolean) {
        if (currentDeviceIp == null) return
        Thread {
            try {
                val mode = if (isManual) "1" else "0"
                URL("http://$currentDeviceIp/mode?manual=$mode").openConnection().getHeaderField(0)
            } catch (e: Exception) { e.printStackTrace() }
        }.start()
    }

    private fun sendCommandToEsp32(isOn: Boolean) {
        Thread {
            try {
                val state = if (isOn) "ON" else "OFF"
                URL("http://$currentDeviceIp/control?pompa=$state").openConnection().getHeaderField(0)
            } catch (e: Exception) { e.printStackTrace() }
        }.start()
    }

    private fun toggleDropdown() { if (isDropdownOpen) closeDropdown() else openDropdown() }
    private fun openDropdown() { cardDropdownContainer.visibility = View.VISIBLE; setupDropdownList(); isDropdownOpen = true }
    private fun closeDropdown() { cardDropdownContainer.visibility = View.GONE; isDropdownOpen = false }
}

// ADAPTER DROPDOWN
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
        val dot: ImageView = v.findViewById(R.id.img_status_dot)
        val indicator: View = v.findViewById(R.id.indicator_selected)
        val btnEdit: ImageView = v.findViewById(R.id.btn_edit)
        val btnDelete: ImageView = v.findViewById(R.id.btn_delete)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder =
        Holder(LayoutInflater.from(parent.context).inflate(R.layout.itempilihanperangkat, parent, false))

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = list[position]
        holder.tvName.text = item.name
        holder.tvId.text = item.id
        holder.dot.setColorFilter(if (item.id.startsWith("DEV")) Color.parseColor("#4CAF50") else Color.parseColor("#BDBDBD"))

        val isSelected = (item.name == currentActiveName)
        holder.indicator.visibility = if (isSelected) View.VISIBLE else View.GONE
        holder.card.setCardBackgroundColor(if (isSelected) Color.WHITE else Color.parseColor("#F5F5F5"))

        // Sembunyikan tombol edit/hapus jika perangkat dummy (DEV-xxx)
        if (item.id.startsWith("DEV")) {
            holder.btnEdit.visibility = View.GONE
            holder.btnDelete.visibility = View.GONE
        } else {
            holder.btnEdit.visibility = View.VISIBLE
            holder.btnDelete.visibility = View.VISIBLE
        }

        holder.itemView.setOnClickListener { onClick(item) }
        holder.btnEdit.setOnClickListener { onEdit(item) }
        holder.btnDelete.setOnClickListener { onDelete(item) }
    }
    override fun getItemCount() = list.size
}