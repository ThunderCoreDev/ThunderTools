package com.maritza.thundertools.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.maritza.thundertools.R
import com.maritza.thundertools.data.OfflineGuard
import com.maritza.thundertools.databinding.ActivityMainBinding
import com.maritza.thundertools.domain.AdminModules
import com.maritza.thundertools.domain.ModuleType

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var offline = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    true
                }
                else -> false
            }
        }

        offline = OfflineGuard.isOffline(this)
        if (offline) {
            Snackbar.make(binding.modulesList, getString(R.string.offline_mode), Snackbar.LENGTH_LONG).show()
        }

        val items = buildItems()
        val adapter = ModuleAdapter(items) { item ->
            if (offline && item.requiresOnline) {
                Snackbar.make(binding.modulesList, "Acción requiere conexión", Snackbar.LENGTH_SHORT).show()
            } else {
                Dialogs.showActionDialog(this, item) { args ->
                    // Aquí se llamaría SoapClient.execute(...)
                    Snackbar.make(binding.modulesList, "Comando enviado: ${item.commandKey}", Snackbar.LENGTH_SHORT).show()
                }
            }
        }

        binding.modulesList.layoutManager = LinearLayoutManager(this)
        binding.modulesList.adapter = adapter
    }

    private fun buildItems(): List<UiActionItem> {
        fun map(type: ModuleType) = AdminModules.modules[type]!!.map { a ->
            UiActionItem(type, a.label, a.commandKey, a.requiresOnline)
        }
        return map(ModuleType.ACCOUNTS) +
               map(ModuleType.CHARACTERS) +
               map(ModuleType.WORLD) +
               map(ModuleType.ECONOMY) +
               map(ModuleType.SUPPORT)
    }
}