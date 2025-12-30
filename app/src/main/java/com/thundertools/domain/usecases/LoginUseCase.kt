package com.thundertools.domain.usecases

import com.thundertools.data.local.database.repositories.AdminRepository
import com.thundertools.domain.utils.SecurityUtils
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val adminRepository: AdminRepository
) {
    suspend fun execute(username: String, password: String): Boolean {
        val user = adminRepository.getAdminUser(username)
        return if (user != null) {
            val hashedPassword = SecurityUtils.hashPassword(password)
            user.passwordHash == hashedPassword
        } else {
            false
        }
    }
}