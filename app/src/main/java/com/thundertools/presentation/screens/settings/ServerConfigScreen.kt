package com.thundertools.presentation.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.thundertools.data.models.EmuType
import com.thundertools.data.models.Expansion
import com.thundertools.presentation.components.BattleNetButton
import com.thundertools.presentation.viewmodels.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServerConfigScreen(
    navController: NavController,
    viewModel: SettingsViewModel = viewModel()
) {
    val profileState by viewModel.currentProfile.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()
    
    var serverName by remember { mutableStateOf(profileState?.name ?: "Mi Servidor") }
    var host by remember { mutableStateOf(profileState?.host ?: "localhost") }
    var port by remember { mutableStateOf(profileState?.port?.toString() ?: "8085") }
    var selectedEmulator by remember { mutableStateOf(profileState?.emulatorType ?: EmuType.AZEROTHCORE) }
    var selectedExpansion by remember { mutableStateOf(profileState?.expansion ?: Expansion.WOTLK) }
    var isDefault by remember { mutableStateOf(profileState?.isDefault ?: true) }
    
    LaunchedEffect(profileState) {
        profileState?.let {
            serverName = it.name
            host = it.host
            port = it.port.toString()
            selectedEmulator = it.emulatorType
            selectedExpansion = it.expansion
            isDefault = it.isDefault
        }
    }
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Configurar Servidor") },
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
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Información del Servidor",
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    OutlinedTextField(
                        value = serverName,
                        onValueChange = { serverName = it },
                        label = { Text("Nombre del Servidor") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    OutlinedTextField(
                        value = host,
                        onValueChange = { host = it },
                        label = { Text("Host/IP") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    OutlinedTextField(
                        value = port,
                        onValueChange = { port = it },
                        label = { Text("Puerto") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Configuración del Emulador",
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Selector de Emulador
                    Text("Tipo de Emulador", style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        EmuType.values().forEach { emulator ->
                            FilterChip(
                                selected = selectedEmulator == emulator,
                                onClick = { selectedEmulator = emulator },
                                label = { Text(emulator.name) }
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Selector de Expansión
                    Text("Expansión", style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Expansion.values().take(5).forEach { expansion ->
                            FilterChip(
                                selected = selectedExpansion == expansion,
                                onClick = { selectedExpansion = expansion },
                                label = { Text(expansion.name) }
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                    ) {
                        Switch(
                            checked = isDefault,
                            onCheckedChange = { isDefault = it }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Establecer como servidor predeterminado")
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            BattleNetButton(
                text = "GUARDAR CONFIGURACIÓN",
                onClick = {
                    val updatedState = profileState?.copy(
                        name = serverName,
                        host = host,
                        port = port.toIntOrNull() ?: 8085,
                        emulatorType = selectedEmulator,
                        expansion = selectedExpansion,
                        isDefault = isDefault
                    ) ?: SettingsViewModel.ServerProfileState(
                        name = serverName,
                        host = host,
                        port = port.toIntOrNull() ?: 8085,
                        emulatorType = selectedEmulator,
                        expansion = selectedExpansion,
                        isDefault = isDefault
                    )
                    
                    viewModel.updateProfileState(updatedState)
                    viewModel.saveProfile()
                    navController.navigateUp()
                },
                isLoading = isSaving,
                modifier = Modifier.fillMaxWidth(),
                icon = Icons.Default.Save
            )
        }
    }
}