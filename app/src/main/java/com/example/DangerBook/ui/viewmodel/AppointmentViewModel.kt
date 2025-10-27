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

    // Cargar citas del usuario
    private fun loadUserAppointments() {
        viewModelScope.launch {
            try {
                _myAppointmentsState.update { it.copy(isLoading = true, errorMsg = null) }

                // Observar citas próximas
                launch {
                    repository.getUpcomingAppointments(userId).collectLatest { upcoming ->
                        _myAppointmentsState.update { it.copy(upcomingAppointments = upcoming) }
                    }
                }

                // Observar todas las citas
                launch {
                    repository.getUserAppointments(userId).collectLatest { all ->
                        _myAppointmentsState.update {
                            it.copy(allAppointments = all, isLoading = false)
                        }
                    }
                }

            } catch (e: Exception) {
                _myAppointmentsState.update {
                    it.copy(isLoading = false, errorMsg = "Error al cargar citas: ${e.message}")
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
            loadAvailableTimeSlots(state.selectedBarberId!!, state.selectedDate!!, durationMinutes)
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
            try {
                _bookState.update { it.copy(isLoadingSlots = true) }

                val slots = repository.getAvailableTimeSlotsForDay(barberId, date, durationMinutes)

                _bookState.update {
                    it.copy(
                        availableTimeSlots = slots,
                        isLoadingSlots = false
                    )
                }

            } catch (e: Exception) {
                _bookState.update {
                    it.copy(
                        isLoadingSlots = false,
                        errorMsg = "Error al cargar horarios: ${e.message}"
                    )
                }
            }
        }
    }

    // Validar si se puede enviar
    private fun recomputeCanSubmit() {
        val s = _bookState.value
        val can = s.selectedServiceId != null &&
                s.selectedBarberId != null &&
                s.selectedDate != null &&
                s.selectedTimeSlot != null
        _bookState.update { it.copy(canSubmit = can) }
    }

    // Enviar (crear cita)
    fun submitBooking(serviceDurationMinutes: Int) {
        val s = _bookState.value
        if (!s.canSubmit || s.isSubmitting) return

        viewModelScope.launch {
            try {
                _bookState.update { it.copy(isSubmitting = true, errorMsg = null) }

                val result = repository.createAppointment(
                    userId = userId,
                    barberId = s.selectedBarberId,
                    serviceId = s.selectedServiceId!!,
                    dateTime = s.selectedTimeSlot!!,
                    durationMinutes = serviceDurationMinutes,
                    notes = s.notes.ifBlank { null }
                )

                _bookState.update {
                    if (result.isSuccess) {
                        it.copy(isSubmitting = false, success = true, errorMsg = null)
                    } else {
                        it.copy(
                            isSubmitting = false,
                            success = false,
                            errorMsg = result.exceptionOrNull()?.message ?: "Error al agendar"
                        )
                    }
                }

            } catch (e: Exception) {
                _bookState.update {
                    it.copy(
                        isSubmitting = false,
                        errorMsg = "Error inesperado: ${e.message}"
                    )
                }
            }
        }
    }

    // Limpiar estado después de agendar
    fun clearBookingResult() {
        _bookState.value = BookAppointmentUiState()
    }

    // Cancelar una cita
    fun cancelAppointment(appointmentId: Long) {
        viewModelScope.launch {
            try {
                val result = repository.cancelAppointment(appointmentId)
                if (result.isFailure) {
                    _myAppointmentsState.update {
                        it.copy(errorMsg = result.exceptionOrNull()?.message)
                    }
                }
                // Las citas se actualizarán automáticamente por el Flow
            } catch (e: Exception) {
                _myAppointmentsState.update {
                    it.copy(errorMsg = "Error al cancelar: ${e.message}")
                }
            }
        }
    }

    // Confirmar una cita (para barberos)
    fun confirmAppointment(appointmentId: Long) {
        viewModelScope.launch {
            try {
                repository.confirmAppointment(appointmentId)
            } catch (e: Exception) {
                _myAppointmentsState.update {
                    it.copy(errorMsg = "Error al confirmar: ${e.message}")
                }
            }
        }
    }

    // Completar una cita (para barberos)
    fun completeAppointment(appointmentId: Long) {
        viewModelScope.launch {
            try {
                repository.completeAppointment(appointmentId)
            } catch (e: Exception) {
                _myAppointmentsState.update {
                    it.copy(errorMsg = "Error al completar: ${e.message}")
                }
            }
        }
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