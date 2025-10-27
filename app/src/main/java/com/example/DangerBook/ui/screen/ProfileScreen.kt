package com.example.DangerBook.ui.screen

import android.Manifest
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.DangerBook.data.local.storage.UserPreferences
import com.example.DangerBook.ui.utils.CameraHelper
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    userId: Long,
    userName: String,
    userRole: String, // NUEVO: mostrar el rol
    onLogout: () -> Unit,
    onPhotoUpdated: (String) -> Unit // NUEVO: callback para actualizar foto
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val userPrefs = remember { UserPreferences(context) }

    // Estado para la foto de perfil
    val currentPhoto by userPrefs.userPhoto.collectAsStateWithLifecycle(null)
    var photoUriString by remember { mutableStateOf(currentPhoto) }
    var pendingCaptureUri by remember { mutableStateOf<Uri?>(null) }

    // Estados para diálogos
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showPhotoDialog by remember { mutableStateOf(false) }

    // Launcher para la cámara
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && pendingCaptureUri != null) {
            val newPhotoUri = pendingCaptureUri.toString()
            photoUriString = newPhotoUri
            scope.launch {
                userPrefs.updateUserPhoto(newPhotoUri)
                onPhotoUpdated(newPhotoUri)
            }
            Toast.makeText(context, "Foto actualizada correctamente", Toast.LENGTH_SHORT).show()
        } else {
            pendingCaptureUri = null
            Toast.makeText(context, "No se tomó ninguna foto", Toast.LENGTH_SHORT).show()
        }
    }

    // Launcher para permisos de cámara
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val file = CameraHelper.createTempImageFile(context)
            val uri = CameraHelper.getImageUriForFile(context, file)
            pendingCaptureUri = uri
            takePictureLauncher.launch(uri)
        } else {
            Toast.makeText(context, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
        }
    }

    // Función para abrir la cámara
    val openCamera = {
        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Text(
                text = "Mi Perfil",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            // Tarjeta de información del usuario
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
                    // Avatar (ícono circular)
                    Surface(
                        modifier = Modifier.size(80.dp),
                        shape = MaterialTheme.shapes.large,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Filled.Person,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    // Nombre del usuario
                    Text(
                        text = userName,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(Modifier.height(4.dp))

                    // ID del usuario
                    Text(
                        text = "ID: #$userId",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            // Sección de información
            Text(
                text = "Información",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            // Opciones del perfil
            ProfileOption(
                icon = Icons.Filled.AccountCircle,
                title = "Datos personales",
                subtitle = "Actualiza tu información",
                onClick = { /* TODO: Implementar edición de perfil */ }
            )

            ProfileOption(
                icon = Icons.Filled.Lock,
                title = "Cambiar contraseña",
                subtitle = "Actualiza tu contraseña",
                onClick = { /* TODO: Implementar cambio de contraseña */ }
            )

            ProfileOption(
                icon = Icons.Filled.Notifications,
                title = "Notificaciones",
                subtitle = "Gestiona tus notificaciones",
                onClick = { /* TODO: Implementar configuración de notificaciones */ }
            )

            Spacer(Modifier.weight(1f))

            // Botón de cerrar sesión
            OutlinedButton(
                onClick = { showLogoutDialog = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Icon(Icons.Filled.Logout, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Cerrar Sesión", style = MaterialTheme.typography.titleMedium)
            }

            // Versión de la app
            Text(
                text = "DangerBook v1.0.0",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }

    // Diálogo de confirmación de logout
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            icon = { Icon(Icons.Filled.Logout, contentDescription = null) },
            title = { Text("¿Cerrar sesión?") },
            text = { Text("¿Estás seguro de que deseas cerrar tu sesión?") },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Cerrar Sesión")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

// Componente reutilizable para opciones del perfil
@Composable
private fun ProfileOption(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ícono
            Surface(
                modifier = Modifier.size(48.dp),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(Modifier.width(16.dp))

            // Textos
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            // Flecha
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            )
        }
    }
}