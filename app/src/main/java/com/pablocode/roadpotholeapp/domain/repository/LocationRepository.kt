package com.pablocode.roadpotholeapp.domain.repository

import com.pablocode.roadpotholeapp.domain.model.Result
import com.pablocode.roadpotholeapp.domain.model.UserLocation
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    fun getCurrentLocation(): Flow<UserLocation>
    fun startLocationUpdates(): Flow<UserLocation>
    suspend fun stopLocationUpdates(): Result<Unit>
    suspend fun requestLocationPermission(): Result<Boolean>
    fun isLocationPermissionGranted(): Boolean
}