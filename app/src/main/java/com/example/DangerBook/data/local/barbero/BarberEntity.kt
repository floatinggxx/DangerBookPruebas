package com.example.DangerBook.data.local.barbero

import androidx.room.Entity
import androidx.room.PrimaryKey

// Entidad que representa a los barberos/estilistas del Studio Danger
@Entity(tableName = "barbers")
data class BarberEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    val name: String, // Nombre del barbero
    val specialty: String, // Especialidad (ej: "Cortes modernos", "Barba y bigote")
    val photoUrl: String? = null, // URL de foto del barbero (opcional)
    val rating: Double = 5.0, // Calificación del barbero (0.0 a 5.0)
    val isAvailable: Boolean = true // Indica si está disponible para agendar
)