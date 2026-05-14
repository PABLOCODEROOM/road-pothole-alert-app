package com.pablocode.roadpotholeapp.domain.model

data class Pothole(
    val potholeId: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val imageUrl: String = "",
    val severity: PotholeSeverity = PotholeSeverity.MEDIUM,
    val description: String = "",
    val reportedBy: String = "",
    val reportedByName: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val status: PotholeStatus = PotholeStatus.PENDING,
    val verificationCount: Int = 0,
    val distance: Float? = null
)

enum class PotholeSeverity {
    LOW,
    MEDIUM,
    HIGH
}

enum class PotholeStatus {
    PENDING,
    VERIFIED,
    REPAIRED,
    DISMISSED
}