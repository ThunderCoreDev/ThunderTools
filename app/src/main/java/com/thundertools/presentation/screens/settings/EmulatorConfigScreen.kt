package com.thundertools.presentation.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.thundertools.data.models.EmuType
import com.thundertools.presentation.components.BattleNetButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmulatorConfigScreen(
    navController: NavController,
    viewModel: SettingsViewModel = viewModel()
) {
    val profileState by viewModel.currentProfile.collectAsState()
    
    var emulatorPath by remember { mutableStateOf("") }
    var executableName by remember { mutableStateOf("") }
    var startupParams by remember { mutableStateOf("") }
    var autoStart by remember { mutableStateOf(false) }
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Configurar Emulador") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Información del Emulador Actual
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Info, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Emulador: ${profileState?.emulatorType?.name ?: "No configurado"}",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Expansión: ${profileState?.expansion?.name ?: "No configurada"}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Configuración de Ruta
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Rutas del Emulador",
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    OutlinedTextField(
                        value = emulatorPath,
                        onValueChange = { emulatorPath = it },
                        label = { Text("Ruta del Emulador") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        trailingIcon = {
                            IconButton(onClick = { /* Browse dialog */ }) {
                                Icon(Icons.Default.FolderOpen, contentDescription = "Buscar")
                            }
                        }
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    OutlinedTextField(
                        value = executableName,
                        onValueChange = { executableName = it },
                        label = { Text("Nombre del Ejecutable") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        placeholder = { Text("ej: worldserver.exe, authserver.exe") }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Parámetros de Inicio
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Parámetros de Inicio",
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    OutlinedTextField(
                        value = startupParams,
                        onValueChange = { startupParams = it },
                        label = { Text("Parámetros de Línea de Comandos") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        placeholder = { Text("ej: -c config.conf") }
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Switch(
                            checked = autoStart,
                            onCheckedChange = { autoStart = it }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Iniciar automáticamente al conectar")
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Lista de Emuladores Soportados
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Emuladores Soportados",
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    EmuType.values().forEach { emulator ->
                        ListItem(
                            headlineContent = { Text(emulator.name) },
                            supportingContent = { 
                                Text(
                                    when (emulator) {
                                        EmuType.AZEROTHCORE -> "Basado en TrinityCore para WotLK"
                                        EmuType.TRINITYCORE -> "Core original para múltiples expansiones"
                                        EmuType.CMANGOS -> "Para expansiones clásicas"
                                        EmuType.MANGOSONE -> "Emulador original"
                                    }
                                )
                            },
                            leadingContent = {
                                Icon(Icons.Default.Storage, contentDescription = null)
                            }
                        )
                        Divider()
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                BattleNetButton(
                    text = "CANCELAR",
                    onClick = { navController.navigateUp() },
                    modifier = Modifier.weight(1f),
                    backgroundColor = MaterialTheme.colorScheme.error
                )
                
                BattleNetButton(
                    text = "GUARDAR",
                    onClick = {
                        // Guardar configuración
                        navController.navigateUp()
                    },
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Save
                )
            }
        }
    }
}