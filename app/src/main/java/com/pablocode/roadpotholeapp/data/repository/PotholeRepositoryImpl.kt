package com.pablocode.roadpotholeapp.data.repository

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.pablocode.roadpotholeapp.domain.model.Pothole
import com.pablocode.roadpotholeapp.domain.model.Result
import com.pablocode.roadpotholeapp.domain.repository.PotholeRepository
import com.pablocode.roadpotholeapp.utils.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class PotholeRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) : PotholeRepository {

    override fun getAllPotholes(): Flow<List<Pothole>> = flow {
        try {
            val snapshot = firestore.collection(Constants.FIRESTORE_POTHOLES_COLLECTION)
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

    override fun getNearbyPotholes(
        latitude: Double,
        longitude: Double,
        radiusInMeters: Float
    ): Flow<List<Pothole>> = flow {
        try {
            val snapshot = firestore.collection(Constants.FIRESTORE_POTHOLES_COLLECTION)
                .get()
                .await()

            val nearbyPotholes = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Pothole::class.java)?.copy(potholeId = doc.id)
            }.filter { pothole ->
                val distance = calculateDistance(
                    latitude, longitude,
                    pothole.latitude, pothole.longitude
                )
                distance <= radiusInMeters
            }.map { pothole ->
                val distance = calculateDistance(
                    latitude, longitude,
                    pothole.latitude, pothole.longitude
                )
                pothole.copy(distance = distance)
            }.sortedBy { it.distance }

            emit(nearbyPotholes)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    override suspend fun getPotholeById(potholeId: String): Result<Pothole> {
        return try {
            val snapshot = firestore.collection(Constants.FIRESTORE_POTHOLES_COLLECTION)
                .document(potholeId)
                .get()
                .await()

            val pothole = snapshot.toObject(Pothole::class.java)?.copy(potholeId = snapshot.id)
            if (pothole != null) {
                Result.Success(pothole)
            } else {
                Result.Error(Exception("Pothole not found"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun reportPothole(pothole: Pothole, imageUri: String): Result<String> {
        return try {
            // Upload image to Firebase Storage
            val imagePath = "potholes/${System.currentTimeMillis()}.jpg"
            val imageRef = storage.reference.child(imagePath)
            imageRef.putFile(Uri.parse(imageUri)).await()
            val downloadUrl = imageRef.downloadUrl.await().toString()

            // Save pothole data to Firestore
            val potholeData = pothole.copy(imageUrl = downloadUrl)
            val docRef = firestore.collection(Constants.FIRESTORE_POTHOLES_COLLECTION)
                .add(potholeData)
                .await()
            
            Result.Success(docRef.id)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun updatePotholeStatus(potholeId: String, status: String): Result<Unit> {
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

    override suspend fun verifyPothole(potholeId: String): Result<Unit> {
        return try {
            firestore.collection(Constants.FIRESTORE_POTHOLES_COLLECTION)
                .document(potholeId)
                .update(
                    mapOf(
                        "verificationCount" to com.google.firebase.firestore.FieldValue.increment(1)
                    )
                )
                .await()
            
            Result.Success(Unit)
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