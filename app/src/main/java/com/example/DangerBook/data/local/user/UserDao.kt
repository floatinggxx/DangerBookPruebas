package com.example.uinavegacion.data.local.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    // Insertar un nuevo usuario
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(user: UserEntity): Long

    // Actualizar usuario (para foto de perfil, por ejemplo)
    @Update
    suspend fun update(user: UserEntity)

    // Obtener un usuario por email
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getByEmail(email: String): UserEntity?

    // Obtener un usuario por ID
    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getById(userId: Long): UserEntity?

    // Obtener todos los usuarios
    @Query("SELECT * FROM users ORDER BY id ASC")
    suspend fun getAll(): List<UserEntity>

    // Obtener usuarios por rol (ej: obtener todos los barberos)
    @Query("SELECT * FROM users WHERE role = :role ORDER BY name ASC")
    fun getUsersByRole(role: String): Flow<List<UserEntity>>

    // Obtener solo barberos disponibles (para selección en booking)
    @Query("SELECT * FROM users WHERE role = 'barber' ORDER BY name ASC")
    fun getAllBarbers(): Flow<List<UserEntity>>

    // Contar usuarios
    @Query("SELECT COUNT(*) FROM users")
    suspend fun count(): Int

    // Actualizar foto de perfil
    @Query("UPDATE users SET photoUri = :photoUri WHERE id = :userId")
    suspend fun updatePhoto(userId: Long, photoUri: String?)
}