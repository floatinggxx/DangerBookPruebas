package com.example.DangerBook.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    isAuthenticated: Boolean,
    userRole: String?, // NUEVO: mostrar rol del usuario
    onGoLogin: () -> Unit,
    onGoRegister: () -> Unit,
    onGoServices: () -> Unit
) {
    val bg = MaterialTheme.colorScheme.background

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bg)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Logo/Ícono de la barbería
            Icon(
                imageVector = Icons.Filled.ContentCut,
                contentDescription = "Studio Danger",
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            // Título
            Text(
                text = "DangerBook",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Studio Danger",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )

            Spacer(Modifier.height(8.dp))

            // Tarjeta de bienvenida
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isAuthenticated) {
                            "¡Bienvenido de vuelta!"
                        } else {
                            "Bienvenido a DangerBook"
                        },
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = if (isAuthenticated) {
                            "Agenda tu próxima cita con nuestros expertos barberos. Ofrecemos cortes clásicos, modernos, arreglo de barba y mucho más."
                        } else {
                            "La mejor barbería de la ciudad. Agenda tu cita online de forma rápida y sencilla."
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // Botones de acción
            if (isAuthenticated) {
                // Usuario autenticado: mostrar botón para ver servicios
                Button(
                    onClick = onGoServices,
                    modifier = Modifier.fillMaxWidth(0.8f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(Icons.Filled.ContentCut, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Ver Servicios", style = MaterialTheme.typography.titleMedium)
                }
            } else {
                // Usuario NO autenticado: mostrar botones de login/registro
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    OutlinedButton(
                        onClick = onGoLogin,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Iniciar Sesión")
                    }

                    Button(
                        onClick = onGoRegister,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text("Registrarse")
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Info adicional
            Text(
                text = "• Horario: Lun-Sáb 9:00 - 20:00\n• Ubicación: Centro de la ciudad\n• Profesionales certificados",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
        }
    }
}