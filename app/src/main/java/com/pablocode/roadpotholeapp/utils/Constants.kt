package com.pablocode.roadpotholeapp.utils

object Constants {
    const val FIRESTORE_USERS_COLLECTION = "users"
    const val FIRESTORE_POTHOLES_COLLECTION = "potholes"
    const val FIRESTORE_NOTIFICATIONS_COLLECTION = "notifications"
    
    const val DEFAULT_ALERT_RADIUS = 500 // meters
    const val LOCATION_UPDATE_INTERVAL = 5000L // milliseconds
    
    const val INTENT_REPORT_ID = "report_id"
    const val INTENT_LATITUDE = "latitude"
    const val INTENT_LONGITUDE = "longitude"
    
    // Image upload settings
    const val MAX_IMAGE_SIZE = 5 * 1024 * 1024 // 5MB
    const val IMAGE_QUALITY = 80
    
    // Alert settings
    const val ALERT_CHECK_INTERVAL_MINUTES = 15L
    const val ALERT_THRESHOLD_DISTANCE = 1000f // meters
}