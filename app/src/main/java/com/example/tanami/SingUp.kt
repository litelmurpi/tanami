package com.example.tanami

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.tanami.models.SignupRequest
import com.example.tanami.network.RetrofitClient
import kotlinx.coroutines.launch

class SingUp : AppCompatActivity() {

    private lateinit var etNama: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etPasswordConfirmation: EditText
    private lateinit var btnSignup: Button
    private lateinit var tvGoToLogin: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.singup)

        // Initialize views
        initViews()

        // Set click listeners
        btnSignup.setOnClickListener {
            handleSignup()
        }

        tvGoToLogin.setOnClickListener {
            navigateToLogin()
        }
    }

    private fun initViews() {
        etNama = findViewById(R.id.et_nama_signup)
        etEmail = findViewById(R.id.et_email_signup)
        etPassword = findViewById(R.id.et_password_signup)
        etPasswordConfirmation = findViewById(R.id.et_password_confirmation_signup)
        btnSignup = findViewById(R.id.btn_signup)
        tvGoToLogin = findViewById(R.id.tv_go_to_login)
    }

    private fun handleSignup() {
        // Get input values
        val nama = etNama.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString()
        val passwordConfirmation = etPasswordConfirmation.text.toString()

        // Validate inputs
        if (!validateInputs(nama, email, password, passwordConfirmation)) {
            return
        }

        // Call API
        performSignup(nama, email, password, passwordConfirmation)
    }

    private fun validateInputs(
        nama: String,
        email: String,
        password: String,
        passwordConfirmation: String
    ): Boolean {
        // Check empty fields
        if (nama.isEmpty()) {
            etNama.error = "Nama tidak boleh kosong"
            etNama.requestFocus()
            return false
        }

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

        if (password.isEmpty()) {
            etPassword.error = "Password tidak boleh kosong"
            etPassword.requestFocus()
            return false
        }

        // Validate password length
        if (password.length < 6) {
            etPassword.error = "Password minimal 6 karakter"
            etPassword.requestFocus()
            return false
        }

        if (passwordConfirmation.isEmpty()) {
            etPasswordConfirmation.error = "Konfirmasi password tidak boleh kosong"
            etPasswordConfirmation.requestFocus()
            return false
        }

        // Check password match
        if (password != passwordConfirmation) {
            etPasswordConfirmation.error = "Password tidak sama"
            etPasswordConfirmation.requestFocus()
            return false
        }

        return true
    }

    private fun performSignup(
        nama: String,
        email: String,
        password: String,
        passwordConfirmation: String
    ) {
        btnSignup.isEnabled = false
        btnSignup.text = "Loading..."

        val signupRequest = SignupRequest(
            nama = nama,
            email = email,
            password = password,
            password_confirmation = passwordConfirmation
        )

        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.signup(signupRequest)

                if (response.isSuccessful) {
                    val signupResponse = response.body()

                    if (signupResponse?.success == true) {
                        // Signup successful - Show Success Dialog
                        showSuccessDialog(
                            title = "Account Created!",
                            message = "Welcome to Tanami"
                        )

                    } else {
                        Toast.makeText(
                            this@SingUp,
                            signupResponse?.message ?: "Registrasi gagal",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@SingUp,
                        "Error: ${response.code()} - ${response.message()}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@SingUp,
                    "Terjadi kesalahan: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
                e.printStackTrace()
            } finally {
                btnSignup.isEnabled = true
                btnSignup.text = "Daftar"
            }
        }
    }

    private fun showSuccessDialog(title: String, message: String) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_success, null)

        val tvTitle = dialogView.findViewById<TextView>(R.id.tv_success_title)
        tvTitle.text = title

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()

        // Auto dismiss after 2.5 seconds, then navigate to Login
        Handler(Looper.getMainLooper()).postDelayed({
            dialog.dismiss()
            navigateToLogin()
        }, 2500)
    }

    private fun navigateToLogin() {
        val intent = Intent(this, Login::class.java)
        startActivity(intent)
        finish() // Close signup activity
    }
}