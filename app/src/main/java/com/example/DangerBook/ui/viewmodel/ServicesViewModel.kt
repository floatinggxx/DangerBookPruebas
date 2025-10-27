package com.example.DangerBook.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.DangerBook.data.local.service.ServiceEntity
import com.example.DangerBook.data.local.barbero.BarberEntity
import com.example.DangerBook.data.repository.ServiceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

// Estado de UI para la pantalla de servicios
data class ServicesUiState(
    val services: List<ServiceEntity> = emptyList(), // Lista de servicios disponibles
    val barbers: List<BarberEntity> = emptyList(), // Lista de barberos disponibles
    val isLoading: Boolean = true, // Indica si está cargando datos
    val errorMsg: String? = null // Mensaje de error si algo falla
)

// ViewModel para gestionar los servicios y barberos
class ServicesViewModel(
    private val repository: ServiceRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ServicesUiState())
    val state: StateFlow<ServicesUiState> = _state

    init {
        // Cargar servicios y barberos al iniciar
        loadServicesAndBarbers()
    }

    private fun loadServicesAndBarbers() {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isLoading = true, errorMsg = null)

                // Observar servicios activos (Flow)
                launch {
                    repository.getAllActiveServices().collectLatest { services ->
                        _state.value = _state.value.copy(
                            services = services,
                            isLoading = false
                        )
                    }
                }

                // Observar barberos disponibles (Flow)
                launch {
                    repository.getAllAvailableBarbers().collectLatest { barbers ->
                        _state.value = _state.value.copy(
                            barbers = barbers,
                            isLoading = false
                        )
                    }
                }

            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMsg = "Error al cargar servicios: ${e.message}"
                )
            }
        }
    }
}