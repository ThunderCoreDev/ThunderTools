package com.thundertools.data.models

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

data class ModuleItem(
    val id: String,
    val name: String,
    val icon: ImageVector,
    val route: String,
    val group: ModuleGroup,
    val description: String = "",
    val requiresConnection: Boolean = false
)

data class ModuleGroup(
    val id: String,
    val name: String,
    val icon: ImageVector = Icons.Default.Category
)

object Modules {
    val GROUPS = listOf(
        ModuleGroup("server", "CONFIGURACIÓN DEL SERVIDOR", Icons.Default.Settings),
        ModuleGroup("players", "ADMINISTRACIÓN DE JUGADORES", Icons.Default.People),
        ModuleGroup("world", "MUNDO DEL JUEGO", Icons.Default.Public),
        ModuleGroup("tools", "HERRAMIENTAS DEL SERVIDOR", Icons.Default.Build),
        ModuleGroup("app", "AJUSTES DE LA APP", Icons.Default.Tune)
    )

    val ALL_MODULES = listOf(
        // Grupo: Configuración del Servidor
        ModuleItem(
            id = "emulator_config",
            name = "Configurar Emulador",
            icon = Icons.Default.Storage,
            route = "emulator_config",
            group = GROUPS[0],
            description = "Configurar tipo y versión del emulador"
        ),
        ModuleItem(
            id = "db_config",
            name = "Configurar Base de Datos",
            icon = Icons.Default.Database,
            route = "database_config",
            group = GROUPS[0],
            description = "Configurar conexiones a auth, characters, world"
        ),
        ModuleItem(
            id = "backup",
            name = "Backup/Restauración",
            icon = Icons.Default.Backup,
            route = "backup",
            group = GROUPS[0],
            description = "Gestionar copias de seguridad",
            requiresConnection = true
        ),

        // Grupo: Administración de Jugadores
        ModuleItem(
            id = "player_search",
            name = "Buscar Jugador",
            icon = Icons.Default.Search,
            route = "player_search",
            group = GROUPS[1],
            description = "Buscar jugadores por diversos criterios",
            requiresConnection = true
        ),
        ModuleItem(
            id = "account_mgmt",
            name = "Gestión de Cuentas",
            icon = Icons.Default.Person,
            route = "account_management",
            group = GROUPS[1],
            description = "Crear, editar y eliminar cuentas",
            requiresConnection = true
        ),
        ModuleItem(
            id = "character_mgmt",
            name = "Gestión de Personajes",
            icon = Icons.Default.Face,
            route = "character_management",
            group = GROUPS[1],
            description = "Administrar personajes de jugadores",
            requiresConnection = true
        ),

        // Grupo: Mundo del Juego
        ModuleItem(
            id = "npc_mgmt",
            name = "Gestión de NPCs",
            icon = Icons.Default.Pets,
            route = "npc_management",
            group = GROUPS[2],
            description = "Administrar criaturas y NPCs",
            requiresConnection = true
        ),
        ModuleItem(
            id = "item_mgmt",
            name = "Gestión de Objetos",
            icon = Icons.Default.ShoppingCart,
            route = "item_management",
            group = GROUPS[2],
            description = "Administrar objetos del juego",
            requiresConnection = true
        ),
        ModuleItem(
            id = "quest_mgmt",
            name = "Gestión de Misiones",
            icon = Icons.Default.Assignment,
            route = "quest_management",
            group = GROUPS[2],
            description = "Administrar misiones y recompensas",
            requiresConnection = true
        ),

        // Grupo: Herramientas del Servidor
        ModuleItem(
            id = "server_monitor",
            name = "Monitor de Estado",
            icon = Icons.Default.MonitorHeart,
            route = "server_monitor",
            group = GROUPS[3],
            description = "Monitorear estado del servidor",
            requiresConnection = true
        ),
        ModuleItem(
            id = "command_console",
            name = "Consola de Comandos",
            icon = Icons.Default.Terminal,
            route = "command_console",
            group = GROUPS[3],
            description = "Ejecutar comandos del servidor",
            requiresConnection = true
        ),
        ModuleItem(
            id = "server_logs",
            name = "Logs del Servidor",
            icon = Icons.Default.ListAlt,
            route = "server_logs",
            group = GROUPS[3],
            description = "Ver logs y actividades",
            requiresConnection = true
        ),

        // Grupo: Ajustes de la App
        ModuleItem(
            id = "connection_profiles",
            name = "Perfiles de Conexión",
            icon = Icons.Default.Link,
            route = "connection_profiles",
            group = GROUPS[4],
            description = "Gestionar múltiples servidores"
        ),
        ModuleItem(
            id = "app_theme",
            name = "Tema de la App",
            icon = Icons.Default.Palette,
            route = "app_theme",
            group = GROUPS[4],
            description = "Personalizar apariencia"
        ),
        ModuleItem(
            id = "app_settings",
            name = "Configuración General",
            icon = Icons.Default.Settings,
            route = "app_settings",
            group = GROUPS[4],
            description = "Ajustes generales de la aplicación"
        )
    )
}