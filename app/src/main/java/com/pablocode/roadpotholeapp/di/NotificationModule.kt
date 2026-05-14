package com.pablocode.roadpotholeapp.di

import com.google.firebase.firestore.FirebaseFirestore
import com.pablocode.roadpotholeapp.data.repository.NotificationRepositoryImpl
import com.pablocode.roadpotholeapp.domain.repository.NotificationRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NotificationModule {

    @Singleton
    @Provides
    fun provideNotificationRepository(
        firestore: FirebaseFirestore
    ): NotificationRepository = NotificationRepositoryImpl(firestore)
}