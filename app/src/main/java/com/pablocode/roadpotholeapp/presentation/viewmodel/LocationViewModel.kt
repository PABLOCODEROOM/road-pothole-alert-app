package com.pablocode.roadpotholeapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pablocode.roadpotholeapp.domain.model.UserLocation
import com.pablocode.roadpotholeapp.domain.repository.LocationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val _userLocation = MutableStateFlow<UserLocation?>(null)
    val userLocation: StateFlow<UserLocation?> = _userLocation

    private val _isLocationPermissionGranted = MutableStateFlow(false)
    val isLocationPermissionGranted: StateFlow<Boolean> = _isLocationPermissionGranted

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        checkLocationPermission()
        getCurrentLocation()
    }

    private fun checkLocationPermission() {
        _isLocationPermissionGranted.value = locationRepository.isLocationPermissionGranted()
    }

    private fun getCurrentLocation() {
        viewModelScope.launch {
            locationRepository.getCurrentLocation()
                .catch { e ->
                    _error.value = e.message
                }
                .collect { location ->
                    _userLocation.value = location
                    _error.value = null
                }
        }
    }

    fun startLocationUpdates() {
        viewModelScope.launch {
            locationRepository.startLocationUpdates()
                .catch { e ->
                    _error.value = e.message
                }
                .collect { location ->
                    _userLocation.value = location
                    _error.value = null
                }
        }
    }

    fun stopLocationUpdates() {
        viewModelScope.launch {
            locationRepository.stopLocationUpdates()
        }
    }

    fun clearError() {
        _error.value = null
    }
}