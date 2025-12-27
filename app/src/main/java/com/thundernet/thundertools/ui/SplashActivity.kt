package com.maritza.thundertools.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.maritza.thundertools.data.Preferences
import com.maritza.thundertools.databinding.ActivitySplashBinding
import com.maritza.thundertools.domain.AdminModules

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    private val prefs by lazy { Preferences(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.version.text = "Versión 1.0.0 • © Maritza"

        Thread {
            // Simular carga de módulos
            AdminModules.modules.keys.size // touch
            runOnUiThread {
                if (prefs.isFirstRun()) {
                    startActivity(Intent(this, LoginActivity::class.java).apply {
                        putExtra("setup", true)
                    })
                } else {
                    startActivity(Intent(this, LoginActivity::class.java))
                }
                finish()
            }
        }.start()
    }
}