package com.thundertools.data.local.database.dao

import androidx.room.*
import com.thundertools.data.local.database.entities.OfflineData
import kotlinx.coroutines.flow.Flow

@Dao
interface OfflineDataDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(data: OfflineData)
    
    @Update
    suspend fun update(data: OfflineData)
    
    @Query("SELECT * FROM offline_data WHERE moduleId = :moduleId")
    suspend fun getByModule(moduleId: String): OfflineData?
    
    @Query("SELECT * FROM offline_data")
    fun getAll(): Flow<List<OfflineData>>
    
    @Query("DELETE FROM offline_data WHERE moduleId = :moduleId")
    suspend fun deleteByModule(moduleId: String)
    
    @Query("DELETE FROM offline_data")
    suspend fun clearAll()
}