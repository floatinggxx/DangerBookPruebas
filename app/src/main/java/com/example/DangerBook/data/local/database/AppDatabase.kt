package com.example.DangerBook.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.uinavegacion.data.local.user.UserDao
import com.example.uinavegacion.data.local.user.UserEntity
import com.example.DangerBook.data.local.service.ServiceDao
import com.example.DangerBook.data.local.service.ServiceEntity
import com.example.DangerBook.data.local.barbero.BarberDao
import com.example.DangerBook.data.local.barbero.BarberEntity
import com.example.DangerBook.data.local.appointment.AppointmentDao
import com.example.DangerBook.data.local.appointment.AppointmentEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// Base de datos Room para DangerBook - incluye todas las entidades
@Database(
    entities = [
        UserEntity::class,
        ServiceEntity::class,
        BarberEntity::class,
        AppointmentEntity::class
    ],
    version = 1, // IMPORTANTE: Si ya tienes la BD creada, aumenta la versión a 2
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    // DAOs para acceder a las tablas
    abstract fun userDao(): UserDao
    abstract fun serviceDao(): ServiceDao
    abstract fun barberDao(): BarberDao
    abstract fun appointmentDao(): AppointmentDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        private const val BD_NAME = "dangerbook.db" // Nombre actualizado para la app

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    BD_NAME
                )
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Corrutina para precargar datos cuando se crea la BD por primera vez
                            CoroutineScope(Dispatchers.IO).launch {
                                val database = getInstance(context)
                                preloadData(database)
                            }
                        }
                    })
                    .fallbackToDestructiveMigration() // En desarrollo: borra y recrea si hay cambios
                    .build()

                INSTANCE = instance
                instance
            }
        }

        // Función privada para precargar datos iniciales
        private suspend fun preloadData(database: AppDatabase) {
            val userDao = database.userDao()
            val serviceDao = database.serviceDao()
            val barberDao = database.barberDao()

            // Precargar usuarios de prueba con diferentes roles
            if (userDao.count() == 0) {
                val users = listOf(
                    // Administrador
                    UserEntity(
                        name = "Admin DangerBook",
                        email = "admin@dangerbook.cl",
                        phone = "912345678",
                        password = "Admin123!",
                        role = "admin"
                    ),
                    // Barberos (vinculados con los de la tabla barbers)
                    UserEntity(
                        name = "Carlos Danger",
                        email = "carlos@dangerbook.cl",
                        phone = "987654321",
                        password = "Barber123!",
                        role = "barber"
                    ),
                    UserEntity(
                        name = "Miguel Estilo",
                        email = "miguel@dangerbook.cl",
                        phone = "987654322",
                        password = "Barber123!",
                        role = "barber"
                    ),
                    UserEntity(
                        name = "Andrés Master",
                        email = "andres@dangerbook.cl",
                        phone = "987654323",
                        password = "Barber123!",
                        role = "barber"
                    ),
                    // Clientes
                    UserEntity(
                        name = "Jose Pérez",
                        email = "jose@test.cl",
                        phone = "987654324",
                        password = "User123!",
                        role = "user"
                    ),
                    UserEntity(
                        name = "María González",
                        email = "maria@test.cl",
                        phone = "987654325",
                        password = "User123!",
                        role = "user"
                    )
                )
                users.forEach { userDao.insert(it) }
            }

            // Precargar servicios del Studio Danger
            if (serviceDao.count() == 0) {
                val services = listOf(
                    ServiceEntity(
                        name = "Corte Clásico",
                        description = "Corte tradicional con tijera y máquina. Incluye lavado y secado.",
                        price = 15000.0,
                        durationMinutes = 30,
                        isActive = true
                    ),
                    ServiceEntity(
                        name = "Corte Moderno",
                        description = "Corte con estilo actual, degradado y diseños. Incluye lavado.",
                        price = 18000.0,
                        durationMinutes = 45,
                        isActive = true
                    ),
                    ServiceEntity(
                        name = "Barba Completa",
                        description = "Arreglo de barba con máquina y navaja. Incluye toalla caliente.",
                        price = 12000.0,
                        durationMinutes = 30,
                        isActive = true
                    ),
                    ServiceEntity(
                        name = "Corte + Barba",
                        description = "Combo completo: corte de cabello y arreglo de barba.",
                        price = 25000.0,
                        durationMinutes = 60,
                        isActive = true
                    ),
                    ServiceEntity(
                        name = "Afeitado Tradicional",
                        description = "Afeitado clásico con navaja, toalla caliente y productos premium.",
                        price = 15000.0,
                        durationMinutes = 40,
                        isActive = true
                    ),
                    ServiceEntity(
                        name = "Tinte/Color",
                        description = "Aplicación de color o tinte para cabello o barba.",
                        price = 20000.0,
                        durationMinutes = 50,
                        isActive = true
                    )
                )
                serviceDao.insertAll(services)
            }

            // Precargar barberos del Studio Danger
            if (barberDao.count() == 0) {
                val barbers = listOf(
                    BarberEntity(
                        name = "Carlos Danger",
                        specialty = "Cortes clásicos y barba",
                        rating = 4.9,
                        isAvailable = true
                    ),
                    BarberEntity(
                        name = "Miguel Estilo",
                        specialty = "Cortes modernos y degradados",
                        rating = 4.8,
                        isAvailable = true
                    ),
                    BarberEntity(
                        name = "Andrés Master",
                        specialty = "Afeitado tradicional",
                        rating = 5.0,
                        isAvailable = true
                    )
                )
                barberDao.insertAll(barbers)
            }
        }
    }
}