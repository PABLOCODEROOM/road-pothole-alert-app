package com.pablocode.roadpotholeapp.domain.model

data class AdminStats(
    val totalReports: Int = 0,
    val pendingReports: Int = 0,
    val verifiedReports: Int = 0,
    val repairedReports: Int = 0,
    val dismissedReports: Int = 0,
    val totalUsers: Int = 0,
    val highSeverityCount: Int = 0,
    val mediumSeverityCount: Int = 0,
    val lowSeverityCount: Int = 0,
    val averageVerificationTime: Long = 0,
    val lastUpdated: Long = System.currentTimeMillis()
)