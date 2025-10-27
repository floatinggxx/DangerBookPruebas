package com.example.DangerBook.data.local.user

import androidx.room.Entity
import androidx.room.PrimaryKey

// @Entity declara una tabla SQLite manejada por Room.
// tableName = "users" define el nombre exacto de la tabla.
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)    // Clave primaria autoincremental
    val id: Long = 0L,

    val name: String,                   // Nombre completo del usuario
    val email: String,                  // Correo (idealmente único a nivel de negocio)
    val phone: String,                  // Teléfono del usuario (⚠️ agregado)
    val password: String                // Contraseña (para demo; en prod usar hash)
)