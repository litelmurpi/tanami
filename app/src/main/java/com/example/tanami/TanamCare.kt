package com.example.tanami

import android.Manifest
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.TextView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.tanami.databinding.TanamcareBinding
import com.example.tanami.utils.HistoryManager
import com.example.tanami.utils.HistoryItem
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class TanamCare : AppCompatActivity() {

    private lateinit var binding: TanamcareBinding
    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService
    private var scannerAnimator: ObjectAnimator? = null

    private val apiKey = "AIzaSyB9OeqWLYCEG9vdTTRjJNYV-46eaz0Et3o"

    private val generativeModel = GenerativeModel(
        modelName = "gemini-2.5-flash",
        apiKey = apiKey
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = TanamcareBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        binding.btnAnalyze.setOnClickListener { takePhotoAndAnalyze() }
        binding.btnBack.setOnClickListener { finish() }

        binding.btnHistory.setOnClickListener {
            startActivity(Intent(this, TanamCareHistory::class.java))
        }

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
            }
            imageCapture = ImageCapture.Builder().build()
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
                startScannerAnimation()
            } catch (exc: Exception) {}
        }, ContextCompat.getMainExecutor(this))
    }

    private fun startScannerAnimation() {
        binding.root.post {
            val borderHeight = binding.imgScanBorder.height.toFloat()
            val startY = 135f
            val endY = borderHeight - 150f

            scannerAnimator = ObjectAnimator.ofFloat(binding.scannerLine, "translationY", startY, endY).apply {
                duration = 2000
                repeatCount = ValueAnimator.INFINITE
                repeatMode = ValueAnimator.REVERSE
                interpolator = AccelerateDecelerateInterpolator()
                start()
            }
            binding.scannerLine.visibility = View.VISIBLE
        }
    }

    private fun takePhotoAndAnalyze() {
        val imageCapture = imageCapture ?: return
        showLoading(true)

        val photoFile = File(externalCacheDir, "scan_tanaman.jpg")
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions, ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    showLoading(false)
                }
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    analyzeImageWithAI(photoFile)
                }
            }
        )
    }

    private fun analyzeImageWithAI(photoFile: File) {
        val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 1024, 1024, true)

        // PROMPT KEMBALI KE KARAKTER RAMAH & SOBAT KEBUN
        val prompt = """
            Berperanlah sebagai "Tanamin", asisten berkebun yang super ramah, ceria, dan sangat membantu. 
            Tugasmu adalah menjadi dokter kebun sekaligus partner yang asik buat diajak ngobrol.
            
            Analisa gambar ini:
            1. Kalau BUKAN tanaman, jawab santai: "Waduh, Tanamin bingung nih 🤔. Sepertinya ini bukan tanaman deh. Coba fotoin tanamannya lagi yang jelas ya!"
            
            2. Kalau ini tanaman, berikan diagnosa dengan struktur (Gunakan bahasa yang mudah dimengerti orang awam, jangan kaku):
            
            Nama Penyakit: [Sebutkan nama penyakitnya, atau "Sehat Walafiat 🌱" kalau tanamannya oke]
            
            Penjelasan:
            [Jelaskan apa yang terjadi dengan bahasa teman. Gunakan emoji yang sesuai agar lebih ceria.]
            
            Skor: [Berikan angka saja 0-100 sebagai tingkat keyakinanmu]
            
            Solusi dari Tanamin:
            1. [Langkah praktis pertama yang gampang dilakuin]
            2. [Langkah praktis kedua]
            3. [Tips simpel biar nggak kambuh lagi]
            
            Tutup dengan satu kalimat penyemangat yang bikin semangat berkebun!
        """.trimIndent()

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val inputContent = content { image(scaledBitmap); text(prompt) }
                val response = generativeModel.generateContent(inputContent)
                val resultText = response.text ?: "Duh, maaf banget Tanamin gagal analisa kodenya."

                withContext(Dispatchers.Main) {
                    showLoading(false)
                    showResultSheet(resultText)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showLoading(false)
                    Toast.makeText(baseContext, "Gagal: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showResultSheet(resultText: String) {
        val dialog = BottomSheetDialog(this, R.style.TransparentBottomSheetDialog)
        val view = layoutInflater.inflate(R.layout.tanamcareresult, null)
        dialog.setContentView(view)

        // Hubungkan secara manual dengan ID yang ada di tanamcareresult.xml
        val tvTitle = view.findViewById<TextView>(R.id.tvDiseaseTitle)
        val tvExplanation = view.findViewById<TextView>(R.id.tvExplanation)
        val tvSolution = view.findViewById<TextView>(R.id.tvSolution)
        val tvConfidence = view.findViewById<TextView>(R.id.tvConfidence)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBarConfidence)

        var title = "Hasil Analisa"
        var explanation = ""
        var solution = ""
        var score = 85

        // Membersihkan format teks dari AI
        val cleanText = resultText.replace("**", "").replace("*", "")

        if (cleanText.contains("Nama Penyakit:", ignoreCase = true)) {
            try {
                title = cleanText.substringAfter("Nama Penyakit:").substringBefore("Penjelasan:").trim()
                explanation = cleanText.substringAfter("Penjelasan:").substringBefore("Skor:").trim()
                score = cleanText.substringAfter("Skor:").substringBefore("Solusi").trim().filter { it.isDigit() }.toIntOrNull() ?: 85
                solution = cleanText.substringAfter("Solusi dari Tanamin:").trim()
            } catch (e: Exception) {
                explanation = cleanText
            }
        } else {
            // Logika untuk non-tanaman (Pesan santai Tanamin)
            title = "Waduh! 🤔"
            explanation = cleanText
            solution = "Coba foto tanaman kamu yang lebih jelas ya, biar Tanamin bisa bantu analisa! ✨"
            score = 0
        }

        // Mengisi data ke UI
        tvTitle.text = title
        tvExplanation.text = explanation
        tvSolution.text = solution
        tvConfidence.text = "$score%"
        progressBar.progress = score

        // Simpan ke history hanya jika itu tanaman
        if (cleanText.contains("Nama Penyakit:", ignoreCase = true)) {
            val historyManager = HistoryManager(this)
            historyManager.saveHistory(title, explanation, solution)
        }

        dialog.show()
    }

    private fun showLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.btnAnalyze.visibility = View.INVISIBLE
            binding.progressBarLoading.visibility = View.VISIBLE
            scannerAnimator?.pause()
        } else {
            binding.btnAnalyze.visibility = View.VISIBLE
            binding.progressBarLoading.visibility = View.GONE
            scannerAnimator?.resume()
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
        scannerAnimator?.cancel()
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }
}