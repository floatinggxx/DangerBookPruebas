package com.example.DangerBook.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.text.style.TextOverflow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(
    isAuthenticated: Boolean,
    userName: String?,
    userRole: String,
    onOpenDrawer: () -> Unit,
    onHome: () -> Unit,
    onLogin: () -> Unit,
    onRegister: () -> Unit,
    onServices: () -> Unit,
    onBookAppointment: () -> Unit,
    onMyAppointments: () -> Unit,
    onProfile: () -> Unit,
    onLogout: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
        ),
        title = {
            Text(
                text = "DangerBook",
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            IconButton(onClick = onOpenDrawer) {
                Icon(imageVector = Icons.Filled.Menu, contentDescription = "Menú")
            }
        },
        actions = {
            if (isAuthenticated) {
                // Acciones para usuarios autenticados
                IconButton(onClick = onServices) {
                    Icon(Icons.Filled.ContentCut, contentDescription = "Servicios")
                }
                IconButton(onClick = onBookAppointment) {
                    Icon(Icons.Filled.EventAvailable, contentDescription = "Agendar")
                }
                IconButton(onClick = onMyAppointments) {
                    Icon(Icons.Filled.Event, contentDescription = "Mis Citas")
                }
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Filled.MoreVert, contentDescription = "Más")
                }

                // Menú desplegable para usuarios autenticados
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    // Mostrar nombre del usuario
                    DropdownMenuItem(
                        text = { Text("Hola, $userName", style = MaterialTheme.typography.labelSmall) },
                        onClick = { },
                        enabled = false
                    )
                    DropdownMenuItem(
                        text = { Text("Servicios") },
                        onClick = { showMenu = false; onServices() },
                        leadingIcon = { Icon(Icons.Filled.ContentCut, null) }
                    )
                    DropdownMenuItem(
                        text = { Text("Agendar Cita") },
                        onClick = { showMenu = false; onBookAppointment() },
                        leadingIcon = { Icon(Icons.Filled.EventAvailable, null) }
                    )
                    DropdownMenuItem(
                        text = { Text("Mis Citas") },
                        onClick = { showMenu = false; onMyAppointments() },
                        leadingIcon = { Icon(Icons.Filled.Event, null) }
                    )
                    DropdownMenuItem(
                        text = { Text("Perfil") },
                        onClick = { showMenu = false; onProfile() },
                        leadingIcon = { Icon(Icons.Filled.AccountCircle, null) }
                    )
                    DropdownMenuItem(
                        text = { Text("Cerrar Sesión") },
                        onClick = { showMenu = false; onLogout() },
                        leadingIcon = { Icon(Icons.Filled.Logout, null) }
                    )
                }
            } else {
                // Acciones para usuarios NO autenticados
                IconButton(onClick = onHome) {
                    Icon(Icons.Filled.Home, contentDescription = "Home")
                }
                IconButton(onClick = onLogin) {
                    Icon(Icons.Filled.AccountCircle, contentDescription = "Login")
                }
                IconButton(onClick = onRegister) {
                    Icon(Icons.Filled.Person, contentDescription = "Registro")
                }
                IconButton(onClick = { showMenu = true }) {
                    Icon(Icons.Filled.MoreVert, contentDescription = "Más")
                }

                // Menú desplegable para usuarios NO autenticados
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Home") },
                        onClick = { showMenu = false; onHome() }
                    )
                    DropdownMenuItem(
                        text = { Text("Iniciar Sesión") },
                        onClick = { showMenu = false; onLogin() }
                    )
                    DropdownMenuItem(
                        text = { Text("Registrarse") },
                        onClick = { showMenu = false; onRegister() }
                    )
                }
            }
        }
    )
}