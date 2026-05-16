package com.pablocode.roadpotholeapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pablocode.roadpotholeapp.domain.model.AdminStats
import com.pablocode.roadpotholeapp.domain.model.Pothole
import com.pablocode.roadpotholeapp.domain.model.Result
import com.pablocode.roadpotholeapp.domain.repository.AdminRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val adminRepository: AdminRepository
) : ViewModel() {

    private val _adminStats = MutableStateFlow(AdminStats())
    val adminStats: StateFlow<AdminStats> = _adminStats

    private val _allReports = MutableStateFlow<List<Pothole>>(emptyList())
    val allReports: StateFlow<List<Pothole>> = _allReports

    private val _filteredReports = MutableStateFlow<List<Pothole>>(emptyList())
    val filteredReports: StateFlow<List<Pothole>> = _filteredReports

    private val _selectedReport = MutableStateFlow<Pothole?>(null)
    val selectedReport: StateFlow<Pothole?> = _selectedReport

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _selectedStatusFilter = MutableStateFlow<String?>(null)
    val selectedStatusFilter: StateFlow<String?> = _selectedStatusFilter

    init {
        loadStats()
        loadAllReports()
    }

    private fun loadStats() {
        viewModelScope.launch {
            _isLoading.value = true
            adminRepository.getAdminStats()
                .catch { e ->
                    _error.value = e.message
                    _isLoading.value = false
                }
                .collect { stats ->
                    _adminStats.value = stats
                    _isLoading.value = false
                    _error.value = null
                }
        }
    }

    private fun loadAllReports() {
        viewModelScope.launch {
            adminRepository.getAllReports()
                .catch { e ->
                    _error.value = e.message
                }
                .collect { reports ->
                    _allReports.value = reports
                    applyFilter()
                    _error.value = null
                }
        }
    }

    fun filterByStatus(status: String?) {
        _selectedStatusFilter.value = status
        applyFilter()
    }

    private fun applyFilter() {
        _filteredReports.value = if (_selectedStatusFilter.value != null) {
            _allReports.value.filter { it.status.name == _selectedStatusFilter.value }
        } else {
            _allReports.value
        }
    }

    fun selectReport(potholeId: String) {
        viewModelScope.launch {
            val result = adminRepository.getReportDetail(potholeId)
            when (result) {
                is Result.Success -> {
                    _selectedReport.value = result.data
                    _error.value = null
                }
                is Result.Error -> {
                    _error.value = result.exception.message
                }
                is Result.Loading -> {}
            }
        }
    }

    fun updateReportStatus(potholeId: String, status: String) {
        viewModelScope.launch {
            val result = adminRepository.updateReportStatus(potholeId, status)
            when (result) {
                is Result.Success -> {
                    loadStats()
                    loadAllReports()
                    _error.value = null
                }
                is Result.Error -> {
                    _error.value = result.exception.message
                }
                is Result.Loading -> {}
            }
        }
    }

    fun deleteReport(potholeId: String) {
        viewModelScope.launch {
            val result = adminRepository.deleteReport(potholeId)
            when (result) {
                is Result.Success -> {
                    loadStats()
                    loadAllReports()
                    _selectedReport.value = null
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
}