package com.example.DangerBook.data.local.appointment

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

// DAO para operaciones de citas en la base de datos
@Dao
interface AppointmentDao {

    // Insertar una nueva cita
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(appointment: AppointmentEntity): Long

    // Actualizar una cita existente
    @Update
    suspend fun update(appointment: AppointmentEntity)

    // Obtener todas las citas de un usuario ordenadas por fecha (más recientes primero)
    @Query("SELECT * FROM appointments WHERE userId = :userId ORDER BY dateTime DESC")
    fun getByUserId(userId: Long): Flow<List<AppointmentEntity>>

    // Obtener citas pendientes/confirmadas de un usuario (excluye completadas y canceladas)
    @Query("""
        SELECT * FROM appointments 
        WHERE userId = :userId 
        AND status IN ('pending', 'confirmed')
        ORDER BY dateTime ASC
    """)
    fun getUpcomingByUserId(userId: Long): Flow<List<AppointmentEntity>>

    // Obtener una cita por ID
    @Query("SELECT * FROM appointments WHERE id = :appointmentId")
    suspend fun getById(appointmentId: Long): AppointmentEntity?

    // Verificar si un barbero tiene citas en un horario específico (para evitar conflictos)
    @Query("""
        SELECT COUNT(*) FROM appointments 
        WHERE barberId = :barberId 
        AND dateTime = :dateTime 
        AND status IN ('pending', 'confirmed')
    """)
    suspend fun countConflictingAppointments(barberId: Long, dateTime: Long): Int

    // Obtener todas las citas de un día específico para un barbero
    @Query("""
        SELECT * FROM appointments 
        WHERE barberId = :barberId 
        AND dateTime >= :startOfDay 
        AND dateTime < :endOfDay
        AND status IN ('pending', 'confirmed')
        ORDER BY dateTime ASC
    """)
    suspend fun getBarberAppointmentsForDay(barberId: Long, startOfDay: Long, endOfDay: Long): List<AppointmentEntity>

    // Cancelar una cita (actualiza el estado)
    @Query("UPDATE appointments SET status = 'cancelled', updatedAt = :timestamp WHERE id = :appointmentId")
    suspend fun cancelAppointment(appointmentId: Long, timestamp: Long = System.currentTimeMillis())

    // Confirmar una cita (cambiar de pending a confirmed)
    @Query("UPDATE appointments SET status = 'confirmed', updatedAt = :timestamp WHERE id = :appointmentId")
    suspend fun confirmAppointment(appointmentId: Long, timestamp: Long = System.currentTimeMillis())

    // Completar una cita (cambiar a completed)
    @Query("UPDATE appointments SET status = 'completed', updatedAt = :timestamp WHERE id = :appointmentId")
    suspend fun completeAppointment(appointmentId: Long, timestamp: Long = System.currentTimeMillis())

    // Obtener todas las citas asignadas a un barbero
    @Query("SELECT * FROM appointments WHERE barberId = :barberId ORDER BY dateTime DESC")
    fun getByBarberId(barberId: Long): Flow<List<AppointmentEntity>>

    // Contar citas
    @Query("SELECT COUNT(*) FROM appointments")
    suspend fun count(): Int
}