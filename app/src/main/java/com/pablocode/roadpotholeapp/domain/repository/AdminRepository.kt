package com.pablocode.roadpotholeapp.domain.repository

import com.pablocode.roadpotholeapp.domain.model.AdminStats
import com.pablocode.roadpotholeapp.domain.model.Pothole
import com.pablocode.roadpotholeapp.domain.model.Result
import kotlinx.coroutines.flow.Flow

interface AdminRepository {
    fun getAdminStats(): Flow<AdminStats>
    fun getAllReports(): Flow<List<Pothole>>
    fun getReportsByStatus(status: String): Flow<List<Pothole>>
    suspend fun updateReportStatus(potholeId: String, status: String): Result<Unit>
    suspend fun deleteReport(potholeId: String): Result<Unit>
    suspend fun getReportDetail(potholeId: String): Result<Pothole>
}