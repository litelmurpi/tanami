package com.example.tanami

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.example.tanami.models.LoginRequest
import com.example.tanami.network.RetrofitClient
import kotlinx.coroutines.launch

class Login : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvForgotPassword: TextView
    private lateinit var tvGoToSignup: TextView

    // SessionManager
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {

        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)

        // Initialize SessionManager
        sessionManager = SessionManager(this)

        // Check if user already logged in
        if (sessionManager.isLoggedIn()) {
            navigateToDashboard()
            return
        }

        // Initialize views
        initViews()

        // Set click listeners
        btnLogin.setOnClickListener {
            handleLogin()
        }

        tvGoToSignup.setOnClickListener {
            navigateToSignup()
        }

        tvForgotPassword.setOnClickListener {
            navigateToForgotPassword()
        }
    }

    private fun initViews() {
        etEmail = findViewById(R.id.et_email_login)
        etPassword = findViewById(R.id.et_password_login)
        btnLogin = findViewById(R.id.btn_login)
        tvForgotPassword = findViewById(R.id.tv_forgot_password)
        tvGoToSignup = findViewById(R.id.tv_go_to_signup)
    }

    private fun handleLogin() {
        // Get input values
        val email = etEmail.text.toString().trim()
        val password = etPassword.text.toString()

        // Validate inputs
        if (!validateInputs(email, password)) {
            return
        }

        // Call API
        performLogin(email, password)
    }

    private fun validateInputs(email: String, password: String): Boolean {
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

        // Check empty password
        if (password.isEmpty()) {
            etPassword.error = "Password tidak boleh kosong"
            etPassword.requestFocus()
            return false
        }

        return true
    }

    private fun performLogin(email: String, password: String) {
        // Disable button to prevent double click
        btnLogin.isEnabled = false
        btnLogin.text = "Loading..."

        // Create request body
        val loginRequest = LoginRequest(
            email = email,
            password = password
        )

        // Call API using coroutines
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.login(loginRequest)

                if (response.isSuccessful) {
                    val loginResponse = response.body()

                    if (loginResponse?.success == true) {
                        // Login successful
                        val userData = loginResponse.data

                        // Save user data using SessionManager
                        sessionManager.saveLoginSession(
                            userId = userData?.id ?: 0,
                            userName = userData?.nama ?: "",
                            userEmail = userData?.email ?: "",
                            token = userData?.token ?: ""
                        )

                        Toast.makeText(
                            this@Login,
                            "Login berhasil! Selamat datang ${userData?.nama}",
                            Toast.LENGTH_SHORT
                        ).show()

                        // Navigate to Dashboard
                        navigateToDashboard()

                    } else {
                        // API returned success: false
                        Toast.makeText(
                            this@Login,
                            loginResponse?.message ?: "Login gagal",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                } else {
                    // HTTP error
                    Toast.makeText(
                        this@Login,
                        "Error: ${response.code()} - ${response.message()}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                // Network error or other exception
                Toast.makeText(
                    this@Login,
                    "Terjadi kesalahan: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
                e.printStackTrace()
            } finally {
                // Re-enable button
                btnLogin.isEnabled = true
                btnLogin.text = "MASUK"
            }
        }
    }

    private fun navigateToDashboard() {
        val intent = Intent(this, Dashboard::class.java)
        startActivity(intent)
        finish() // Close login activity
    }

    private fun navigateToSignup() {
        try {
            val intent = Intent(this, SingUp::class.java)
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    private fun navigateToForgotPassword() {
        val intent = Intent(this, LupaSandi::class.java)
        startActivity(intent)
    }
}