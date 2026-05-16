package com.pablocode.roadpotholeapp.domain.model

data class Notification(
    val notificationId: String = "",
    val userId: String = "",
    val potholeId: String = "",
    val title: String = "",
    val message: String = "",
    val potholeLatitude: Double = 0.0,
    val potholeLongitude: Double = 0.0,
    val distance: Float = 0f,
    val severity: String = "MEDIUM",
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val actionTaken: String? = null
)