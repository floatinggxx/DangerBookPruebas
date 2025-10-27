package com.example.DangerBook.navigation

// Clase sellada para rutas: evita \"strings mágicos\" y facilita refactors
sealed class Route(val path: String) {
    // Rutas públicas (sin autenticación)
    object Home : Route("home")
    object Login : Route("login")
    object Register : Route("register")

    // Rutas privadas (requieren autenticación)
    object Services : Route("services") // Pantalla de servicios disponibles
    object BookAppointment : Route("book_appointment") // Agendar nueva cita
    object MyAppointments : Route("my_appointments") // Ver mis citas
    object Profile : Route("profile") // Perfil del usuario

    // Rutas para barberos (solo role = "barber")
    object BarberAppointments : Route("barber_appointments") // Citas asignadas al barbero

    // Rutas para administradores (solo role = "admin")
    object AdminDashboard : Route("admin_dashboard") // Panel de administración
}

/*
 * Estructura de navegación de DangerBook:
 *
 * PÚBLICAS (cualquiera puede acceder):
 * - Home: Página de bienvenida
 * - Login: Iniciar sesión
 * - Register: Crear cuenta
 *
 * PRIVADAS (requiere login):
 * - Services: Ver servicios de la barbería
 * - BookAppointment: Agendar una cita
 * - MyAppointments: Ver/cancelar mis citas
 * - Profile: Ver/editar perfil
 */