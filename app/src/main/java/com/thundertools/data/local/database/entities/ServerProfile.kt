package com.thundertools.data.local.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.thundertools.data.models.DatabaseConfig
import com.thundertools.data.models.EmuType
import com.thundertools.data.models.Expansion

@Entity(tableName = "server_profiles")
data class ServerProfile(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String = "",
    val host: String = "localhost",
    val port: Int = 8085,
    val emulatorType: EmuType = EmuType.AZEROTHCORE,
    val expansion: Expansion = Expansion.WOTLK,
    val authConfig: DatabaseConfig = DatabaseConfig(),
    val charactersConfig: DatabaseConfig = DatabaseConfig(),
    val worldConfig: DatabaseConfig = DatabaseConfig(),
    val isDefault: Boolean = false,
    val lastUsed: Long = System.currentTimeMillis()
)