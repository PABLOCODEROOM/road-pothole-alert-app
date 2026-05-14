package com.pablocode.roadpotholeapp.di

import android.content.Context
import androidx.work.WorkManager
import androidx.work.WorkerFactory
import com.pablocode.roadpotholeapp.domain.repository.LocationRepository
import com.pablocode.roadpotholeapp.domain.repository.PotholeRepository
import com.pablocode.roadpotholeapp.domain.usecase.SendNearbyAlertUseCase
import com.pablocode.roadpotholeapp.services.PotholeWorkerFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WorkerModule {

    @Singleton
    @Provides
    fun provideWorkerFactory(
        locationRepository: LocationRepository,
        potholeRepository: PotholeRepository,
        sendNearbyAlertUseCase: SendNearbyAlertUseCase
    ): WorkerFactory = PotholeWorkerFactory(
        locationRepository,
        potholeRepository,
        sendNearbyAlertUseCase
    )

    @Singleton
    @Provides
    fun provideWorkManager(
        @ApplicationContext context: Context
    ): WorkManager = WorkManager.getInstance(context)
}