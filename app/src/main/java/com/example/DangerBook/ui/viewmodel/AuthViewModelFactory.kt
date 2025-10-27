package com.example.DangerBook.ui.viewmodel

import androidx.lifecycle.ViewModel                              // Tipo base ViewModel
import androidx.lifecycle.ViewModelProvider                      // Factory de ViewModels
import com.example.DangerBook.data.repository.UserRepository   // Repositorio a inyectar

// Factory simple para crear AuthViewModel con su UserRepository.
class AuthViewModelFactory(
    private val repository: UserRepository                       // Dependencia que inyectaremos
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")                                   // Evitar warning de cast genérico
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Si solicitan AuthViewModel, lo creamos con el repo.
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(repository) as T
        }
        // Si piden otra clase, lanzamos error descriptivo.
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}