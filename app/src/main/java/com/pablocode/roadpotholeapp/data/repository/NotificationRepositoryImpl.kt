package com.pablocode.roadpotholeapp.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.pablocode.roadpotholeapp.domain.model.Notification
import com.pablocode.roadpotholeapp.domain.model.Pothole
import com.pablocode.roadpotholeapp.domain.model.Result
import com.pablocode.roadpotholeapp.domain.model.UserLocation
import com.pablocode.roadpotholeapp.domain.repository.NotificationRepository
import com.pablocode.roadpotholeapp.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : NotificationRepository {

    override fun getUserNotifications(userId: String): Flow<List<Notification>> = flow {
        try {
            val snapshot = firestore.collection(Constants.FIRESTORE_NOTIFICATIONS_COLLECTION)
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .limit(50)
                .get()
                .await()

            val notifications = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Notification::class.java)?.copy(notificationId = doc.id)
            }

            emit(notifications)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    override suspend fun saveNotification(notification: Notification): Result<String> {
        return try {
            val docRef = firestore.collection(Constants.FIRESTORE_NOTIFICATIONS_COLLECTION)
                .add(notification)
                .await()

            Result.Success(docRef.id)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun markAsRead(notificationId: String): Result<Unit> {
        return try {
            firestore.collection(Constants.FIRESTORE_NOTIFICATIONS_COLLECTION)
                .document(notificationId)
                .update("isRead", true)
                .await()

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun deleteNotification(notificationId: String): Result<Unit> {
        return try {
            firestore.collection(Constants.FIRESTORE_NOTIFICATIONS_COLLECTION)
                .document(notificationId)
                .delete()
                .await()

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun saveFCMToken(userId: String, token: String): Result<Unit> {
        return try {
            firestore.collection("users")
                .document(userId)
                .update("fcmToken", token)
                .await()

            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun sendNearbyAlert(
        pothole: Pothole,
        userLocation: UserLocation
    ): Result<Unit> {
        return try {
            val distance = calculateDistance(
                userLocation.latitude,
                userLocation.longitude,
                pothole.latitude,
                pothole.longitude
            )

            if (distance <= Constants.DEFAULT_ALERT_RADIUS) {
                val notification = Notification(
                    title = "⚠️ Pothole Alert",
                    message = "${pothole.severity} severity pothole detected ${distance.toInt()}m away",
                    potholeId = pothole.potholeId,
                    potholeLatitude = pothole.latitude,
                    potholeLongitude = pothole.longitude,
                    distance = distance,
                    severity = pothole.severity.name,
                    timestamp = System.currentTimeMillis()
                )

                // Save to Firestore
                firestore.collection(Constants.FIRESTORE_NOTIFICATIONS_COLLECTION)
                    .add(notification)
                    .await()

                Result.Success(Unit)
            } else {
                Result.Success(Unit)
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val result = FloatArray(1)
        android.location.Location.distanceBetween(lat1, lon1, lat2, lon2, result)
        return result[0]
    }
}