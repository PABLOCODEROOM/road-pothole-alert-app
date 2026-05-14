package com.pablocode.roadpotholeapp.domain.repository

import com.pablocode.roadpotholeapp.domain.model.Pothole
import com.pablocode.roadpotholeapp.domain.model.Result
import kotlinx.coroutines.flow.Flow

interface PotholeRepository {
    fun getAllPotholes(): Flow<List<Pothole>>
    fun getNearbyPotholes(latitude: Double, longitude: Double, radiusInMeters: Float): Flow<List<Pothole>>
    suspend fun getPotholeById(potholeId: String): Result<Pothole>
    suspend fun reportPothole(pothole: Pothole, imageUri: String): Result<String>
    suspend fun updatePotholeStatus(potholeId: String, status: String): Result<Unit>
    suspend fun verifyPothole(potholeId: String): Result<Unit>
}