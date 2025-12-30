package com.thundertools.data.local.database.dao

import androidx.room.*
import com.thundertools.data.local.database.entities.AdminUser
import kotlinx.coroutines.flow.Flow

@Dao
interface AdminUserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: AdminUser)
    
    @Update
    suspend fun update(user: AdminUser)
    
    @Query("SELECT * FROM admin_users WHERE username = :username")
    suspend fun getByUsername(username: String): AdminUser?
    
    @Query("SELECT COUNT(*) FROM admin_users")
    suspend fun getCount(): Int
    
    @Query("SELECT * FROM admin_users")
    fun getAll(): Flow<List<AdminUser>>
    
    @Query("DELETE FROM admin_users WHERE username = :username")
    suspend fun delete(username: String)
}