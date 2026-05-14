package com.pablocode.roadpotholeapp.di

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.pablocode.roadpotholeapp.data.repository.PotholeRepositoryImpl
import com.pablocode.roadpotholeapp.domain.repository.PotholeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PotholeModule {

    @Singleton
    @Provides
    fun providePotholeRepository(
        firestore: FirebaseFirestore,
        storage: FirebaseStorage
    ): PotholeRepository = PotholeRepositoryImpl(firestore, storage)
}