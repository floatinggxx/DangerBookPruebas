package com.example.DangerBook.data.local.storage
import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Extensión del contexto para manipular el DataStore
val Context.dataStore by preferencesDataStore("user_prefs")

// Clase para manejar las preferencias del usuario (sesión persistente)
class UserPreferences(private val context: Context) {

    // Claves para almacenar datos
    private val isLoggedInKey = booleanPreferencesKey("is_logged_in")
    private val userIdKey = longPreferencesKey("user_id")
    private val userNameKey = stringPreferencesKey("user_name")
    private val userEmailKey = stringPreferencesKey("user_email")
    private val userRoleKey = stringPreferencesKey("user_role")
    private val userPhotoKey = stringPreferencesKey("user_photo")

    // ========== GUARDAR SESIÓN ==========

    // Guardar sesión completa del usuario
    suspend fun saveUserSession(
        userId: Long,
        userName: String,
        userEmail: String,
        userRole: String,
        userPhoto: String?
    ) {
        context.dataStore.edit { prefs ->
            prefs[isLoggedInKey] = true
            prefs[userIdKey] = userId
            prefs[userNameKey] = userName
            prefs[userEmailKey] = userEmail
            prefs[userRoleKey] = userRole
            if (userPhoto != null) {
                prefs[userPhotoKey] = userPhoto
            }
        }
    }

    // Actualizar solo el estado de login
    suspend fun setLoggedIn(value: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[isLoggedInKey] = value
        }
    }

    // Actualizar foto de perfil
    suspend fun updateUserPhoto(photoUri: String?) {
        context.dataStore.edit { prefs ->
            if (photoUri != null) {
                prefs[userPhotoKey] = photoUri
            } else {
                prefs.remove(userPhotoKey)
            }
        }
    }

    // Cerrar sesión (limpiar todo)
    suspend fun clearSession() {
        context.dataStore.edit { prefs ->
            prefs.clear()
        }
    }

    // ========== OBTENER DATOS ==========

    // Estado de login
    val isLoggedIn: Flow<Boolean> = context.dataStore.data
        .map { prefs ->
            prefs[isLoggedInKey] ?: false
        }

    // ID del usuario
    val userId: Flow<Long?> = context.dataStore.data
        .map { prefs ->
            prefs[userIdKey]
        }

    // Nombre del usuario
    val userName: Flow<String?> = context.dataStore.data
        .map { prefs ->
            prefs[userNameKey]
        }

    // Email del usuario
    val userEmail: Flow<String?> = context.dataStore.data
        .map { prefs ->
            prefs[userEmailKey]
        }

    // Rol del usuario
    val userRole: Flow<String?> = context.dataStore.data
        .map { prefs ->
            prefs[userRoleKey]
        }

    // Foto del usuario
    val userPhoto: Flow<String?> = context.dataStore.data
        .map { prefs ->
            prefs[userPhotoKey]
        }
}