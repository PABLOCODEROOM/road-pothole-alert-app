package com.pablocode.roadpotholeapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pablocode.roadpotholeapp.domain.model.Pothole
import com.pablocode.roadpotholeapp.domain.model.PotholeSeverity
import com.pablocode.roadpotholeapp.domain.model.Result
import com.pablocode.roadpotholeapp.domain.model.UserLocation
import com.pablocode.roadpotholeapp.domain.usecase.ReportPotholeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReportViewModel @Inject constructor(
    private val reportPotholeUseCase: ReportPotholeUseCase
) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage

    private val _selectedImageUri = MutableStateFlow<String?>(null)
    val selectedImageUri: StateFlow<String?> = _selectedImageUri

    private val _selectedSeverity = MutableStateFlow(PotholeSeverity.MEDIUM)
    val selectedSeverity: StateFlow<PotholeSeverity> = _selectedSeverity

    fun setImageUri(uri: String) {
        _selectedImageUri.value = uri
        _error.value = null
    }

    fun setSeverity(severity: PotholeSeverity) {
        _selectedSeverity.value = severity
    }

    fun reportPothole(
        description: String,
        location: UserLocation,
        userId: String,
        userName: String
    ) {
        if (_selectedImageUri.value == null) {
            _error.value = "Please select an image"
            return
        }

        if (description.isBlank()) {
            _error.value = "Please enter a description"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            val pothole = Pothole(
                latitude = location.latitude,
                longitude = location.longitude,
                severity = _selectedSeverity.value,
                description = description,
                reportedBy = userId,
                reportedByName = userName,
                timestamp = System.currentTimeMillis()
            )

            val result = reportPotholeUseCase(pothole, _selectedImageUri.value!!)

            _isLoading.value = false
            when (result) {
                is Result.Success -> {
                    _successMessage.value = "Pothole reported successfully!"
                    _selectedImageUri.value = null
                    _selectedSeverity.value = PotholeSeverity.MEDIUM
                }
                is Result.Error -> {
                    _error.value = result.exception.message ?: "Failed to report pothole"
                }
                is Result.Loading -> {}
            }
        }
    }

    fun clearMessages() {
        _error.value = null
        _successMessage.value = null
    }
}
