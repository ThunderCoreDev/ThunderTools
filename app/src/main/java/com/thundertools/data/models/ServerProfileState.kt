package com.thundertools.data.models

data class ServerProfileState(
    val id: Int = 0,
    val name: String = "Mi Servidor",
    val host: String = "localhost",
    val port: Int = 8085,
    val emulatorType: EmuType = EmuType.AZEROTHCORE,
    val expansion: Expansion = Expansion.WOTLK,
    val authConfig: DatabaseConfig = DatabaseConfig(database = "auth"),
    val charactersConfig: DatabaseConfig = DatabaseConfig(database = "characters"),
    val worldConfig: DatabaseConfig = DatabaseConfig(database = "world"),
    val isDefault: Boolean = true
)