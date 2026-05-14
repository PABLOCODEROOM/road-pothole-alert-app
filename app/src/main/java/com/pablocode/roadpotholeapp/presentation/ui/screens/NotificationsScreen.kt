package com.pablocode.roadpotholeapp.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pablocode.roadpotholeapp.domain.model.Notification
import com.pablocode.roadpotholeapp.presentation.viewmodel.NotificationViewModel
import com.pablocode.roadpotholeapp.presentation.ui.theme.severityHigh
import com.pablocode.roadpotholeapp.presentation.ui.theme.severityMedium
import com.pablocode.roadpotholeapp.presentation.ui.theme.severityLow
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NotificationsScreen(
    viewModel: NotificationViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onNotificationClick: (String) -> Unit
) {
    val notifications by viewModel.notifications.collectAsState()
    val unreadCount by viewModel.unreadCount.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadUserNotifications("current_user_id") // Replace with actual user ID
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notifications") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (unreadCount > 0) {
                        Badge(
                            modifier = Modifier.padding(end = 12.dp),
                            containerColor = MaterialTheme.colorScheme.error
                        ) {
                            Text("$unreadCount")
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (notifications.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Default.NotificationsNone,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "No notifications yet",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(top = 16.dp)
                )
                Text(
                    text = "You will receive alerts when potholes are reported nearby",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 8.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                items(notifications) { notification ->
                    NotificationItem(
                        notification = notification,
                        onNotificationClick = {
                            viewModel.markAsRead(notification.notificationId)
                            onNotificationClick(notification.potholeId)
                        },
                        onDismiss = {
                            viewModel.deleteNotification(notification.notificationId)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun NotificationItem(
    notification: Notification,
    onNotificationClick: () -> Unit,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (notification.isRead) {
                MaterialTheme.colorScheme.surface
            } else {
                MaterialTheme.colorScheme.primaryContainer
            }
        ),
        onClick = onNotificationClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Severity Badge
            Surface(
                modifier = Modifier
                    .size(12.dp)
                    .padding(end = 12.dp),
                shape = androidx.compose.foundation.shape.CircleShape,
                color = when (notification.severity) {
                    "HIGH" -> severityHigh
                    "MEDIUM" -> severityMedium
                    "LOW" -> severityLow
                    else -> MaterialTheme.colorScheme.secondary
                }
            ) {}

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
            ) {
                Text(
                    text = notification.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
                Text(
                    text = notification.message,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Text(
                    text = formatTime(notification.timestamp),
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.padding(top = 4.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            IconButton(
                onClick = onDismiss,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Dismiss",
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

private fun formatTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("HH:mm, dd MMM", Locale.US)
    return sdf.format(Date(timestamp))
}