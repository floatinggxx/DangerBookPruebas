package com.example.DangerBook.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.DangerBook.data.local.appointment.AppointmentEntity
import com.example.DangerBook.data.repository.AppointmentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar

// Estado para agendar una nueva cita
data class BookAppointmentUiState(
    val selectedServiceId: Long? = null, // Servicio seleccionado
    val selectedBarberId: Long? = null, // Barbero seleccionado (null = "cualquier barbero")
    val selectedDate: Calendar? = null, // Fecha seleccionada
    val selectedTimeSlot: Long? = null, // Horario seleccionado (timestamp)
    val availableTimeSlots: List<Long> = emptyList(), // Horarios disponibles
    val notes: String = "", // Notas adicionales del cliente
    val isLoadingSlots: Boolean = false, // Indica si está cargando horarios
    val isSubmitting: Boolean = false, // Indica si está creando la cita
    val canSubmit: Boolean = false, // Habilita el botón de agendar
    val success: Boolean = false, // Indica si la cita se creó exitosamente
    val errorMsg: String? = null // Mensaje de error
)

// Estado para visualizar las citas del usuario
data class MyAppointmentsUiState(
    val upcomingAppointments: List<AppointmentEntity> = emptyList(), // Citas próximas
    val allAppointments: List<AppointmentEntity> = emptyList(), // Todas las citas
    val isLoading: Boolean = true, // Indica si está cargando
    val errorMsg: String? = null // Mensaje de error
)

// ViewModel para gestionar las citas
class AppointmentViewModel(
    private val repository: AppointmentRepository,
    private val userId: Long // ID del usuario actual (lo obtenemos del login)
) : ViewModel() {

    // Estado para agendar cita
    private val _bookState = MutableStateFlow(BookAppointmentUiState())
    val bookState: StateFlow<BookAppointmentUiState> = _bookState

    // Estado para visualizar citas
    private val _myAppointmentsState = MutableStateFlow(MyAppointmentsUiState())
    val myAppointmentsState: StateFlow<MyAppointmentsUiState> = _myAppointmentsState

    init {
        // Cargar citas del usuario al iniciar
        loadUserAppointments()
    }

    // Cargar citas del usuario de forma eficiente
    private fun loadUserAppointments() {
        viewModelScope.launch {
            _myAppointmentsState.update { it.copy(isLoading = true, errorMsg = null) }
            try {
                // Observar todas las citas del usuario desde una única fuente de datos
                repository.getUserAppointments(userId).collectLatest { allUserAppointments ->
                    // Filtrar las citas próximas desde la lista completa
                    val upcoming = allUserAppointments.filter {
                        it.status in listOf("pending", "confirmed")
                    }
                    _myAppointmentsState.update {
                        it.copy(
                            isLoading = false,
                            allAppointments = allUserAppointments,
                            upcomingAppointments = upcoming
                        )
                    }
                }
            } catch (e: Exception) {
                _myAppointmentsState.update {
                    it.copy(isLoading = false, errorMsg = "Error al cargar las citas: ${e.message}")
                }
            }
        }
    }

    // ---- Handlers para agendar cita ----

    fun onSelectService(serviceId: Long, durationMinutes: Int) {
        _bookState.update {
            it.copy(
                selectedServiceId = serviceId,
                selectedTimeSlot = null, // Reset del horario al cambiar servicio
                availableTimeSlots = emptyList()
            )
        }
        // Si ya hay fecha y barbero, recargar horarios
        val state = _bookState.value
        if (state.selectedDate != null && state.selectedBarberId != null) {
            loadAvailableTimeSlots(state.selectedBarberId, state.selectedDate, durationMinutes)
        }
        recomputeCanSubmit()
    }

    fun onSelectBarber(barberId: Long?) {
        _bookState.update {
            it.copy(
                selectedBarberId = barberId,
                selectedTimeSlot = null, // Reset del horario al cambiar barbero
                availableTimeSlots = emptyList()
            )
        }
        recomputeCanSubmit()
    }

    fun onSelectDate(date: Calendar, serviceDurationMinutes: Int) {
        _bookState.update {
            it.copy(
                selectedDate = date,
                selectedTimeSlot = null, // Reset del horario al cambiar fecha
                availableTimeSlots = emptyList()
            )
        }
        // Cargar horarios disponibles si hay barbero seleccionado
        val barberId = _bookState.value.selectedBarberId
        if (barberId != null) {
            loadAvailableTimeSlots(barberId, date, serviceDurationMinutes)
        }
        recomputeCanSubmit()
    }

    fun onSelectTimeSlot(timeSlot: Long) {
        _bookState.update { it.copy(selectedTimeSlot = timeSlot) }
        recomputeCanSubmit()
    }

    fun onNotesChange(notes: String) {
        _bookState.update { it.copy(notes = notes) }
    }

    // Cargar horarios disponibles
    private fun loadAvailableTimeSlots(barberId: Long, date: Calendar, durationMinutes: Int) {
        viewModelScope.launch {
            _bookState.update { it.copy(isLoadingSlots = true) }
            try {
                val slots = repository.getAvailableTimeSlotsForDay(barberId, date, durationMinutes)
                _bookState.update {
                    it.copy(availableTimeSlots = slots, isLoadingSlots = false)
                }
            } catch (e: Exception) {
                _bookState.update {
                    it.copy(isLoadingSlots = false, errorMsg = "Error al cargar horarios: ${e.message}")
                }
            }
        }
    }

    // Validar si se puede enviar
    private fun recomputeCanSubmit() {
        val s = _bookState.value
        val canSubmit = s.selectedServiceId != null &&
                s.selectedBarberId != null &&
                s.selectedDate != null &&
                s.selectedTimeSlot != null
        _bookState.update { it.copy(canSubmit = canSubmit) }
    }

    // Enviar (crear cita)
    fun submitBooking(serviceDurationMinutes: Int) {
        val state = _bookState.value
        if (!state.canSubmit || state.isSubmitting) return

        // Usar \"let\" para evitar el uso de \"!!\" y aumentar la seguridad
        state.selectedBarberId?.let { barberId ->
            state.selectedServiceId?.let { serviceId ->
                state.selectedTimeSlot?.let { timeSlot ->
                    viewModelScope.launch {
                        try {
                            _bookState.update { it.copy(isSubmitting = true, errorMsg = null) }

                            val result = repository.createAppointment(
                                userId = userId,
                                barberId = barberId,
                                serviceId = serviceId,
                                dateTime = timeSlot,
                                durationMinutes = serviceDurationMinutes,
                                notes = state.notes.ifBlank { null }
                            )

                            _bookState.update {
                                if (result.isSuccess) {
                                    it.copy(isSubmitting = false, success = true)
                                } else {
                                    it.copy(
                                        isSubmitting = false,
                                        success = false,
                                        errorMsg = result.exceptionOrNull()?.message ?: "Error desconocido al agendar"
                                    )
                                }
                            }
                        } catch (e: Exception) {
                            _bookState.update {
                                it.copy(isSubmitting = false, errorMsg = "Error inesperado: ${e.message}")
                            }
                        }
                    }
                }
            }
        }
    }

    // Limpiar estado después de agendar
    fun clearBookingResult() {
        _bookState.update { BookAppointmentUiState() }
    }

    // --- Acciones de citas (cancelar, confirmar, etc.) ---

    // Función de ayuda para ejecutar acciones de citas y manejar errores de forma centralizada
    private fun executeAppointmentAction(action: suspend () -> Result<Unit>, errorPrefix: String) {
        viewModelScope.launch {
            _myAppointmentsState.update { it.copy(errorMsg = null) }
            try {
                val result = action()
                if (result.isFailure) {
                    _myAppointmentsState.update {
                        it.copy(errorMsg = "$errorPrefix: ${result.exceptionOrNull()?.message}")
                    }
                }
            } catch (e: Exception) {
                _myAppointmentsState.update {
                    it.copy(errorMsg = "$errorPrefix: ${e.message}")
                }
            }
        }
    }

    // Cancelar una cita
    fun cancelAppointment(appointmentId: Long) {
        executeAppointmentAction(
            action = { repository.cancelAppointment(appointmentId) },
            errorPrefix = "Error al cancelar"
        )
    }

    // Confirmar una cita (para barberos)
    fun confirmAppointment(appointmentId: Long) {
        executeAppointmentAction(
            action = { repository.confirmAppointment(appointmentId) },
            errorPrefix = "Error al confirmar"
        )
    }

    // Completar una cita (para barberos)
    fun completeAppointment(appointmentId: Long) {
        executeAppointmentAction(
            action = { repository.completeAppointment(appointmentId) },
            errorPrefix = "Error al completar"
        )
    }

    // Cargar citas de un barbero específico
    fun loadBarberAppointments(barberId: Long) {
        viewModelScope.launch {
            try {
                _myAppointmentsState.update { it.copy(isLoading = true, errorMsg = null) }

                repository.getBarberAppointments(barberId).collectLatest { appointments ->
                    _myAppointmentsState.update {
                        it.copy(
                            allAppointments = appointments,
                            upcomingAppointments = appointments.filter {
                                it.status in listOf("pending", "confirmed")
                            },
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _myAppointmentsState.update {
                    it.copy(isLoading = false, errorMsg = "Error al cargar citas: ${e.message}")
                }
            }
        }
    }
}
