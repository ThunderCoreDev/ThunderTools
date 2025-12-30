package com.thundertools.data.local.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "offline_data")
data class OfflineData(
    @PrimaryKey
    val moduleId: String,
    val dataJson: String = "{}",
    val lastUpdated: Long = System.currentTimeMillis(),
    val dataType: String = "json"
)