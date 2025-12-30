package com.thundertools

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp  // <- IMPORTANTE: Esto falta
class ThunderToolsApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Inicializar Room Database
        AppDatabase.init(this)
    }
}