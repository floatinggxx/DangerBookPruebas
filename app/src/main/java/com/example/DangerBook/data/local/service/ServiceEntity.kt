package com.example.DangerBook.data.local.service
import androidx.room.Entity
import androidx.room.PrimaryKey

// Entidad que representa los servicios disponibles en la barbería
@Entity(tableName = "services")
data class ServiceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    val name: String, // Nombre del servicio (ej: "Corte Clásico", "Barba")
    val description: String, // Descripción detallada del servicio
    val price: Double, // Precio en la moneda local
    val durationMinutes: Int, // Duración aproximada en minutos
    val imageUrl: String? = null, // URL de imagen (opcional, puede ser null)
    val isActive: Boolean = true // Indica si el servicio está activo/disponible
)