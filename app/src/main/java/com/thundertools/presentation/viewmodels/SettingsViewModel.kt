package com.thundertools.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thundertools.data.local.database.repositories.ServerRepository
import com.thundertools.data.models.*  // Importar todos los modelos
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    private val serverRepository: ServerRepository
) : ViewModel() {
    
    private val _currentProfile = MutableStateFlow<ServerProfileState?>(null)
    val currentProfile: StateFlow<ServerProfileState?> = _currentProfile
    
    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving
    
    init {
        loadDefaultProfile()
    }
    
    private fun loadDefaultProfile() {
        viewModelScope.launch {
            val profile = serverRepository.getDefaultProfile()
            _currentProfile.value = profile?.toState() ?: ServerProfileState()
        }
    }
    
    fun updateProfileState(state: ServerProfileState) {
        _currentProfile.value = state
    }
    
    fun saveProfile() {
        viewModelScope.launch {
            _isSaving.value = true
            try {
                val state = _currentProfile.value ?: return@launch
                val profile = state.toEntity()
                serverRepository.saveProfile(profile)
                if (state.isDefault) {
                    serverRepository.setAsDefault(profile)
                }
            } finally {
                _isSaving.value = false
            }
        }
    }
    
    // Función para convertir de State a Entity
    private fun ServerProfileState.toEntity() = com.thundertools.data.local.database.entities.ServerProfile(
        id = id,
        name = name,
        host = host,
        port = port,
        emulatorType = emulatorType,
        expansion = expansion,
        authConfig = authConfig,
        charactersConfig = charactersConfig,
        worldConfig = worldConfig,
        isDefault = isDefault
    )
    
    // Función para convertir de Entity a State
    private fun com.thundertools.data.local.database.entities.ServerProfile.toState() = ServerProfileState(
        id = id,
        name = name,
        host = host,
        port = port,
        emulatorType = emulatorType,
        expansion = expansion,
        authConfig = authConfig,
        charactersConfig = charactersConfig,
        worldConfig = worldConfig,
        isDefault = isDefault
    )
}