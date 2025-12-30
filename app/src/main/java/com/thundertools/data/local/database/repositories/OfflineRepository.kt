package com.thundertools.data.local.database.repositories

import com.thundertools.data.local.database.dao.OfflineDataDao
import com.thundertools.data.local.database.entities.OfflineData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class OfflineRepository @Inject constructor(
    private val offlineDataDao: OfflineDataDao
) {
    suspend fun saveOfflineData(moduleId: String, dataJson: String) {
        val offlineData = OfflineData(
            moduleId = moduleId,
            dataJson = dataJson,
            lastUpdated = System.currentTimeMillis()
        )
        offlineDataDao.insert(offlineData)
    }
    
    suspend fun getOfflineData(moduleId: String): OfflineData? {
        return offlineDataDao.getByModule(moduleId)
    }
    
    fun getAllOfflineData(): Flow<List<OfflineData>> {
        return offlineDataDao.getAll()
    }
    
    suspend fun deleteOfflineData(moduleId: String) {
        offlineDataDao.deleteByModule(moduleId)
    }
    
    suspend fun clearAllOfflineData() {
        offlineDataDao.clearAll()
    }
}