package com.pablocode.roadpotholeapp.services

import android.content.Context
import androidx.work.WorkerFactory
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.pablocode.roadpotholeapp.domain.repository.LocationRepository
import com.pablocode.roadpotholeapp.domain.repository.PotholeRepository
import com.pablocode.roadpotholeapp.domain.usecase.SendNearbyAlertUseCase
import javax.inject.Inject

class PotholeWorkerFactory @Inject constructor(
    private val locationRepository: LocationRepository,
    private val potholeRepository: PotholeRepository,
    private val sendNearbyAlertUseCase: SendNearbyAlertUseCase
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): Worker? {
        return when (workerClassName) {
            NearbyPotholeWorker::class.java.name -> {
                NearbyPotholeWorker(
                    appContext,
                    workerParameters,
                    locationRepository,
                    potholeRepository,
                    sendNearbyAlertUseCase
                )
            }
            else -> null
        }
    }
}