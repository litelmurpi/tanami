package com.example.tanami

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tanami.adapter.TanamanAdapter
import com.example.tanami.models.Tanaman
import com.example.tanami.models.TanamanResponse
import com.example.tanami.network.RetrofitClient
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.apply
import kotlin.jvm.java
import kotlin.let
import kotlin.text.isEmpty
import kotlin.toString

class ListPanduan : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var searchEditText: TextInputEditText
    private lateinit var chipGroup: ChipGroup
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar

    private lateinit var tanamanAdapter: TanamanAdapter
    private var allTanamanList = listOf<Tanaman>()
    private var selectedKategoriId: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.listpanduan)

        initViews()
        setupToolbar()
        setupRecyclerView()
        setupChipGroup()
        setupSearchBar()

        loadAllTanaman()
    }

    private fun initViews() {
        toolbar = findViewById(R.id.toolbar)
        searchEditText = findViewById(R.id.searchEditText)
        chipGroup = findViewById(R.id.chipGroup)
        recyclerView = findViewById(R.id.recyclerViewTanaman)
        progressBar = findViewById(R.id.progressBar) // Tambahkan ke layout
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Panduan Menanam"
        toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        tanamanAdapter = TanamanAdapter(emptyList()) { tanaman ->
            val intent = Intent(this, DetailTanaman::class.java)
            intent.putExtra("TANAMAN_ID", tanaman.id)
            intent.putExtra("TANAMAN_NAMA", tanaman.namaUmum)
            startActivity(intent)
        }

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@ListPanduan)
            adapter = tanamanAdapter
        }
    }

    private fun setupChipGroup() {
        chipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
            if (checkedIds.isEmpty()) return@setOnCheckedStateChangeListener

            when (checkedIds[0]) {
                R.id.chipAll -> {
                    selectedKategoriId = null
                    loadAllTanaman()
                }
                R.id.chipSayuran -> {
                    selectedKategoriId = 1
                    loadTanamanByKategori(1)
                }
                R.id.chipTanamanHias -> {
                    selectedKategoriId = 2
                    loadTanamanByKategori(2)
                }
                R.id.chipBuah -> {
                    selectedKategoriId = 3
                    loadTanamanByKategori(3)
                }
            }
        }
    }

    private fun setupSearchBar() {
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s.toString()
                if (query.isEmpty()) {
                    if (selectedKategoriId == null) {
                        loadAllTanaman()
                    } else {
                        loadTanamanByKategori(selectedKategoriId!!)
                    }
                } else {
                    searchTanaman(query)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun loadAllTanaman() {
        showLoading(true)

        RetrofitClient.instance.getAllTanaman().enqueue(object : Callback<TanamanResponse> {
            override fun onResponse(
                call: Call<TanamanResponse>,
                response: Response<TanamanResponse>
            ) {
                showLoading(false)

                if (response.isSuccessful) {
                    response.body()?.let { tanamanResponse ->
                        if (tanamanResponse.success) {
                            allTanamanList = tanamanResponse.data
                            tanamanAdapter.updateData(allTanamanList)
                        } else {
                            showError(tanamanResponse.message ?: "Error loading data")
                        }
                    }
                } else {
                    showError("Server error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<TanamanResponse>, t: Throwable) {
                showLoading(false)
                showError("Network error: ${t.message}")
                Log.e("API_ERROR", "Error: ${t.message}", t)
            }
        })
    }

    private fun loadTanamanByKategori(kategoriId: Int) {
        showLoading(true)

        RetrofitClient.instance.getTanamanByKategori(kategoriId)
            .enqueue(object : Callback<TanamanResponse> {
                override fun onResponse(
                    call: Call<TanamanResponse>,
                    response: Response<TanamanResponse>
                ) {
                    showLoading(false)

                    if (response.isSuccessful) {
                        response.body()?.let { tanamanResponse ->
                            if (tanamanResponse.success) {
                                tanamanAdapter.updateData(tanamanResponse.data)
                            } else {
                                tanamanAdapter.updateData(emptyList())
                                Toast.makeText(
                                    this@ListPanduan,
                                    "Tidak ada data",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } else {
                        showError("Server error: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<TanamanResponse>, t: Throwable) {
                    showLoading(false)
                    showError("Network error: ${t.message}")
                }
            })
    }

    private fun searchTanaman(query: String) {
        showLoading(true)

        RetrofitClient.instance.searchTanaman(query)
            .enqueue(object : Callback<TanamanResponse> {
                override fun onResponse(
                    call: Call<TanamanResponse>,
                    response: Response<TanamanResponse>
                ) {
                    showLoading(false)

                    if (response.isSuccessful) {
                        response.body()?.let { tanamanResponse ->
                            if (tanamanResponse.success) {
                                tanamanAdapter.updateData(tanamanResponse.data)
                            } else {
                                tanamanAdapter.updateData(emptyList())
                                Toast.makeText(
                                    this@ListPanduan,
                                    "Tidak ada hasil",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }

                override fun onFailure(call: Call<TanamanResponse>, t: Throwable) {
                    showLoading(false)
                    showError("Network error: ${t.message}")
                }
            })
    }

    private fun showLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        recyclerView.visibility = if (isLoading) View.GONE else View.VISIBLE
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        Log.e("API_ERROR", message)
    }
}
