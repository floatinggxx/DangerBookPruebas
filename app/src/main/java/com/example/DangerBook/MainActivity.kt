package com.example.DangerBook

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.DangerBook.data.local.database.AppDatabase
import com.example.DangerBook.data.repository.UserRepository
import com.example.DangerBook.navigation.AppNavGraph
import com.example.DangerBook.ui.viewmodel.AuthViewModel
import com.example.DangerBook.ui.viewmodel.AuthViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppRoot()
        }
    }
}


/*
* En Compose, Surface es un contenedor visual que viene de Material 3.Crea un bloque
*  que puedes personalizar con color, forma, sombra (elevación).
Sirve para aplicar un fondo (color, borde, elevación, forma) siguiendo las guías de diseño
* de Material.
Piensa en él como una “lona base” sobre la cual vas a pintar tu UI.
* Si cambias el tema a dark mode, colorScheme.background
* cambia automáticamente y el Surface pinta la pantalla con el nuevo color.
* */
@Composable // Indica que esta función dibuja UI
fun AppRoot() { // Raíz de la app para separar responsabilidades (se conserva)
    // ====== NUEVO: construcción de dependencias (Composition Root) ======
    val context = LocalContext.current.applicationContext
    // ^ Obtenemos el applicationContext para construir la base de datos de Room.

    val db = AppDatabase.getInstance(context)
    // ^ Singleton de Room. No crea múltiples instancias.

    val userDao = db.userDao()
    // ^ Obtenemos el DAO de usuarios desde la DB.

    val userRepository = UserRepository(userDao)
    // ^ Repositorio que encapsula la lógica de login/registro contra Room.

    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(userRepository)
    )
    // ^ Creamos el ViewModel con factory para inyectar el repositorio.
    //   Esto reemplaza cualquier uso anterior de listas en memoria (USERS).

    // ====== TU NAVEGACIÓN ORIGINAL ======
    val navController = rememberNavController() // Controlador de navegación (igual que antes)
    MaterialTheme { // Provee colores/tipografías Material 3 (igual que antes)
        Surface(color = MaterialTheme.colorScheme.background) { // Fondo general (igual que antes)

            // ====== MOD: pasamos el AuthViewModel a tu NavGraph ======
            // Si tu AppNavGraph ya recibía el VM o lo creaba adentro, lo mejor ahora es PASARLO
            // para que toda la app use la MISMA instancia que acabamos de inyectar.
            AppNavGraph(
                navController = navController,
                authViewModel = authViewModel // <-- NUEVO parámetro
            )
            // NOTA: Si tu AppNavGraph no tiene este parámetro aún, basta con agregarlo:
            // fun AppNavGraph(navController: NavHostController, authViewModel: AuthViewModel) { ... }
            // y luego pasar ese authViewModel a las pantallas Login/Register donde se use.
        }
    }
}