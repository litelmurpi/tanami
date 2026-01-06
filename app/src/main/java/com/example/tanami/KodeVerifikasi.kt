package com.example.tanami

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.example.tanami.models.VerifyCodeRequest
import com.example.tanami.network.RetrofitClient
import kotlinx.coroutines.launch

class KodeVerifikasi : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var otp1: EditText
    private lateinit var otp2: EditText
    private lateinit var otp3: EditText
    private lateinit var otp4: EditText
    private lateinit var otp5: EditText
    private lateinit var tvCountdown: TextView
    private lateinit var btnVerifikasi: Button

    private var email: String = ""
    private var countDownTimer: CountDownTimer? = null
    private var resetToken: String = "" // Token untuk reset password

    override fun onCreate(savedInstanceState: Bundle?) {

        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.kodeverifikasi)

        // Get email from intent
        email = intent.getStringExtra("email") ?: ""

        // Initialize views
        initViews()

        // Setup OTP auto focus
        setupOtpInputs()

        // Start countdown
        startCountdown()

        // Set click listeners
        btnBack.setOnClickListener {
            finish()
        }

        tvCountdown.setOnClickListener {
            if (tvCountdown.isEnabled) {
                resendOtp()
            }
        }

        btnVerifikasi.setOnClickListener {
            handleVerification()
        }
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btn_back_verifikasi)
        otp1 = findViewById(R.id.otp1)
        otp2 = findViewById(R.id.otp2)
        otp3 = findViewById(R.id.otp3)
        otp4 = findViewById(R.id.otp4)
        otp5 = findViewById(R.id.otp5)
        tvCountdown = findViewById(R.id.tv_countdown)
        btnVerifikasi = findViewById(R.id.btn_verifikasi)
    }

    private fun setupOtpInputs() {
        // Auto focus ke kotak berikutnya
        otp1.addTextChangedListener(OtpTextWatcher(otp1, otp2))
        otp2.addTextChangedListener(OtpTextWatcher(otp2, otp3))
        otp3.addTextChangedListener(OtpTextWatcher(otp3, otp4))
        otp4.addTextChangedListener(OtpTextWatcher(otp4, otp5))
        otp5.addTextChangedListener(OtpTextWatcher(otp5, null))

        // Handle backspace untuk kembali ke kotak sebelumnya
        otp2.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN && otp2.text.isEmpty()) {
                otp1.requestFocus()
            }
            false
        }

        otp3.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN && otp3.text.isEmpty()) {
                otp2.requestFocus()
            }
            false
        }

        otp4.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN && otp4.text.isEmpty()) {
                otp3.requestFocus()
            }
            false
        }

        otp5.setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN && otp5.text.isEmpty()) {
                otp4.requestFocus()
            }
            false
        }
    }

    // Inner class untuk handle auto focus OTP
    inner class OtpTextWatcher(
        private val currentView: EditText,
        private val nextView: EditText?
    ) : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        override fun afterTextChanged(s: Editable?) {
            val text = s.toString()
            if (text.length == 1) {
                nextView?.requestFocus()
            }
        }
    }

    private fun startCountdown() {
        tvCountdown.isEnabled = false
        tvCountdown.setTextColor(Color.GRAY)

        countDownTimer = object : CountDownTimer(30000, 1000) { // 30 detik
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000
                tvCountdown.text = "Kirim Ulang : 00:${String.format("%02d", seconds)}"
            }

            override fun onFinish() {
                tvCountdown.text = "Kirim Ulang Kode"
                tvCountdown.isEnabled = true
                tvCountdown.setTextColor(Color.parseColor("#54BD10"))
            }
        }
        countDownTimer?.start()
    }

    private fun resendOtp() {
        // Disable countdown text
        tvCountdown.isEnabled = false
        tvCountdown.text = "Mengirim..."

        // Call API to resend OTP
        val requestBody = mapOf("email" to email)

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.forgotPassword(requestBody)

                if (response.isSuccessful && response.body()?.success == true) {
                    Toast.makeText(
                        this@KodeVerifikasi,
                        "Kode verifikasi telah dikirim ulang",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Clear OTP inputs
                    clearOtpInputs()

                    // Restart countdown
                    startCountdown()
                } else {
                    Toast.makeText(
                        this@KodeVerifikasi,
                        "Gagal mengirim ulang kode",
                        Toast.LENGTH_SHORT
                    ).show()
                    tvCountdown.text = "Kirim Ulang Kode"
                    tvCountdown.isEnabled = true
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@KodeVerifikasi,
                    "Terjadi kesalahan: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
                e.printStackTrace()
            }
        }
    }

    private fun clearOtpInputs() {
        otp1.text.clear()
        otp2.text.clear()
        otp3.text.clear()
        otp4.text.clear()
        otp5.text.clear()
        otp1.requestFocus()
    }

    private fun handleVerification() {
        // Get OTP code from all 5 inputs
        val code = otp1.text.toString() +
                otp2.text.toString() +
                otp3.text.toString() +
                otp4.text.toString() +
                otp5.text.toString()

        // Validate OTP
        if (code.length != 5) {
            Toast.makeText(this, "Masukkan kode verifikasi lengkap", Toast.LENGTH_SHORT).show()
            return
        }

        // Verify OTP
        verifyOtpCode(code)
    }

    private fun verifyOtpCode(code: String) {
        // Disable button
        btnVerifikasi.isEnabled = false
        btnVerifikasi.text = "Memverifikasi..."

        // Create request
        val verifyRequest = VerifyCodeRequest(
            email = email,
            code = code
        )

        // Call API
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.verifyCode(verifyRequest)

                if (response.isSuccessful) {
                    val verifyResponse = response.body()

                    if (verifyResponse?.success == true) {
                        // OTP verified successfully
                        resetToken = verifyResponse.resetToken ?: ""

                        Toast.makeText(
                            this@KodeVerifikasi,
                            "Kode verifikasi berhasil!",
                            Toast.LENGTH_SHORT
                        ).show()

                        // Navigate to ResetPass
                        navigateToResetPassword()

                    } else {
                        // Wrong OTP
                        Toast.makeText(
                            this@KodeVerifikasi,
                            verifyResponse?.message ?: "Kode verifikasi salah",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@KodeVerifikasi,
                        "Error: ${response.code()}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@KodeVerifikasi,
                    "Terjadi kesalahan: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
                e.printStackTrace()
            } finally {
                // Re-enable button
                btnVerifikasi.isEnabled = true
                btnVerifikasi.text = "Verifikasi"
            }
        }
    }

    private fun navigateToResetPassword() {
        val intent = Intent(this, ResetPass::class.java)
        intent.putExtra("email", email)
        intent.putExtra("reset_token", resetToken)
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Cancel countdown timer
        countDownTimer?.cancel()
    }
}