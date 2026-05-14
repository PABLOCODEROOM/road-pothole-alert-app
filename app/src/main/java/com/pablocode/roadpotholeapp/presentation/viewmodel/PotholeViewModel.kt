package com.pablocode.roadpotholeapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pablocode.roadpotholeapp.domain.model.Pothole
import com.pablocode.roadpotholeapp.domain.model.Result
import com.pablocode.roadpotholeapp.domain.repository.PotholeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PotholeViewModel @Inject constructor(
    private val potholeRepository: PotholeRepository
) : ViewModel() {

    private val _allPotholes = MutableStateFlow<List<Pothole>>(emptyList())
    val allPotholes: StateFlow<List<Pothole>> = _allPotholes

    private val _nearbyPotholes = MutableStateFlow<List<Pothole>>(emptyList())
    val nearbyPotholes: StateFlow<List<Pothole>> = _nearbyPotholes

    private val _selectedPothole = MutableStateFlow<Pothole?>(null)
    val selectedPothole: StateFlow<Pothole?> = _selectedPothole

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        loadAllPotholes()
    }

    private fun loadAllPotholes() {
        viewModelScope.launch {
            _isLoading.value = true
            potholeRepository.getAllPotholes()
                .catch { e ->
                    _error.value = e.message
                    _isLoading.value = false
                }
                .collect { potholes ->
                    _allPotholes.value = potholes
                    _isLoading.value = false
                    _error.value = null
                }
        }
    }

    fun loadNearbyPotholes(latitude: Double, longitude: Double, radiusInMeters: Float = 500f) {
        viewModelScope.launch {
            potholeRepository.getNearbyPotholes(latitude, longitude, radiusInMeters)
                .catch { e ->
                    _error.value = e.message
                }
                .collect { potholes ->
                    _nearbyPotholes.value = potholes
                    _error.value = null
                }
        }
    }

    fun selectPothole(potholeId: String) {
        viewModelScope.launch {
            val result = potholeRepository.getPotholeById(potholeId)
            when (result) {
                is Result.Success -> {
                    _selectedPothole.value = result.data
                    _error.value = null
                }
                is Result.Error -> {
                    _error.value = result.exception.message
                }
                is Result.Loading -> {}
            }
        }
    }

    fun verifyPothole(potholeId: String) {
        viewModelScope.launch {
            val result = potholeRepository.verifyPothole(potholeId)
            when (result) {
                is Result.Success -> {
                    loadAllPotholes()
                    _error.value = null
                }
                is Result.Error -> {
                    _error.value = result.exception.message
                }
                is Result.Loading -> {}
            }
        }
    }

    fun clearError() {
        _error.value = null
    }

    fun clearSelectedPothole() {
        _selectedPothole.value = null
    }
}