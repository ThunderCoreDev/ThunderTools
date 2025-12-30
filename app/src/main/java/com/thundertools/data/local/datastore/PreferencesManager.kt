package com.thundertools.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "thundertools_prefs")

class PreferencesManager @Inject constructor(
    private val context: Context
) {
    companion object {
        private val IS_FIRST_LAUNCH = booleanPreferencesKey("is_first_launch")
        private val DARK_MODE = booleanPreferencesKey("dark_mode")
        private val SELECTED_THEME = stringPreferencesKey("selected_theme")
        private val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        private val AUTO_CONNECT = booleanPreferencesKey("auto_connect")
        private val LAST_SERVER_ID = intPreferencesKey("last_server_id")
    }
    
    suspend fun setFirstLaunch(completed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[IS_FIRST_LAUNCH] = completed
        }
    }
    
    val isFirstLaunch: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[IS_FIRST_LAUNCH] ?: true
        }
    
    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DARK_MODE] = enabled
        }
    }
    
    val darkMode: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[DARK_MODE] ?: true
        }
    
    suspend fun setSelectedTheme(theme: String) {
        context.dataStore.edit { preferences ->
            preferences[SELECTED_THEME] = theme
        }
    }
    
    val selectedTheme: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[SELECTED_THEME] ?: "battlenet"
        }
    
    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[NOTIFICATIONS_ENABLED] = enabled
        }
    }
    
    val notificationsEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[NOTIFICATIONS_ENABLED] ?: true
        }
    
    suspend fun setAutoConnect(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[AUTO_CONNECT] = enabled
        }
    }
    
    val autoConnect: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[AUTO_CONNECT] ?: false
        }
    
    suspend fun setLastServerId(serverId: Int) {
        context.dataStore.edit { preferences ->
            preferences[LAST_SERVER_ID] = serverId
        }
    }
    
    val lastServerId: Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[LAST_SERVER_ID] ?: -1
        }
}