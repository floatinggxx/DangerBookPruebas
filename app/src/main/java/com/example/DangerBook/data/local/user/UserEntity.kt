package com.example.DangerBook.data.local.user
import androidx.room.Entity
import androidx.room.PrimaryKey

// Entidad de usuario con soporte para roles y foto de perfil
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    val name: String,
    val email: String,
    val phone: String,
    val password: String,

    // Rol del usuario: "user" (cliente), "barber" (barbero), "admin" (administrador)
    val role: String = "user", // Por defecto es cliente

    // URI de la foto de perfil (String que contiene la ruta)
    val photoUri: String? = null,

    // Fecha de creación
    val createdAt: Long = System.currentTimeMillis()
)

// Enum para roles (mejor práctica para validación)
enum class UserRole(val value: String) {
    USER("user"),        // Cliente normal
    BARBER("barber"),    // Barbero
    ADMIN("admin");      // Administrador

    companion object {
        fun fromString(value: String): UserRole {
            return entries.find { it.value == value } ?: USER
        }
    }
}