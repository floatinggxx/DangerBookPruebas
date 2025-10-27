package com.example.DangerBook.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.DangerBook.data.local.service.ServiceEntity
import com.example.DangerBook.ui.viewmodel.ServicesViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ServicesScreen(
    vm: ServicesViewModel,
    onBookService: (Long) -> Unit // Callback al seleccionar un servicio para agendar
) {
    val state by vm.state.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (state.isLoading) {
            // Mostrar loading
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else if (state.errorMsg != null) {
            // Mostrar error
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = state.errorMsg ?: "Error desconocido",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            // Mostrar lista de servicios
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header
                item {
                    Column {
                        Text(
                            text = "Nuestros Servicios",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Selecciona el servicio que deseas agendar",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                        )
                        Spacer(Modifier.height(16.dp))
                    }
                }

                // Lista de servicios
                items(state.services) { service ->
                    ServiceCard(
                        service = service,
                        onBook = { onBookService(service.id) }
                    )
                }

                // Mensaje si no hay servicios
                if (state.services.isEmpty()) {
                    item {
                        Text(
                            text = "No hay servicios disponibles en este momento",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                            modifier = Modifier.padding(32.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ServiceCard(
    service: ServiceEntity,
    onBook: () -> Unit
) {
    // Formatear precio en formato chileno
    val priceFormat = NumberFormat.getCurrencyInstance(Locale("es", "CL"))
    val formattedPrice = priceFormat.format(service.price)

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Nombre del servicio
            Text(
                text = service.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(8.dp))

            // Descripción
            Text(
                text = service.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )

            Spacer(Modifier.height(12.dp))

            // Detalles (precio y duración)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    // Precio
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.AttachMoney,
                            contentDescription = "Precio",
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = formattedPrice,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(Modifier.height(4.dp))

                    // Duración
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.AccessTime,
                            contentDescription = "Duración",
                            modifier = Modifier.size(20.dp),
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = "${service.durationMinutes} min",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }

                // Botón de agendar
                Button(
                    onClick = onBook,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Agendar")
                }
            }
        }
    }
}