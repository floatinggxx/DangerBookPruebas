package com.example.DangerBook.data.local.appointment

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.DangerBook.data.local.barbero.BarberEntity
import com.example.DangerBook.data.local.service.ServiceEntity
import com.example.uinavegacion.data.local.user.UserEntity

// Entidad que representa una cita agendada en la barbería
@Entity(
    tableName = "appointments",
    // Índices para búsquedas rápidas por usuario, barbero y fecha
    indices = [
        Index(value = ["userId"]),
        Index(value = ["barberId"]),
        Index(value = ["serviceId"]),
        Index(value = ["dateTime"])
    ],
    // Relaciones con otras tablas (opcional, para integridad referencial)
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE // Si se borra el usuario, se borran sus appointment
        ),
        ForeignKey(
            entity = BarberEntity::class,
            parentColumns = ["id"],
            childColumns = ["barberId"],
            onDelete = ForeignKey.SET_NULL // Si se borra el barbero, el campo queda null
        ),
        ForeignKey(
            entity = ServiceEntity::class,
            parentColumns = ["id"],
            childColumns = ["serviceId"],
            onDelete = ForeignKey.CASCADE // Si se borra el servicio, se borra la cita
        )
    ]
)
data class AppointmentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    val userId: Long, // ID del usuario que agenda la cita
    val barberId: Long?, // ID del barbero asignado (puede ser null si es "cualquier barbero")
    val serviceId: Long, // ID del servicio solicitado

    val dateTime: Long, // Fecha y hora de la cita en milisegundos (timestamp)
    val durationMinutes: Int, // Duración estimada en minutos

    val status: String, // Estado: "pending" (pendiente), "confirmed" (confirmada), "completed" (completada), "cancelled" (cancelada)
    val notes: String? = null, // Notas adicionales del cliente (opcional)

    val createdAt: Long = System.currentTimeMillis(), // Fecha de creación de la cita
    val updatedAt: Long = System.currentTimeMillis() // Última actualización
)