package com.pablocode.roadpotholeapp.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.pablocode.roadpotholeapp.MainActivity
import com.pablocode.roadpotholeapp.R

class PotholeMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val title = remoteMessage.notification?.title ?: "Pothole Alert"
        val message = remoteMessage.notification?.body ?: "New pothole reported nearby"
        val potholeId = remoteMessage.data["potholeId"] ?: ""
        val distance = remoteMessage.data["distance"] ?: "Unknown"
        val severity = remoteMessage.data["severity"] ?: "MEDIUM"

        sendNotification(title, message, potholeId, distance, severity)
    }

    private fun sendNotification(
        title: String,
        messageBody: String,
        potholeId: String,
        distance: String,
        severity: String
    ) {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            putExtra("potholeId", potholeId)
            putExtra("navigateToMap", true)
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val channelId = "pothole_alerts"
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("$messageBody\nDistance: $distance | Severity: $severity")
            )
            .setVibrate(longArrayOf(0, 250, 250, 250))
            .setSound(android.provider.Settings.System.DEFAULT_NOTIFICATION_URI)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Pothole Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alerts for nearby potholes"
                enableVibration(true)
                setShowBadge(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Save token to Firestore
        saveFCMTokenToServer(token)
    }

    private fun saveFCMTokenToServer(token: String) {
        // Implementation to save FCM token to Firestore
        // This will be called when a new token is generated
    }
}