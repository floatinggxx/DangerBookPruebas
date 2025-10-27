package com.example.DangerBook.data.local.storage

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

//variable de contexto para obtener un DataStore para el almacenamiento
val Context.dataStore by preferencesDataStore(name = "user_prefs")

class UserPreferences (private val context: Context){
    //variable para manipular el estado de la sesion
    private val issLoggedInKey = booleanPreferencesKey("is_logged_in")

    //funcion para setear el valor de la sesion
    suspend fun setLoggedIn(value: Boolean){
        context.dataStore.edit { prefs ->
            prefs[issLoggedInKey] = value
        }
    }

    //funcion exponer el valor de mi variable de store
    val isLoggedIn: Flow<Boolean> = context.dataStore.data
        .map { prefs -> prefs[issLoggedInKey] ?: false }



}