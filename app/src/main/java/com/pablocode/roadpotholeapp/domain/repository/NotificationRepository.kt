package com.pablocode.roadpotholeapp.domain.repository

import com.pablocode.roadpotholeapp.domain.model.Notification
import com.pablocode.roadpotholeapp.domain.model.Result
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    fun getUserNotifications(userId: String): Flow<List<Notification>>
    suspend fun saveNotification(notification: Notification): Result<String>
    suspend fun markAsRead(notificationId: String): Result<Unit>
    suspend fun deleteNotification(notificationId: String): Result<Unit>
    suspend fun saveFCMToken(userId: String, token: String): Result<Unit>
    suspend fun sendNearbyAlert(pothole: com.pablocode.roadpotholeapp.domain.model.Pothole, userLocation: com.pablocode.roadpotholeapp.domain.model.UserLocation): Result<Unit>
}