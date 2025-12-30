package com.maritza.thundertools.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.maritza.thundertools.data.Preferences
import com.maritza.thundertools.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val prefs by lazy { Preferences(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val setup = intent.getBooleanExtra("setup", false)
        if (setup) {
            binding.title.text = getString(com.maritza.thundertools.R.string.login_setup_title)
        }

        binding.loginBtn.setOnClickListener {
            val user = binding.user.text?.toString().orEmpty()
            val pass = binding.pass.text?.toString().orEmpty()
            if (user.isBlank() || pass.isBlank()) {
                Snackbar.make(binding.root, "Completa usuario y contraseña", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (setup) {
                prefs.completeFirstRun(user, pass)
            } else {
                val saved = prefs.getAll()
                if (user != saved["admin_user"] || pass != saved["admin_pass"]) {
                    Snackbar.make(binding.root, "Credenciales inválidas", Snackbar.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}