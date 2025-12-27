package com.maritza.thundertools.ui

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.maritza.thundertools.data.Preferences
import com.maritza.thundertools.databinding.ActivitySettingsBinding
import com.maritza.thundertools.domain.Emulators
import com.maritza.thundertools.domain.Expansions

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private val prefs by lazy { Preferences(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.emulatorSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, Emulators.all)
        binding.expansionSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, Expansions.all)

        binding.saveButton.setOnClickListener {
            prefs.saveSettings(
                emulator = binding.emulatorSpinner.selectedItem.toString(),
                expansion = binding.expansionSpinner.selectedItem.toString(),
                dbHost = binding.dbHost.text?.toString().orEmpty(),
                dbPort = binding.dbPort.text?.toString().orEmpty(),
                dbName = binding.dbName.text?.toString().orEmpty(),
                dbUser = binding.dbUser.text?.toString().orEmpty(),
                dbPass = binding.dbPass.text?.toString().orEmpty(),
                soapHost = binding.soapHost.text?.toString().orEmpty(),
                soapPort = binding.soapPort.text?.toString().orEmpty(),
                soapUser = binding.soapUser.text?.toString().orEmpty(),
                soapPass = binding.soapPass.text?.toString().orEmpty(),
            )
            Snackbar.make(binding.saveButton, "Ajustes guardados", Snackbar.LENGTH_SHORT).show()
            finish()
        }
    }
}