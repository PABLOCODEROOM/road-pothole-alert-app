package com.pablocode.roadpotholeapp.services

import android.content.Context
import androidx.work.*
import com.pablocode.roadpotholeapp.domain.repository.LocationRepository
import com.pablocode.roadpotholeapp.domain.repository.PotholeRepository
import com.pablocode.roadpotholeapp.domain.usecase.SendNearbyAlertUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import java.util.concurrent.TimeUnit

class NearbyPotholeWorker(
    context: Context,
    params: WorkerParameters,
    private val locationRepository: LocationRepository,
    private val potholeRepository: PotholeRepository,
    private val sendNearbyAlertUseCase: SendNearbyAlertUseCase
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            // Get current location
            val userLocation = locationRepository.getCurrentLocation().first()

            // Get all potholes
            val potholes = potholeRepository.getAllPotholes().first()

            // Check for nearby potholes and send alerts
            potholes.forEach { pothole ->
                sendNearbyAlertUseCase(pothole, userLocation)
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        const val WORK_NAME = "nearby_pothole_check"

        fun schedulePeriodicCheck() {
            val checkPotholesWork = PeriodicWorkRequestBuilder<NearbyPotholeWorker>(
                15, TimeUnit.MINUTES
            ).build()

            WorkManager.getInstance().enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                checkPotholesWork
            )
        }

        fun stopPeriodicCheck() {
            WorkManager.getInstance().cancelUniqueWork(WORK_NAME)
        }
    }
}