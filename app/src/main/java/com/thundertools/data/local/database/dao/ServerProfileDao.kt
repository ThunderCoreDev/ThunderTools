package com.thundertools.data.local.database.dao

import androidx.room.*
import com.thundertools.data.local.database.entities.ServerProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface ServerProfileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(profile: ServerProfile): Long
    
    @Update
    suspend fun update(profile: ServerProfile)
    
    @Delete
    suspend fun delete(profile: ServerProfile)
    
    @Query("SELECT * FROM server_profiles ORDER BY lastUsed DESC")
    fun getAll(): Flow<List<ServerProfile>>
    
    @Query("SELECT * FROM server_profiles WHERE id = :id")
    suspend fun getById(id: Int): ServerProfile?
    
    @Query("SELECT * FROM server_profiles WHERE isDefault = 1 LIMIT 1")
    suspend fun getDefault(): ServerProfile?
    
    @Query("UPDATE server_profiles SET isDefault = 0")
    suspend fun clearDefault()
    
    @Query("SELECT COUNT(*) FROM server_profiles")
    suspend fun getCount(): Int
}