package com.pablocode.roadpotholeapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pablocode.roadpotholeapp.domain.model.Notification
import com.pablocode.roadpotholeapp.domain.model.Result
import com.pablocode.roadpotholeapp.domain.repository.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications: StateFlow<List<Notification>> = _notifications

    private val _unreadCount = MutableStateFlow(0)
    val unreadCount: StateFlow<Int> = _unreadCount

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadNotifications(userId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            notificationRepository.getUserNotifications(userId)
                .catch { e ->
                    _error.value = e.message
                    _isLoading.value = false
                }
                .collect { notifications ->
                    _notifications.value = notifications
                    _unreadCount.value = notifications.count { !it.isRead }
                    _isLoading.value = false
                    _error.value = null
                }
        }
    }

    fun markAsRead(notificationId: String) {
        viewModelScope.launch {
            val result = notificationRepository.markAsRead(notificationId)
            when (result) {
                is Result.Success -> {
                    _notifications.value = _notifications.value.map { notif ->
                        if (notif.notificationId == notificationId) {
                            notif.copy(isRead = true)
                        } else notif
                    }
                    _unreadCount.value = maxOf(0, _unreadCount.value - 1)
                }
                is Result.Error -> {
                    _error.value = result.exception.message
                }
                is Result.Loading -> {}
            }
        }
    }

    fun deleteNotification(notificationId: String) {
        viewModelScope.launch {
            val result = notificationRepository.deleteNotification(notificationId)
            when (result) {
                is Result.Success -> {
                    val notif = _notifications.value.find { it.notificationId == notificationId }
                    _notifications.value = _notifications.value.filter { it.notificationId != notificationId }
                    if (notif != null && !notif.isRead) {
                        _unreadCount.value = maxOf(0, _unreadCount.value - 1)
                    }
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