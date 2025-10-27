package com.example.DangerBook.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.DangerBook.data.repository.ServiceRepository

// Factory para crear instancias de ServicesViewModel con sus dependencias
class ServicesViewModelFactory(
    private val repository: ServiceRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ServicesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ServicesViewModel(repository) as T
        }
        throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
    }
}