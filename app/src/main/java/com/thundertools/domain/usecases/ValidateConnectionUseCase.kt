package com.thundertools.domain.usecases

import com.thundertools.data.models.DatabaseConfig
import javax.inject.Inject

class ValidateConnectionUseCase @Inject constructor() {
    suspend fun execute(config: DatabaseConfig): Boolean {
        // Implementación de prueba de conexión a MySQL
        return try {
            // Aquí iría la lógica real de conexión
            // Por ahora simulamos éxito
            true
        } catch (e: Exception) {
            false
        }
    }
}