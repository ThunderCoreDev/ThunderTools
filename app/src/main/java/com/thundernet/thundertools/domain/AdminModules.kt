package com.maritza.thundertools.domain

enum class ModuleType { ACCOUNTS, CHARACTERS, WORLD, ECONOMY, SUPPORT }

data class ModuleAction(val label: String, val commandKey: String, val requiresOnline: Boolean = true)

object AdminModules {
    val modules: Map<ModuleType, List<ModuleAction>> = mapOf(
        ModuleType.ACCOUNTS to listOf(
            ModuleAction("Crear cuenta", "accounts.create"),
            ModuleAction("Banear", "accounts.ban"),
            ModuleAction("Cambiar contraseña", "accounts.set_password"),
            ModuleAction("Editar nombre de usuario", "accounts.set_username")
        ),
        ModuleType.CHARACTERS to listOf(
            ModuleAction("Teletransportar", "characters.teleport"),
            ModuleAction("Cambiar nivel", "characters.set_level"),
            ModuleAction("Reputación", "characters.set_reputation"),
            ModuleAction("Facción", "characters.set_faction")
        ),
        ModuleType.WORLD to listOf(
            ModuleAction("Anunciar", "world.announce"),
            ModuleAction("Reiniciar", "world.restart"),
            ModuleAction("Cargar script", "world.load_script")
        ),
        ModuleType.ECONOMY to listOf(
            ModuleAction("Oro", "economy.set_gold"),
            ModuleAction("Items", "economy.add_item"),
            ModuleAction("Correo", "economy.send_mail")
        ),
        ModuleType.SUPPORT to listOf(
            ModuleAction("Tickets", "support.tickets"),
            ModuleAction("Logs", "support.logs")
        )
    )
}