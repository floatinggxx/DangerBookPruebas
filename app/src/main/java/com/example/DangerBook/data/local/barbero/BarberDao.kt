package com.example.DangerBook.data.local.barbero

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

// DAO para operaciones de barberos en la base de datos
@Dao
interface BarberDao {

    // Insertar múltiples barberos (útil para precarga)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(barbers: List<BarberEntity>)

    // Insertar un barbero
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(barber: BarberEntity): Long

    // Obtener todos los barberos disponibles ordenados por nombre
    @Query("SELECT * FROM barbers WHERE isAvailable = 1 ORDER BY name ASC")
    fun getAllAvailable(): Flow<List<BarberEntity>>

    // Obtener un barbero por ID
    @Query("SELECT * FROM barbers WHERE id = :barberId")
    suspend fun getById(barberId: Long): BarberEntity?

    // Contar barberos (útil para verificar precarga)
    @Query("SELECT COUNT(*) FROM barbers")
    suspend fun count(): Int
}