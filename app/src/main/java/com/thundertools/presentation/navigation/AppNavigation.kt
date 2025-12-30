package com.thundertools.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.thundertools.presentation.screens.login.LoginScreen
import com.thundertools.presentation.screens.main.MainScreen
import com.thundertools.presentation.screens.settings.*
import com.thundertools.presentation.screens.splash.SplashScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = Destinations.SPLASH_SCREEN
    ) {
        // Pantallas principales
        composable(Destinations.SPLASH_SCREEN) {
            SplashScreen(navController = navController)
        }
        
        composable(Destinations.LOGIN_SCREEN) {
            LoginScreen(
                navController = navController,
                viewModel = hiltViewModel()
            )
        }
        
        composable(Destinations.MAIN_SCREEN) {
            MainScreen(
                navController = navController,
                viewModel = hiltViewModel()
            )
        }
        
        // Pantallas de configuración
        composable(Destinations.SERVER_CONFIG) {
            ServerConfigScreen(
                navController = navController,
                viewModel = hiltViewModel()
            )
        }
        
        composable(Destinations.DATABASE_CONFIG) {
            DatabaseConfigScreen(
                navController = navController,
                viewModel = hiltViewModel()
            )
        }
        
        composable(Destinations.EMULATOR_CONFIG) {
            EmulatorConfigScreen(
                navController = navController,
                viewModel = hiltViewModel()
            )
        }
        
        // TODO: Agregar más pantallas de módulos aquí
        // composable(Destinations.PLAYER_SEARCH) { ... }
        // composable(Destinations.ACCOUNT_MANAGEMENT) { ... }
        // etc...
    }
}