package com.thundertools.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thundertools.data.local.database.repositories.ServerRepository
import com.thundertools.data.models.DatabaseConfig
import com.thundertools.data.models.EmuType
import com.thundertools.data.models.Expansion
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
    
    data class ServerProfileState(
        val id: Int = 0,
        val name: String = "Mi Servidor",
        val host: String = "localhost",
        val port: Int = 8085,
        val emulatorType: EmuType = EmuType.AZEROTHCORE,
        val expansion: Expansion = Expansion.WOTLK,
        val authConfig: DatabaseConfig = DatabaseConfig(database = "auth"),
        val charactersConfig: DatabaseConfig = DatabaseConfig(database = "characters"),
        val worldConfig: DatabaseConfig = DatabaseConfig(database = "world"),
        val isDefault: Boolean = true
    )
    
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