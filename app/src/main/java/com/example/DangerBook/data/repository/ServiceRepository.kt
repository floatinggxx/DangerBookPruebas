package com.example.DangerBook.data.repository

import com.example.DangerBook.data.local.barbero.BarberDao
import com.example.DangerBook.data.local.barbero.BarberEntity
import com.example.DangerBook.data.local.service.ServiceDao
import com.example.DangerBook.data.local.service.ServiceEntity
import kotlinx.coroutines.flow.Flow

// Repositorio para manejar la lógica de negocio de servicios y barberos
class ServiceRepository(
    private val serviceDao: ServiceDao,
    private val barberDao: BarberDao
) {

    // Obtener todos los servicios activos (como Flow para observar cambios)
    fun getAllActiveServices(): Flow<List<ServiceEntity>> {
        return serviceDao.getAllActive()
    }

    // Obtener un servicio específico por ID
    suspend fun getServiceById(serviceId: Long): ServiceEntity? {
        return serviceDao.getById(serviceId)
    }

    // Obtener todos los barberos disponibles (como Flow)
    fun getAllAvailableBarbers(): Flow<List<BarberEntity>> {
        return barberDao.getAllAvailable()
    }

    // Obtener un barbero específico por ID
    suspend fun getBarberById(barberId: Long): BarberEntity? {
        return barberDao.getById(barberId)
    }
}