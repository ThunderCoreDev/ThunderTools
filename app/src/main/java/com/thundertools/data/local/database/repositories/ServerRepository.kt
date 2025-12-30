package com.thundertools.data.local.database.repositories

import com.thundertools.data.local.database.dao.ServerProfileDao
import com.thundertools.data.local.database.entities.ServerProfile
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ServerRepository @Inject constructor(
    private val serverProfileDao: ServerProfileDao
) {
    suspend fun saveProfile(profile: ServerProfile): Long {
        return serverProfileDao.insert(profile)
    }
    
    suspend fun updateProfile(profile: ServerProfile) {
        serverProfileDao.update(profile)
    }
    
    suspend fun deleteProfile(profile: ServerProfile) {
        serverProfileDao.delete(profile)
    }
    
    fun getAllProfiles(): Flow<List<ServerProfile>> {
        return serverProfileDao.getAll()
    }
    
    suspend fun getProfileById(id: Int): ServerProfile? {
        return serverProfileDao.getById(id)
    }
    
    suspend fun setAsDefault(profile: ServerProfile) {
        serverProfileDao.clearDefault()
        val updated = profile.copy(isDefault = true)
        serverProfileDao.update(updated)
    }
    
    suspend fun getDefaultProfile(): ServerProfile? {
        return serverProfileDao.getDefault()
    }
}