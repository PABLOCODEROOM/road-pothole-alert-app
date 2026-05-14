package com.pablocode.roadpotholeapp.domain.usecase

import com.pablocode.roadpotholeapp.domain.model.Pothole
import com.pablocode.roadpotholeapp.domain.model.Result
import com.pablocode.roadpotholeapp.domain.model.UserLocation
import com.pablocode.roadpotholeapp.domain.repository.NotificationRepository
import javax.inject.Inject

class SendNearbyAlertUseCase @Inject constructor(
    private val notificationRepository: NotificationRepository
) {
    suspend operator fun invoke(pothole: Pothole, userLocation: UserLocation): Result<Unit> {
        return notificationRepository.sendNearbyAlert(pothole, userLocation)
    }
}