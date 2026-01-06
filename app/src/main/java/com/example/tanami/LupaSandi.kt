package com.example.tanami

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.tanami.network.RetrofitClient
import kotlinx.coroutines.launch

class LupaSandi : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var etEmail: EditText
    private lateinit var btnKirimCode: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.lupasandi)

        // Initialize views
        initViews()

        // Set click listeners
        btnBack.setOnClickListener {
            finish() // Kembali ke halaman sebelumnya
        }

        btnKirimCode.setOnClickListener {
            handleForgotPassword()
        }
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btn_back_lupa_sandi)
        etEmail = findViewById(R.id.et_email_lupa_sandi)
        btnKirimCode = findViewById(R.id.btn_kirim_code)
    }

    private fun handleForgotPassword() {
        // Get email input
        val email = etEmail.text.toString().trim()

        // Validate email
        if (!validateEmail(email)) {
            return
        }

        // Call API to send OTP
        sendOtpCode(email)
    }

    private fun validateEmail(email: String): Boolean {
        // Check empty email
        if (email.isEmpty()) {
            etEmail.error = "Email tidak boleh kosong"
            etEmail.requestFocus()
            return false
        }

        // Validate email format
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = "Format email tidak valid"
            etEmail.requestFocus()
            return false
        }

        return true
    }

    private fun sendOtpCode(email: String) {
        // Disable button
        btnKirimCode.isEnabled = false
        btnKirimCode.text = "Mengirim..."

        // Create request body (hanya email)
        val requestBody = mapOf("email" to email)

        // Call API
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.forgotPassword(requestBody)

                if (response.isSuccessful) {
                    val forgotPasswordResponse = response.body()

                    if (forgotPasswordResponse?.success == true) {
                        // OTP sent successfully
                        Toast.makeText(
                            this@LupaSandi,
                            "Kode verifikasi telah dikirim ke email Anda",
                            Toast.LENGTH_SHORT
                        ).show()

                        // Navigate to KodeVerifikasi
                        navigateToVerification(email)

                    } else {
                        // API returned success: false
                        Toast.makeText(
                            this@LupaSandi,
                            forgotPasswordResponse?.message ?: "Email tidak terdaftar",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    // HTTP error
                    Toast.makeText(
                        this@LupaSandi,
                        "Error: ${response.code()} - ${response.message()}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                // Network error
                Toast.makeText(
                    this@LupaSandi,
                    "Terjadi kesalahan: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
                e.printStackTrace()
            } finally {
                // Re-enable button
                btnKirimCode.isEnabled = true
                btnKirimCode.text = "Kirim Code"
            }
        }
    }

    private fun navigateToVerification(email: String) {
        val intent = Intent(this, KodeVerifikasi::class.java)
        intent.putExtra("email", email) // Pass email ke screen berikutnya
        startActivity(intent)
    }
}