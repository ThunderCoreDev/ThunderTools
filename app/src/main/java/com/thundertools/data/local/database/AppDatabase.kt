package com.thundertools.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.thundertools.data.local.database.dao.*
import com.thundertools.data.local.database.entities.*

@Database(
    entities = [
        AdminUser::class,
        ServerProfile::class,
        OfflineData::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun adminUserDao(): AdminUserDao
    abstract fun serverProfileDao(): ServerProfileDao
    abstract fun offlineDataDao(): OfflineDataDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        fun init(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "thundertools_db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
        
        fun getInstance(): AppDatabase {
            return INSTANCE ?: throw IllegalStateException("Database not initialized")
        }
    }
}