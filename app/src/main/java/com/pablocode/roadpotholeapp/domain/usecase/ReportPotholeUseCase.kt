package com.pablocode.roadpotholeapp.domain.usecase

import com.pablocode.roadpotholeapp.domain.model.Pothole
import com.pablocode.roadpotholeapp.domain.model.Result
import com.pablocode.roadpotholeapp.domain.repository.PotholeRepository
import javax.inject.Inject

class ReportPotholeUseCase @Inject constructor(
    private val potholeRepository: PotholeRepository
) {
    suspend operator fun invoke(
        pothole: Pothole,
        imageUri: String
    ): Result<String> {
        // Validation
        if (imageUri.isBlank()) {
            return Result.Error(Exception("Image is required"))
        }

        if (pothole.latitude == 0.0 || pothole.longitude == 0.0) {
            return Result.Error(Exception("Location is required"))
        }

        if (pothole.description.isBlank()) {
            return Result.Error(Exception("Description is required"))
        }

        return potholeRepository.reportPothole(pothole, imageUri)
    }
}