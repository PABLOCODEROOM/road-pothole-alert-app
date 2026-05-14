package com.pablocode.roadpotholeapp.data.repository

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.pablocode.roadpotholeapp.domain.model.Result
import com.pablocode.roadpotholeapp.domain.model.UserLocation
import com.pablocode.roadpotholeapp.domain.repository.LocationRepository
import com.pablocode.roadpotholeapp.utils.Constants
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class LocationRepositoryImpl @Inject constructor(
    private val context: Context,
    private val fusedLocationProviderClient: FusedLocationProviderClient
) : LocationRepository {

    private var locationCallback: LocationCallback? = null

    override fun getCurrentLocation(): Flow<UserLocation> = callbackFlow {
        try {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                close(Exception("Location permission not granted"))
                return@callbackFlow
            }

            val location = fusedLocationProviderClient.lastLocation.await()
            if (location != null) {
                trySend(
                    UserLocation(
                        latitude = location.latitude,
                        longitude = location.longitude,
                        accuracy = location.accuracy,
                        altitude = location.altitude,
                        bearing = location.bearing,
                        speed = location.speed,
                        timestamp = location.time
                    )
                )
            }
        } catch (e: Exception) {
            close(e)
        }
        awaitClose()
    }

    override fun startLocationUpdates(): Flow<UserLocation> = callbackFlow {
        try {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                close(Exception("Location permission not granted"))
                return@callbackFlow
            }

            locationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)
                    for (location in locationResult.locations) {
                        trySend(
                            UserLocation(
                                latitude = location.latitude,
                                longitude = location.longitude,
                                accuracy = location.accuracy,
                                altitude = location.altitude,
                                bearing = location.bearing,
                                speed = location.speed,
                                timestamp = location.time
                            )
                        )
                    }
                }
            }

            val locationRequest = LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                Constants.LOCATION_UPDATE_INTERVAL
            )
                .setMinUpdateIntervalMillis(Constants.LOCATION_UPDATE_INTERVAL / 2)
                .build()

            fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback!!,
                Looper.getMainLooper()
            )
        } catch (e: Exception) {
            close(e)
        }

        awaitClose {
            locationCallback?.let {
                fusedLocationProviderClient.removeLocationUpdates(it)
            }
        }
    }

    override suspend fun stopLocationUpdates(): Result<Unit> {
        return try {
            locationCallback?.let {
                fusedLocationProviderClient.removeLocationUpdates(it)
            }
            locationCallback = null
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun requestLocationPermission(): Result<Boolean> {
        return Result.Success(isLocationPermissionGranted())
    }

    override fun isLocationPermissionGranted(): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
}