package com.example.DangerBook
import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.DangerBook.data.local.database.AppDatabase
import com.example.DangerBook.data.local.notifications.NotificationHelper
import com.example.DangerBook.data.local.storage.UserPreferences
import com.example.DangerBook.data.repository.UserRepository
import com.example.DangerBook.data.repository.ServiceRepository
import com.example.DangerBook.data.repository.AppointmentRepository
import com.example.DangerBook.navigation.AppNavGraph
import com.example.DangerBook.ui.viewmodel.AuthViewModel
import com.example.DangerBook.ui.viewmodel.AuthViewModelFactory
import com.example.DangerBook.ui.viewmodel.ServicesViewModel
import com.example.DangerBook.ui.viewmodel.ServicesViewModelFactory
import com.example.DangerBook.ui.viewmodel.AppointmentViewModel
import com.example.DangerBook.ui.viewmodel.AppointmentViewModelFactory
import com.example.DangerBook.ui.theme.UINavegacionTheme
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    // Launcher para solicitar permiso de notificaciones
    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Permiso concedido
        } else {
            // Permiso denegado (la app seguirá funcionando sin notificaciones)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Crear canal de notificaciones
        NotificationHelper.createNotificationChannel(this)

        // Solicitar permiso de notificaciones en Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        setContent {
            UINavegacionTheme {
                AppRoot()
            }
        }
    }
}

@Composable
fun AppRoot() {
    val context = LocalContext.current.applicationContext
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()

    // DataStore para persistencia de sesión
    val userPrefs = remember { UserPreferences(context) }

    // Inicializar base de datos y DAOs
    val db = AppDatabase.getInstance(context)
    val userDao = db.userDao()
    val serviceDao = db.serviceDao()
    val barberDao = db.barberDao()
    val appointmentDao = db.appointmentDao()

    // Inicializar repositorios
    val userRepository = UserRepository(userDao)
    val serviceRepository = ServiceRepository(serviceDao, barberDao)
    val appointmentRepository = AppointmentRepository(appointmentDao, context)

    // Estado de autenticación desde DataStore
    val currentUserId by userPrefs.userId.collectAsStateWithLifecycle(null)
    val currentUserName by userPrefs.userName.collectAsStateWithLifecycle(null)
    val currentUserRole by userPrefs.userRole.collectAsStateWithLifecycle(null)

    // Crear AuthViewModel
    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(userRepository)
    )

    // Observar el estado de login para guardar sesión en DataStore
    LaunchedEffect(authViewModel) {
        authViewModel.login.collectLatest { loginState ->
            if (loginState.success && loginState.loggedUser != null) {
                val user = loginState.loggedUser
                userPrefs.saveUserSession(
                    userId = user.id,
                    userName = user.name,
                    userEmail = user.email,
                    userRole = user.role,
                    userPhoto = null
                )
            }
        }
    }

    // Crear ServicesViewModel
    val servicesViewModel: ServicesViewModel = viewModel(
        factory = ServicesViewModelFactory(serviceRepository)
    )

    // Crear AppointmentViewModel solo si hay usuario logueado
    val appointmentViewModel: AppointmentViewModel = viewModel(
        factory = AppointmentViewModelFactory(
            appointmentRepository,
            currentUserId ?: -1L
        )
    )

    // Callback para cerrar sesión
    val handleLogout: () -> Unit = {
        scope.launch {
            userPrefs.clearSession()
        }
    }

    // Callback para actualizar foto de perfil
    val handlePhotoUpdated: (String) -> Unit = { photoUri ->
        scope.launch {
            currentUserId?.let { userId ->
                userRepository.updateUserPhoto(userId, photoUri)
            }
        }
    }

    Surface(color = MaterialTheme.colorScheme.background) {
        AppNavGraph(
            navController = navController,
            authViewModel = authViewModel,
            servicesViewModel = servicesViewModel,
            appointmentViewModel = appointmentViewModel,
            currentUserId = currentUserId,
            currentUserName = currentUserName,
            currentUserRole = currentUserRole,
            onLogout = handleLogout,
            onPhotoUpdated = handlePhotoUpdated
        )
    }
}