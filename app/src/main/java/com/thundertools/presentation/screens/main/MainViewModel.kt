package com.thundertools.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val _isOnline = MutableStateFlow(false)
    val isOnline: StateFlow<Boolean> = _isOnline
    
    private val _connectionStatus = MutableStateFlow("Verificando conexión...")
    val connectionStatus: StateFlow<String> = _connectionStatus
    
    init {
        checkConnection()
    }
    
    fun checkConnection() {
        viewModelScope.launch {
            // Aquí iría la lógica para verificar conexión al servidor
            // Por ahora simulamos
            _isOnline.value = true
            _connectionStatus.value = "Conectado al servidor principal"
        }
    }
    
    fun showOfflineMessage() {
        // Mostrar snackbar o diálogo
        _connectionStatus.value = "Esta función requiere conexión al servidor"
    }
}