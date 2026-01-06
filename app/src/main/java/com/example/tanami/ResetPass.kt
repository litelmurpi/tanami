package com.example.tanami

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.tanami.models.ResetPasswordRequest
import com.example.tanami.network.RetrofitClient
import kotlinx.coroutines.launch

class ResetPass : AppCompatActivity() {

    private lateinit var btnBack: ImageView
    private lateinit var etPasswordBaru: EditText
    private lateinit var etKonfirmasiPasswordBaru: EditText
    private lateinit var btnResetPassword: Button

    private var email: String = ""
    private var resetToken: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.resetpass)

        // Get data from intent
        email = intent.getStringExtra("email") ?: ""
        resetToken = intent.getStringExtra("reset_token") ?: ""

        // Initialize views
        initViews()

        // Set click listeners
        btnBack.setOnClickListener {
            finish()
        }

        btnResetPassword.setOnClickListener {
            handleResetPassword()
        }
    }

    private fun initViews() {
        btnBack = findViewById(R.id.btn_back_reset)
        etPasswordBaru = findViewById(R.id.et_password_baru)
        etKonfirmasiPasswordBaru = findViewById(R.id.et_konfirmasi_password_baru)
        btnResetPassword = findViewById(R.id.btn_reset_password)
    }

    private fun handleResetPassword() {
        // Get input values
        val password = etPasswordBaru.text.toString()
        val passwordConfirmation = etKonfirmasiPasswordBaru.text.toString()

        // Validate inputs
        if (!validateInputs(password, passwordConfirmation)) {
            return
        }

        // Call API to reset password
        performResetPassword(password, passwordConfirmation)
    }

    private fun validateInputs(password: String, passwordConfirmation: String): Boolean {
        // Check empty password
        if (password.isEmpty()) {
            etPasswordBaru.error = "Password tidak boleh kosong"
            etPasswordBaru.requestFocus()
            return false
        }

        // Check password length
        if (password.length < 6) {
            etPasswordBaru.error = "Password minimal 6 karakter"
            etPasswordBaru.requestFocus()
            return false
        }

        // Check empty confirmation
        if (passwordConfirmation.isEmpty()) {
            etKonfirmasiPasswordBaru.error = "Konfirmasi password tidak boleh kosong"
            etKonfirmasiPasswordBaru.requestFocus()
            return false
        }

        // Check password match
        if (password != passwordConfirmation) {
            etKonfirmasiPasswordBaru.error = "Password tidak sama"
            etKonfirmasiPasswordBaru.requestFocus()
            return false
        }

        return true
    }

    private fun performResetPassword(password: String, passwordConfirmation: String) {
        // Disable button
        btnResetPassword.isEnabled = false
        btnResetPassword.text = "Mengatur ulang..."

        // Create request
        val resetRequest = ResetPasswordRequest(
            email = email,
            reset_token = resetToken,
            password = password,
            password_confirmation = passwordConfirmation
        )

        // Call API
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.resetPassword(resetRequest)

                if (response.isSuccessful) {
                    val resetResponse = response.body()

                    if (resetResponse?.success == true) {
                        // Password reset successful - Show Success Dialog
                        showSuccessDialog()

                    } else {
                        // API returned success: false
                        Toast.makeText(
                            this@ResetPass,
                            resetResponse?.message ?: "Reset password gagal",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    // HTTP error
                    Toast.makeText(
                        this@ResetPass,
                        "Error: ${response.code()} - ${response.message()}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                // Network error
                Toast.makeText(
                    this@ResetPass,
                    "Terjadi kesalahan: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
                e.printStackTrace()
            } finally {
                // Re-enable button
                btnResetPassword.isEnabled = true
                btnResetPassword.text = "Atur ulang kata sandi"
            }
        }
    }

    private fun showSuccessDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_success, null)

        // Ubah title jadi "Password Changed!"
        val tvTitle = dialogView.findViewById<TextView>(R.id.tv_success_title)
        tvTitle.text = "Password Changed!"

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        // Set background transparent
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialog.show()

        // Set dialog width & height
        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.85).toInt(), // 85% lebar layar
            WindowManager.LayoutParams.WRAP_CONTENT
        )

        // Auto dismiss after 2.5 seconds, then navigate to Login
        Handler(Looper.getMainLooper()).postDelayed({
            dialog.dismiss()
            navigateToLogin()
        }, 2500)
    }

    private fun navigateToLogin() {
        val intent = Intent(this, Login::class.java)
        // Clear all previous activities
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}