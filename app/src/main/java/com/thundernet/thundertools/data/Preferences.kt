package com.maritza.thundertools.data

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

private val Context.dataStore by preferencesDataStore("thundertools_prefs")

class Preferences(private val context: Context) {
    private val ADMIN_USER = stringPreferencesKey("admin_user")
    private val ADMIN_PASS = stringPreferencesKey("admin_pass")
    private val FIRST_RUN_DONE = booleanPreferencesKey("first_run_done")

    private val EMULATOR = stringPreferencesKey("emulator")
    private val EXPANSION = stringPreferencesKey("expansion")

    private val DB_HOST = stringPreferencesKey("db_host")
    private val DB_PORT = stringPreferencesKey("db_port")
    private val DB_NAME = stringPreferencesKey("db_name")
    private val DB_USER = stringPreferencesKey("db_user")
    private val DB_PASS = stringPreferencesKey("db_pass")

    private val SOAP_HOST = stringPreferencesKey("soap_host")
    private val SOAP_PORT = stringPreferencesKey("soap_port")
    private val SOAP_USER = stringPreferencesKey("soap_user")
    private val SOAP_PASS = stringPreferencesKey("soap_pass")

    fun isFirstRun(): Boolean = runBlocking {
        val d = context.dataStore.data.first()
        !(d[FIRST_RUN_DONE] ?: false)
    }

    fun completeFirstRun(user: String, pass: String) = runBlocking {
        context.dataStore.edit {
            it[ADMIN_USER] = user
            it[ADMIN_PASS] = pass
            it[FIRST_RUN_DONE] = true
        }
    }

    fun saveSettings(
        emulator: String, expansion: String,
        dbHost: String, dbPort: String, dbName: String, dbUser: String, dbPass: String,
        soapHost: String, soapPort: String, soapUser: String, soapPass: String,
    ) = runBlocking {
        context.dataStore.edit {
            it[EMULATOR] = emulator
            it[EXPANSION] = expansion
            it[DB_HOST] = dbHost
            it[DB_PORT] = dbPort
            it[DB_NAME] = dbName
            it[DB_USER] = dbUser
            it[DB_PASS] = dbPass
            it[SOAP_HOST] = soapHost
            it[SOAP_PORT] = soapPort
            it[SOAP_USER] = soapUser
            it[SOAP_PASS] = soapPass
        }
    }

    fun getAll(): Map<String, String> = runBlocking {
        val p = context.dataStore.data.first()
        mapOf(
            "admin_user" to (p[ADMIN_USER] ?: ""),
            "admin_pass" to (p[ADMIN_PASS] ?: ""),
            "emulator" to (p[EMULATOR] ?: ""),
            "expansion" to (p[EXPANSION] ?: ""),
            "db_host" to (p[DB_HOST] ?: ""),
            "db_port" to (p[DB_PORT] ?: ""),
            "db_name" to (p[DB_NAME] ?: ""),
            "db_user" to (p[DB_USER] ?: ""),
            "db_pass" to (p[DB_PASS] ?: ""),
            "soap_host" to (p[SOAP_HOST] ?: ""),
            "soap_port" to (p[SOAP_PORT] ?: ""),
            "soap_user" to (p[SOAP_USER] ?: ""),
            "soap_pass" to (p[SOAP_PASS] ?: "")
        )
    }
}