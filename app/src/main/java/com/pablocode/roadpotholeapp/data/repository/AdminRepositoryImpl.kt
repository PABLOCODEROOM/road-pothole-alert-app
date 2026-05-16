package com.pablocode.roadpotholeapp.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.pablocode.roadpotholeapp.domain.model.AdminStats
import com.pablocode.roadpotholeapp.domain.model.Pothole
import com.pablocode.roadpotholeapp.domain.model.PotholeStatus
import com.pablocode.roadpotholeapp.domain.model.PotholeSeverity
import com.pablocode.roadpotholeapp.domain.model.Result
import com.pablocode.roadpotholeapp.domain.repository.AdminRepository
import com.pablocode.roadpotholeapp.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AdminRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : AdminRepository {

    override fun getAdminStats(): Flow<AdminStats> = flow {
        try {
            val snapshot = firestore.collection(Constants.FIRESTORE_POTHOLES_COLLECTION)
                .get()
                .await()

            val potholes = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Pothole::class.java)
            }

            val stats = AdminStats(
                totalReports = potholes.size,
                pendingReports = potholes.count { it.status == PotholeStatus.PENDING },
                verifiedReports = potholes.count { it.status == PotholeStatus.VERIFIED },
                repairedReports = potholes.count { it.status == PotholeStatus.REPAIRED },
                dismissedReports = potholes.count { it.status == PotholeStatus.DISMISSED },
                highSeverityCount = potholes.count { it.severity == PotholeSeverity.HIGH },
                mediumSeverityCount = potholes.count { it.severity == PotholeSeverity.MEDIUM },
                lowSeverityCount = potholes.count { it.severity == PotholeSeverity.LOW },
                lastUpdated = System.currentTimeMillis()
            )

            emit(stats)
        } catch (e: Exception) {
            emit(AdminStats())
        }
    }

    override fun getAllReports(): Flow<List<Pothole>> = flow {
        try {
            val snapshot = firestore.collection(Constants.FIRESTORE_POTHOLES_COLLECTION)
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()

            val potholes = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Pothole::class.java)?.copy(potholeId = doc.id)
            }

            emit(potholes)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    override fun getReportsByStatus(status: String): Flow<List<Pothole>> = flow {
        try {
            val snapshot = firestore.collection(Constants.FIRESTORE_POTHOLES_COLLECTION)
                .whereEqualTo("status", status)
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()

            val potholes = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Pothole::class.java)?.copy(potholeId = doc.id)
            }

            emit(potholes)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    override suspend fun updateReportStatus(potholeId: String, status: String): Result<Unit> {
        return try {
            firestore.collection(Constants.FIRESTORE_POTHOLES_COLLECTION)
                .document(potholeId)
                .update("status", status)
                .await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun deleteReport(potholeId: String): Result<Unit> {
        return try {
            firestore.collection(Constants.FIRESTORE_POTHOLES_COLLECTION)
                .document(potholeId)
                .delete()
                .await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getReportDetail(potholeId: String): Result<Pothole> {
        return try {
            val snapshot = firestore.collection(Constants.FIRESTORE_POTHOLES_COLLECTION)
                .document(potholeId)
                .get()
                .await()

            val pothole = snapshot.toObject(Pothole::class.java)?.copy(potholeId = snapshot.id)
            if (pothole != null) {
                Result.Success(pothole)
            } else {
                Result.Error(Exception("Report not found"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}