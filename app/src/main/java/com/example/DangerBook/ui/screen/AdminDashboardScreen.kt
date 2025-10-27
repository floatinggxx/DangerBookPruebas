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
import androidx.compose.ui.unit.dp

// Pantalla de administración (dashboard para admin)
@Composable
fun AdminDashboardScreen(
    totalAppointments: Int, // Total de citas en el sistema
    totalUsers: Int, // Total de usuarios
    totalBarbers: Int, // Total de barberos
    onManageUsers: () -> Unit, // Ir a gestión de usuarios
    onManageServices: () -> Unit, // Ir a gestión de servicios
    onViewReports: () -> Unit // Ver reportes
) {
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.AdminPanelSettings,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(32.dp)
                    )
                    Text(
                        text = "Panel de Administración",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            // Estadísticas
            item {
                Text(
                    text = "Estadísticas Generales",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "Citas",
                        value = totalAppointments.toString(),
                        icon = Icons.Filled.Event,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Usuarios",
                        value = totalUsers.toString(),
                        icon = Icons.Filled.People,
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                StatCard(
                    title = "Barberos Activos",
                    value = totalBarbers.toString(),
                    icon = Icons.Filled.ContentCut,
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Acciones rápidas
            item {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Gestión",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            item {
                AdminActionCard(
                    title = "Gestionar Usuarios",
                    subtitle = "Ver, editar y crear usuarios",
                    icon = Icons.Filled.ManageAccounts,
                    onClick = onManageUsers
                )
            }

            item {
                AdminActionCard(
                    title = "Gestionar Servicios",
                    subtitle = "Agregar, editar o eliminar servicios",
                    icon = Icons.Filled.Build,
                    onClick = onManageServices
                )
            }

            item {
                AdminActionCard(
                    title = "Ver Reportes",
                    subtitle = "Estadísticas y análisis detallados",
                    icon = Icons.Filled.Analytics,
                    onClick = onViewReports
                )
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier,
        colors = CardDefaults.elevatedCardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(40.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun AdminActionCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.medium,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(56.dp),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            )
        }
    }
}