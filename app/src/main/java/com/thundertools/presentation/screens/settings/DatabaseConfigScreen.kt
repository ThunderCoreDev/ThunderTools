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
import com.thundertools.data.models.DatabaseConfig
import com.thundertools.presentation.components.BattleNetButton
import com.thundertools.presentation.viewmodels.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatabaseConfigScreen(
    navController: NavController,
    viewModel: SettingsViewModel = viewModel()
) {
    val profileState by viewModel.currentProfile.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()
    
    var authConfig by remember { mutableStateOf(profileState?.authConfig ?: DatabaseConfig(database = "auth")) }
    var charsConfig by remember { mutableStateOf(profileState?.charactersConfig ?: DatabaseConfig(database = "characters")) }
    var worldConfig by remember { mutableStateOf(profileState?.worldConfig ?: DatabaseConfig(database = "world")) }
    
    LaunchedEffect(profileState) {
        profileState?.let {
            authConfig = it.authConfig
            charsConfig = it.charactersConfig
            worldConfig = it.worldConfig
        }
    }
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Configuración de Base de Datos") },
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
            // Base de Datos Auth
            DatabaseConfigSection(
                title = "Base de Datos Auth",
                config = authConfig,
                onConfigChange = { authConfig = it },
                icon = Icons.Default.Security
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Base de Datos Characters
            DatabaseConfigSection(
                title = "Base de Datos Characters",
                config = charsConfig,
                onConfigChange = { charsConfig = it },
                icon = Icons.Default.Person
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Base de Datos World
            DatabaseConfigSection(
                title = "Base de Datos World",
                config = worldConfig,
                onConfigChange = { worldConfig = it },
                icon = Icons.Default.Public
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Botones
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                BattleNetButton(
                    text = "Test Conexión",
                    onClick = { /* Test connection logic */ },
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Wifi
                )
                
                BattleNetButton(
                    text = "Guardar",
                    onClick = {
                        val updatedState = profileState?.copy(
                            authConfig = authConfig,
                            charactersConfig = charsConfig,
                            worldConfig = worldConfig
                        )
                        updatedState?.let {
                            viewModel.updateProfileState(it)
                            viewModel.saveProfile()
                        }
                    },
                    modifier = Modifier.weight(1f),
                    isLoading = isSaving,
                    icon = Icons.Default.Save
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Información
            Text(
                text = "Los datos se guardan localmente. Para operaciones en línea necesitarás conexión al servidor.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun DatabaseConfigSection(
    title: String,
    config: DatabaseConfig,
    onConfigChange: (DatabaseConfig) -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Icon(icon, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Campos de configuración
            OutlinedTextField(
                value = config.host,
                onValueChange = { onConfigChange(config.copy(host = it)) },
                label = { Text("Host") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedTextField(
                value = config.port.toString(),
                onValueChange = { 
                    val port = it.toIntOrNull() ?: 3306
                    onConfigChange(config.copy(port = port))
                },
                label = { Text("Puerto") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedTextField(
                value = config.database,
                onValueChange = { onConfigChange(config.copy(database = it)) },
                label = { Text("Nombre de la Base de Datos") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedTextField(
                value = config.username,
                onValueChange = { onConfigChange(config.copy(username = it)) },
                label = { Text("Usuario") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedTextField(
                value = config.password,
                onValueChange = { onConfigChange(config.copy(password = it)) },
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                singleLine = true
            )
        }
    }
}