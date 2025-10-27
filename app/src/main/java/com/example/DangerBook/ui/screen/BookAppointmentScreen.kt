package com.example.DangerBook.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.DangerBook.ui.viewmodel.AppointmentViewModel
import com.example.DangerBook.ui.viewmodel.ServicesViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun BookAppointmentScreen(
    appointmentVm: AppointmentViewModel,
    servicesVm: ServicesViewModel,
    onAppointmentBooked: () -> Unit // Navegar después de agendar
) {
    val bookState by appointmentVm.bookState.collectAsStateWithLifecycle()
    val servicesState by servicesVm.state.collectAsStateWithLifecycle()

    // Navegar si la cita se creó exitosamente
    LaunchedEffect(bookState.success) {
        if (bookState.success) {
            appointmentVm.clearBookingResult()
            onAppointmentBooked()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            item {
                Text(
                    text = "Agendar Nueva Cita",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Paso 1: Seleccionar servicio
            item {
                Text(
                    text = "1. Selecciona el servicio",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(servicesState.services) { service ->
                        FilterChip(
                            selected = bookState.selectedServiceId == service.id,
                            onClick = {
                                appointmentVm.onSelectService(service.id, service.durationMinutes)
                            },
                            label = { Text(service.name) },
                            leadingIcon = if (bookState.selectedServiceId == service.id) {
                                { Icon(Icons.Filled.Check, contentDescription = null, Modifier.size(18.dp)) }
                            } else null
                        )
                    }
                }
            }

            // Paso 2: Seleccionar barbero
            item {
                Text(
                    text = "2. Selecciona tu barbero",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Opción "Cualquier barbero"
                    item {
                        FilterChip(
                            selected = bookState.selectedBarberId == null,
                            onClick = { appointmentVm.onSelectBarber(null) },
                            label = { Text("Cualquier barbero") },
                            leadingIcon = if (bookState.selectedBarberId == null) {
                                { Icon(Icons.Filled.Check, contentDescription = null, Modifier.size(18.dp)) }
                            } else null
                        )
                    }

                    // Barberos específicos
                    items(servicesState.barbers) { barber ->
                        FilterChip(
                            selected = bookState.selectedBarberId == barber.id,
                            onClick = { appointmentVm.onSelectBarber(barber.id) },
                            label = { Text(barber.name) },
                            leadingIcon = if (bookState.selectedBarberId == barber.id) {
                                { Icon(Icons.Filled.Check, contentDescription = null, Modifier.size(18.dp)) }
                            } else null
                        )
                    }
                }
            }

            // Paso 3: Seleccionar fecha
            item {
                Text(
                    text = "3. Selecciona la fecha",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(8.dp))

                // DatePicker simplificado (próximos 7 días)
                DateSelector(
                    selectedDate = bookState.selectedDate,
                    onDateSelected = { date ->
                        val selectedService = servicesState.services.find { it.id == bookState.selectedServiceId }
                        if (selectedService != null) {
                            appointmentVm.onSelectDate(date, selectedService.durationMinutes)
                        }
                    }
                )
            }

            // Paso 4: Seleccionar horario
            if (bookState.selectedDate != null && bookState.selectedBarberId != null) {
                item {
                    Text(
                        text = "4. Selecciona el horario",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(8.dp))

                    if (bookState.isLoadingSlots) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(32.dp))
                        }
                    } else if (bookState.availableTimeSlots.isEmpty()) {
                        Text(
                            text = "No hay horarios disponibles para esta fecha",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                    } else {
                        TimeSlotGrid(
                            timeSlots = bookState.availableTimeSlots,
                            selectedSlot = bookState.selectedTimeSlot,
                            onSlotSelected = { appointmentVm.onSelectTimeSlot(it) }
                        )
                    }
                }
            }

            // Notas adicionales
            item {
                Text(
                    text = "Notas adicionales (opcional)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = bookState.notes,
                    onValueChange = { appointmentVm.onNotesChange(it) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Ej: Prefiero un corte más conservador") },
                    maxLines = 3
                )
            }

            // Error message
            if (bookState.errorMsg != null) {
                item {
                    Text(
                        text = bookState.errorMsg!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            // Botón de confirmar
            item {
                Button(
                    onClick = {
                        val selectedService = servicesState.services.find { it.id == bookState.selectedServiceId }
                        if (selectedService != null) {
                            appointmentVm.submitBooking(selectedService.durationMinutes)
                        }
                    },
                    enabled = bookState.canSubmit && !bookState.isSubmitting,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    if (bookState.isSubmitting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Agendando...")
                    } else {
                        Text("Confirmar Cita", style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
    }
}

// Selector de fecha (próximos 7 días)
@Composable
private fun DateSelector(
    selectedDate: Calendar?,
    onDateSelected: (Calendar) -> Unit
) {
    val dateFormat = SimpleDateFormat("EEE dd/MM", Locale("es", "CL"))
    val today = Calendar.getInstance()

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(7) { index ->
            val date = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, index)
            }

            val isSelected = selectedDate?.get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR) &&
                    selectedDate?.get(Calendar.YEAR) == date.get(Calendar.YEAR)

            FilterChip(
                selected = isSelected,
                onClick = { onDateSelected(date) },
                label = { Text(dateFormat.format(date.time)) },
                leadingIcon = if (isSelected) {
                    { Icon(Icons.Filled.CalendarToday, contentDescription = null, Modifier.size(18.dp)) }
                } else null
            )
        }
    }
}

// Grid de horarios disponibles
@Composable
private fun TimeSlotGrid(
    timeSlots: List<Long>,
    selectedSlot: Long?,
    onSlotSelected: (Long) -> Unit
) {
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        timeSlots.chunked(3).forEach { rowSlots ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowSlots.forEach { slot ->
                    val isSelected = selectedSlot == slot

                    OutlinedCard(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onSlotSelected(slot) },
                        colors = CardDefaults.outlinedCardColors(
                            containerColor = if (isSelected) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.surface
                            }
                        ),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = if (isSelected) {
                                androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.primary)
                            } else {
                                androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.outline)
                            }
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = timeFormat.format(Date(slot)),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurface
                                }
                            )
                        }
                    }
                }

                // Rellenar espacios vacíos si la fila no está completa
                repeat(3 - rowSlots.size) {
                    Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}