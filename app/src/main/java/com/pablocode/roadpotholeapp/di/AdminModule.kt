package com.pablocode.roadpotholeapp.di

import com.google.firebase.firestore.FirebaseFirestore
import com.pablocode.roadpotholeapp.data.repository.AdminRepositoryImpl
import com.pablocode.roadpotholeapp.domain.repository.AdminRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AdminModule {

    @Singleton
    @Provides
    fun provideAdminRepository(
        firestore: FirebaseFirestore
    ): AdminRepository = AdminRepositoryImpl(firestore)
}