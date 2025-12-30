package com.thundertools.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.thundertools.data.local.database.repositories.AdminRepository
import com.thundertools.domain.usecases.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val adminRepository: AdminRepository
) : ViewModel() {
    
    fun login(username: String, password: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                // Primero, verificar si es el primer inicio
                val isFirstTime = adminRepository.getAdminCount() == 0
                
                if (isFirstTime) {
                    // Crear cuenta admin inicial
                    adminRepository.createAdminUser(username, password)
                    callback(true)
                } else {
                    // Verificar credenciales
                    val isValid = loginUseCase.execute(username, password)
                    callback(isValid)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                callback(false)
            }
        }
    }
}