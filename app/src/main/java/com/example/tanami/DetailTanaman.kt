package com.example.tanami

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.example.tanami.models.DetailData
import com.example.tanami.models.TanamanDetailResponse
import com.example.tanami.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.isNotEmpty
import kotlin.collections.joinToString
import kotlin.let
import kotlin.run
import kotlin.text.ifEmpty
import kotlin.text.isNullOrEmpty

class DetailTanaman : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var imageHeader: ImageView
    private lateinit var textNamaTanaman: TextView
    private lateinit var textNamaLatin: TextView
    private lateinit var textBibitMedia: TextView
    private lateinit var textFrekuensi: TextView
    private lateinit var textCara: TextView
    private lateinit var textPemupukan: TextView
    private lateinit var textPerawatan: TextView
    private lateinit var textMasaPanen: TextView
    private lateinit var progressBar: ProgressBar

    private var tanamanId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detailtanaman)

        tanamanId = intent.getIntExtra("TANAMAN_ID", 0)
        val tanamanNama = intent.getStringExtra("TANAMAN_NAMA")

        initViews()
        setupToolbar()

        if (tanamanId > 0) {
            loadDetailTanaman()
        } else {
            Toast.makeText(this, "ID Tanaman tidak valid", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun initViews() {
        toolbar = findViewById(R.id.toolbar)
        imageHeader = findViewById(R.id.imageHeaderTanaman)
        textNamaTanaman = findViewById(R.id.textNamaTanaman)
        textNamaLatin = findViewById(R.id.textNamaLatin)
        textBibitMedia = findViewById(R.id.textBibitMedia)
        textFrekuensi = findViewById(R.id.textFrekuensi)
        textCara = findViewById(R.id.textCara)
        textPemupukan = findViewById(R.id.textPemupukan)
        textPerawatan = findViewById(R.id.textPerawatan)
        textMasaPanen = findViewById(R.id.textMasaPanen)
        progressBar = findViewById(R.id.progressBar) // Tambahkan ke layout
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Detail Tanaman"
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun loadDetailTanaman() {
        showLoading(true)

        RetrofitClient.instance.getDetailTanaman(tanamanId)
            .enqueue(object : Callback<TanamanDetailResponse> {
                override fun onResponse(
                    call: Call<TanamanDetailResponse>,
                    response: Response<TanamanDetailResponse>
                ) {
                    showLoading(false)

                    if (response.isSuccessful) {
                        response.body()?.let { detailResponse ->
                            if (detailResponse.success) {
                                populateData(detailResponse.data)
                            } else {
                                showError(detailResponse.message ?: "Error loading detail")
                            }
                        }
                    } else {
                        showError("Server error: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<TanamanDetailResponse>, t: Throwable) {
                    showLoading(false)
                    showError("Network error: ${t.message}")
                    Log.e("DETAIL_ERROR", "Error: ${t.message}", t)
                }
            })
    }

    private fun populateData(data: DetailData) {
        // Tanaman info
        textNamaTanaman.text = data.tanaman.namaUmum
        textNamaLatin.text = data.tanaman.namaLatin

        // Load header image
        if (!data.tanaman.gambarUrl.isNullOrEmpty()) {
            Glide.with(this)
                .load("http://192.168.1.72/tanami-api/${data.tanaman.gambarUrl}")
                .placeholder(R.drawable.ic_launcher_background)
                .into(imageHeader)
        }

        // Bibit & Media
        data.bibitMedia?.let { bibit ->
            val bibitText = buildString {
                bibit.jenisBibit?.let { append("Bibit: $it\n") }
                bibit.sumberBibit?.let { append("Sumber: $it\n") }
                bibit.jenisMedia?.let { append("Media: $it\n") }
                bibit.rasioMedia?.let { append("Rasio: $it\n") }
                bibit.drainase?.let { append("Drainase: $it\n") }
                bibit.ukuranPot?.let { append("Ukuran Pot: $it") }
            }
            textBibitMedia.text = bibitText.ifEmpty { "-" }
        } ?: run {
            textBibitMedia.text = "Data tidak tersedia"
        }

        // Penyiraman
        data.penyiraman?.let { siram ->
            textFrekuensi.text = "Frekuensi: ${siram.frekuensi ?: "-"}"
            textCara.text = "Cara: ${siram.caraPenyiraman ?: "-"}"
        } ?: run {
            textFrekuensi.text = "Data tidak tersedia"
            textCara.text = ""
        }

        // Pemupukan
        data.pemupukan?.let { pupuk ->
            val pupukText = buildString {
                pupuk.jenisPupuk?.let { append("Jenis: $it\n") }
                pupuk.dosis?.let { append("Dosis: $it\n") }
                pupuk.frekuensi?.let { append("Frekuensi: $it\n") }
                pupuk.caraAplikasi?.let { append("Cara: $it") }
            }
            textPemupukan.text = pupukText.ifEmpty { "-" }
        } ?: run {
            textPemupukan.text = "Data tidak tersedia"
        }

        // Perawatan
        data.perawatan?.let { perawatanList ->
            if (perawatanList.isNotEmpty()) {
                val perawatanText = perawatanList.joinToString("\n\n") { perawatan ->
                    buildString {
                        perawatan.jenisPerawatan?.let { append("$it\n") }
                        perawatan.frekuensi?.let { append("Frekuensi: $it\n") }
                        perawatan.caraPerawatan?.let { append("Cara: $it") }
                    }
                }
                textPerawatan.text = perawatanText
            } else {
                textPerawatan.text = "Data tidak tersedia"
            }
        } ?: run {
            textPerawatan.text = "Data tidak tersedia"
        }

        // Masa Panen
        data.masaPanen?.let { panen ->
            val panenText = buildString {
                panen.durasiTanam?.let { append("Durasi: $it\n") }
                panen.ciriSiapPanen?.let { append("Ciri Siap Panen: $it\n") }
                panen.caraPanen?.let { append("Cara Panen: $it\n") }
                panen.hasilPanen?.let { append("Hasil: $it") }
            }
            textMasaPanen.text = panenText.ifEmpty { "-" }
        } ?: run {
            textMasaPanen.text = "Data tidak tersedia"
        }
    }

    private fun showLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
