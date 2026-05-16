package com.pablocode.roadpotholeapp.domain.usecase

import com.pablocode.roadpotholeapp.domain.model.Notification
import com.pablocode.roadpotholeapp.domain.model.Result
import com.pablocode.roadpotholeapp.domain.repository.NotificationRepository
import javax.inject.Inject

class SendNearbyAlertUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository
) {
    suspend operator fun invoke(
        userId: String,
        latitude: Double,
        longitude: Double
    ): Result<List<Notification>> {
        if (latitude == 0.0 || longitude == 0.0) {
            return Result.Error(Exception("Invalid location coordinates"))
        }

        return notificationRepository.getNearbyPotholes(userId, latitude, longitude)
    }
}