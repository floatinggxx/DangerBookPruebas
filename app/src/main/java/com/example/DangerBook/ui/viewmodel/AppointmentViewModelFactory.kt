package com.example.DangerBook.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.DangerBook.data.repository.AppointmentRepository

// Factory para crear instancias de AppointmentViewModel con sus dependencias
class AppointmentViewModelFactory(
    private val repository: AppointmentRepository,
    private val userId: Long // Usuario actual logueado
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppointmentViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AppointmentViewModel(repository, userId) as T
        }
        throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
    }
}