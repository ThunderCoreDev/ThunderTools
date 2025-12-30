package com.thundertools.presentation.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.thundertools.data.models.Modules
import com.thundertools.presentation.components.ModuleCard
import com.thundertools.presentation.theme.ElectricBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavController,
    viewModel: MainViewModel = viewModel()
) {
    val isOnline by viewModel.isOnline.collectAsState()
    val connectionStatus by viewModel.connectionStatus.collectAsState()
    
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "THUNDERTOOLS",
                        color = ElectricBlue,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = ElectricBlue
                ),
                actions = {
                    // Indicador de conexión
                    Badge(
                        containerColor = if (isOnline) Color.Green else Color.Red,
                        modifier = Modifier
                            .size(12.dp)
                            .padding(end = 16.dp)
                    )
                }
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
                    label = { Text("Inicio") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("connection_profiles") },
                    icon = { Icon(Icons.Default.Link, contentDescription = "Conexiones") },
                    label = { Text("Conexiones") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate("app_settings") },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Ajustes") },
                    label = { Text("Ajustes") }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Header con estado
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Bienvenido, Administrador",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = if (isOnline) "✓ Conectado al servidor" else "⚠ Modo offline",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isOnline) Color.Green else Color.Yellow
                    )
                }
                
                // Botón de recargar/actualizar
                IconButton(
                    onClick = { viewModel.checkConnection() }
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = "Recargar",
                        tint = ElectricBlue
                    )
                }
            }
            
            // Lista de módulos por grupos
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(16.dp)
            ) {
                Modules.GROUPS.forEach { group ->
                    item {
                        Text(
                            text = group.name,
                            style = MaterialTheme.typography.titleSmall,
                            color = ElectricBlue,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    
                    // Módulos de este grupo
                    val groupModules = Modules.ALL_MODULES.filter { it.group.id == group.id }
                    
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            groupModules.chunked(3).forEach { rowModules ->
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    rowModules.forEach { module ->
                                        ModuleCard(
                                            module = module,
                                            isOnline = isOnline,
                                            onClick = {
                                                if (!module.requiresConnection || isOnline) {
                                                    navController.navigate(module.route)
                                                } else {
                                                    // Mostrar mensaje de que se necesita conexión
                                                    viewModel.showOfflineMessage()
                                                }
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                    
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}