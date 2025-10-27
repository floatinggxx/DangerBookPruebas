package com.example.DangerBook.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

// Estructura de un ítem del drawer
data class DrawerItem(
    val label: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)

@Composable
fun AppDrawer(
    currentRoute: String?,
    items: List<DrawerItem>,
    modifier: Modifier = Modifier
) {
    ModalDrawerSheet(
        modifier = modifier
    ) {
        // Header del drawer con branding de DangerBook
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .padding(24.dp)
        ) {
            Column {
                Text(
                    text = "DangerBook",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Text(
                    text = "Studio Danger",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                )
            }
        }

        Divider()
        Spacer(Modifier.height(8.dp))

        // Ítems del menú
        items.forEach { item ->
            NavigationDrawerItem(
                label = { Text(item.label) },
                selected = false, // Puedes implementar lógica para marcar la ruta actual
                onClick = item.onClick,
                icon = { Icon(item.icon, contentDescription = item.label) },
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                colors = NavigationDrawerItemDefaults.colors()
            )
        }
    }
}

// Helper: Ítems para usuarios NO autenticados
@Composable
fun defaultDrawerItems(
    onHome: () -> Unit,
    onLogin: () -> Unit,
    onRegister: () -> Unit
): List<DrawerItem> = listOf(
    DrawerItem("Home", Icons.Filled.Home, onHome),
    DrawerItem("Iniciar Sesión", Icons.Filled.AccountCircle, onLogin),
    DrawerItem("Registrarse", Icons.Filled.Person, onRegister)
)

// Helper: Ítems para usuarios AUTENTICADOS
@Composable
fun authenticatedDrawerItems(
    userName: String,
    userRole: String, // NUEVO: rol del usuario
    onServices: () -> Unit,
    onBookAppointment: () -> Unit,
    onMyAppointments: () -> Unit,
    onBarberAppointments: () -> Unit, // NUEVO
    onAdminDashboard: () -> Unit, // NUEVO
    onProfile: () -> Unit,
    onLogout: () -> Unit
): List<DrawerItem> {
    val commonItems = mutableListOf(
        DrawerItem("Servicios", Icons.Filled.ContentCut, onServices),
        DrawerItem("Perfil", Icons.Filled.AccountCircle, onProfile)
    )

    // Ítems específicos según rol
    when (userRole) {
        "admin" -> {
            commonItems.add(0, DrawerItem("Panel Admin", Icons.Filled.AdminPanelSettings, onAdminDashboard))
            commonItems.add(DrawerItem("Gestionar Citas", Icons.Filled.EventNote, onMyAppointments))
        }
        "barber" -> {
            commonItems.add(DrawerItem("Mis Citas Asignadas", Icons.Filled.Event, onBarberAppointments))
        }
        else -> { // "user"
            commonItems.add(DrawerItem("Agendar Cita", Icons.Filled.EventAvailable, onBookAppointment))
            commonItems.add(DrawerItem("Mis Citas", Icons.Filled.Event, onMyAppointments))
        }
    }

    commonItems.add(DrawerItem("Cerrar Sesión", Icons.Filled.Logout, onLogout))

    return commonItems
}