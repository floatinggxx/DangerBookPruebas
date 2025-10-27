package com.example.DangerBook.data.repository
import android.content.Context
import com.example.DangerBook.data.local.appointment.AppointmentDao
import com.example.DangerBook.data.local.appointment.AppointmentEntity
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

// Repositorio para manejar la lógica de negocio de las citas
class AppointmentRepository(
    private val appointmentDao: AppointmentDao,
    private val context: Context // Necesario para enviar notificaciones
) {

    // Crear una nueva cita
    suspend fun createAppointment(
        userId: Long,
        barberId: Long?,
        serviceId: Long,
        dateTime: Long,
        durationMinutes: Int,
        notes: String?
    ): Result<Long> {
        return try {
            // Validar que la fecha no sea en el pasado
            if (dateTime < System.currentTimeMillis()) {
                return Result.failure(IllegalArgumentException("No puedes agendar citas en el pasado"))
            }

            // Verificar si hay conflictos de horario (si se especificó un barbero)
            if (barberId != null) {
                val conflicts = appointmentDao.countConflictingAppointments(barberId, dateTime)
                if (conflicts > 0) {
                    return Result.failure(IllegalArgumentException("Este horario ya está ocupado"))
                }
            }

            // Crear la cita
            val appointment = AppointmentEntity(
                userId = userId,
                barberId = barberId,
                serviceId = serviceId,
                dateTime = dateTime,
                durationMinutes = durationMinutes,
                status = "pending",
                notes = notes
            )

            val id = appointmentDao.insert(appointment)
            Result.success(id)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Obtener todas las citas de un usuario
    fun getUserAppointments(userId: Long): Flow<List<AppointmentEntity>> {
        return appointmentDao.getByUserId(userId)
    }

    // Obtener solo las citas próximas (pendientes/confirmadas)
    fun getUpcomingAppointments(userId: Long): Flow<List<AppointmentEntity>> {
        return appointmentDao.getUpcomingByUserId(userId)
    }

    // Obtener una cita específica por ID
    suspend fun getAppointmentById(appointmentId: Long): AppointmentEntity? {
        return appointmentDao.getById(appointmentId)
    }

    // Cancelar una cita
    suspend fun cancelAppointment(appointmentId: Long): Result<Unit> {
        return try {
            appointmentDao.cancelAppointment(appointmentId)
            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Confirmar una cita
    suspend fun confirmAppointment(appointmentId: Long): Result<Unit> {
        return try {
            appointmentDao.confirmAppointment(appointmentId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Completar una cita
    suspend fun completeAppointment(appointmentId: Long): Result<Unit> {
        return try {
            appointmentDao.completeAppointment(appointmentId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Obtener todas las citas de un barbero
    fun getBarberAppointments(barberId: Long): Flow<List<AppointmentEntity>> {
        return appointmentDao.getByBarberId(barberId)
    }

    // Verificar horarios disponibles para un día específico
    suspend fun getAvailableTimeSlotsForDay(
        barberId: Long,
        date: Calendar,
        serviceDurationMinutes: Int
    ): List<Long> {
        // Configurar inicio y fin del día
        val startOfDay = date.clone() as Calendar
        startOfDay.set(Calendar.HOUR_OF_DAY, 9) // Horario de apertura: 9 AM
        startOfDay.set(Calendar.MINUTE, 0)
        startOfDay.set(Calendar.SECOND, 0)

        val endOfDay = date.clone() as Calendar
        endOfDay.set(Calendar.HOUR_OF_DAY, 20) // Horario de cierre: 8 PM
        endOfDay.set(Calendar.MINUTE, 0)
        endOfDay.set(Calendar.SECOND, 0)

        // Obtener citas existentes del barbero ese día
        val existingAppointments = appointmentDao.getBarberAppointmentsForDay(
            barberId,
            startOfDay.timeInMillis,
            endOfDay.timeInMillis
        )

        // Generar slots cada 30 minutos
        val availableSlots = mutableListOf<Long>()
        val currentSlot = startOfDay.clone() as Calendar

        while (currentSlot.before(endOfDay)) {
            val slotTime = currentSlot.timeInMillis

            // Verificar si este slot no colisiona con citas existentes
            val hasConflict = existingAppointments.any { appointment ->
                val appointmentEnd = appointment.dateTime + (appointment.durationMinutes * 60 * 1000)
                val slotEnd = slotTime + (serviceDurationMinutes * 60 * 1000)

                // Hay conflicto si los intervalos se solapan
                slotTime < appointmentEnd && slotEnd > appointment.dateTime
            }

            if (!hasConflict && slotTime > System.currentTimeMillis()) {
                availableSlots.add(slotTime)
            }

            // Avanzar 30 minutos
            currentSlot.add(Calendar.MINUTE, 30)
        }

        return availableSlots
    }
}