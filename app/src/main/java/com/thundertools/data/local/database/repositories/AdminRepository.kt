package com.thundertools.data.local.database.repositories

import com.thundertools.data.local.database.dao.AdminUserDao
import com.thundertools.data.local.database.entities.AdminUser
import com.thundertools.domain.utils.SecurityUtils
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AdminRepository @Inject constructor(
    private val adminUserDao: AdminUserDao
) {
    suspend fun createAdminUser(username: String, password: String) {
        val hashedPassword = SecurityUtils.hashPassword(password)
        val user = AdminUser(
            username = username,
            passwordHash = hashedPassword
        )
        adminUserDao.insert(user)
    }
    
    suspend fun getAdminUser(username: String): AdminUser? {
        return adminUserDao.getByUsername(username)
    }
    
    suspend fun getAdminCount(): Int {
        return adminUserDao.getCount()
    }
    
    fun getAllAdmins(): Flow<List<AdminUser>> {
        return adminUserDao.getAll()
    }
    
    suspend fun updateLastLogin(username: String) {
        val user = adminUserDao.getByUsername(username)
        user?.let {
            val updated = it.copy(lastLogin = System.currentTimeMillis())
            adminUserDao.update(updated)
        }
    }
}