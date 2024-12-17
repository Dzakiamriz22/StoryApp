package com.example.storyapp.ui.auth

import android.animation.ObjectAnimator
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.storyapp.R
import com.example.storyapp.ui.MainActivity
import com.example.storyapp.data.model.auth.LoginRequest
import com.example.storyapp.data.model.auth.LoginResponse
import com.example.storyapp.databinding.ActivityLoginBinding
import com.example.storyapp.data.remote.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences("session", MODE_PRIVATE)

        // Jika pengguna sudah login, langsung ke MainActivity
        if (isLoggedIn()) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        // Jalankan animasi
        playAnimation()

        // Siapkan aksi tombol
        setupAction()
    }

    private fun playAnimation() {
        // Animasi Title
        ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 0f, 1f).apply {
            duration = 1000 // Durasi animasi 1 detik
            start()
        }

        // Animasi Email Input
        ObjectAnimator.ofFloat(binding.emailInputLayout, View.TRANSLATION_Y, 300f, 0f).apply {
            duration = 1000
            start()
        }

        // Animasi Password Input
        ObjectAnimator.ofFloat(binding.passwordInputLayout, View.TRANSLATION_Y, 300f, 0f).apply {
            duration = 1000
            start()
        }

        // Animasi Login Button
        ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 0f, 1f).apply {
            duration = 1000
            start()
        }

        // Animasi Register TextView
        ObjectAnimator.ofFloat(binding.tvRegister, View.ALPHA, 0f, 1f).apply {
            duration = 1200 // Sedikit lebih lambat untuk efek tertunda
            start()
        }
    }

    private fun setupAction() {
        // Klik tombol Login
        binding.btnLogin.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()

            when {
                email.isEmpty() -> {
                    binding.edLoginEmail.error = "Email harus diisi"
                    binding.edLoginEmail.requestFocus()
                }
                !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                    binding.edLoginEmail.error = "Format email tidak valid"
                    binding.edLoginEmail.requestFocus()
                }
                password.isEmpty() -> {
                    binding.edLoginPassword.error = "Password harus diisi"
                    binding.edLoginPassword.requestFocus()
                }
                password.length < 8 -> {
                    binding.edLoginPassword.error = "Password minimal 8 karakter"
                    binding.edLoginPassword.requestFocus()
                }
                else -> {
                    loginUser(email, password)
                }
            }
        }

        // Klik "Belum punya akun?" untuk RegisterActivity
        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun isLoggedIn(): Boolean {
        return !sharedPreferences.getString("token", "").isNullOrEmpty()
    }

    private fun loginUser(email: String, password: String) {
        showLoading(true)
        val loginRequest = LoginRequest(email, password)
        val call = ApiClient.apiInterface.loginUser(loginRequest)

        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                showLoading(false)
                if (response.isSuccessful) {
                    response.body()?.let { loginResponse ->
                        if (!loginResponse.error) {
                            sharedPreferences.edit().apply {
                                putString("token", loginResponse.loginResult.token)
                                putString("userId", loginResponse.loginResult.userId)
                                putString("name", loginResponse.loginResult.name)
                                apply()
                            }

                            startActivity(Intent(this@LoginActivity, MainActivity::class.java).apply {
                                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            })
                            finish()
                        } else {
                            Toast.makeText(this@LoginActivity, loginResponse.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this@LoginActivity, getString(R.string.login_failed), Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                showLoading(false)
                Toast.makeText(this@LoginActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}