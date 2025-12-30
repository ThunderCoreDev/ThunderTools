package com.thundertools.data.models

data class DatabaseConfig(
    val host: String = "localhost",
    val port: Int = 3306,
    val database: String = "",
    val username: String = "",
    val password: String = "",
    val connectionName: String = "Default"
)

data class EmulatorConfig(
    val type: EmulatorType = EmulatorType.AZEROTHCORE,
    val expansion: Expansion = Expansion.WOTLK,
    val path: String = "",
    val executable: String = ""
)

enum class EmulatorType {
    AZEROTHCORE, TRINITYCORE, CMANGOS, MANGOSONE
}

enum class Expansion {
    CLASSIC, TBC, WOTLK, CATA, MOP, WOD, LEGION, BFA, SHADOWLANDS, DRAGONFLIGHT
}