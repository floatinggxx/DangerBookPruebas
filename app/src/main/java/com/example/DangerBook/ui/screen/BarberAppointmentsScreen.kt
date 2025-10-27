package com.example.DangerBook.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.DangerBook.data.local.appointment.AppointmentEntity
import java.text.SimpleDateFormat
import java.util.*

// Pantalla para que los barberos vean sus citas asignadas
@Composable
fun BarberAppointmentsScreen(
    appointments: List<AppointmentEntity>, // Citas del barbero
    onConfirmAppointment: (Long) -> Unit, // Confirmar una cita
    onCompleteAppointment: (Long) -> Unit // Marcar como completada
) {
    var selectedTab by remember { mutableStateOf(0) }

    // Filtrar citas según pestaña
    val filteredAppointments = when (selectedTab) {
        0 -> appointments.filter { it.status in listOf("pending", "confirmed") } // Activas
        1 -> appointments.filter { it.status == "completed" } // Completadas
        else -> appointments // Todas
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ContentCut,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Mis Citas Asignadas",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(Modifier.height(8.dp))

                    // Tabs
                    TabRow(selectedTabIndex = selectedTab) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = { Text("Activas") }
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = { Text("Completadas") }
                        )
                        Tab(
                            selected = selectedTab == 2,
                            onClick = { selectedTab = 2 },
                            text = { Text("Todas") }
                        )
                    }
                }
            }

            // Contenido
            if (filteredAppointments.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.EventBusy,
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = "No hay citas en esta categoría",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredAppointments) { appointment ->
                        BarberAppointmentCard(
                            appointment = appointment,
                            onConfirm = { onConfirmAppointment(appointment.id) },
                            onComplete = { onCompleteAppointment(appointment.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BarberAppointmentCard(
    appointment: AppointmentEntity,
    onConfirm: () -> Unit,
    onComplete: () -> Unit
) {
    val dateFormat = SimpleDateFormat("EEEE, dd 'de' MMMM", Locale("es", "CL"))
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
    val date = Date(appointment.dateTime)

    val statusColor = when (appointment.status) {
        "pending" -> MaterialTheme.colorScheme.tertiary
        "confirmed" -> MaterialTheme.colorScheme.primary
        "completed" -> MaterialTheme.colorScheme.outline
        else -> MaterialTheme.colorScheme.error
    }

    val statusText = when (appointment.status) {
        "pending" -> "Pendiente"
        "confirmed" -> "Confirmada"
        "completed" -> "Completada"
        else -> "Cancelada"
    }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = statusColor.copy(alpha = 0.15f),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = statusText,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelMedium,
                        color = statusColor,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Text(
                    text = "#${appointment.id}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }

            Spacer(Modifier.height(12.dp))

            // Fecha y hora
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.CalendarToday,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.width(8.dp))
                Column {
                    Text(
                        text = dateFormat.format(date).capitalize(Locale.getDefault()),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "a las ${timeFormat.format(date)} hrs",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // Duración
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.AccessTime,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Duración: ${appointment.durationMinutes} minutos",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            // ID del cliente
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Cliente ID: #${appointment.userId}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            // Notas
            if (!appointment.notes.isNullOrBlank()) {
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = Icons.Filled.StickyNote2,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Nota: ${appointment.notes}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
            }

            // Acciones
            if (appointment.status == "pending") {
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = onConfirm,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(Icons.Filled.CheckCircle, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Confirmar Cita")
                }
            } else if (appointment.status == "confirmed") {
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = onComplete,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary
                    )
                ) {
                    Icon(Icons.Filled.Done, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Marcar como Completada")
                }
            }
        }
    }
}