package com.pablocode.roadpotholeapp.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pablocode.roadpotholeapp.domain.model.PotholeStatus
import com.pablocode.roadpotholeapp.presentation.viewmodel.AdminViewModel
import com.pablocode.roadpotholeapp.presentation.ui.theme.severityHigh
import com.pablocode.roadpotholeapp.presentation.ui.theme.severityMedium
import com.pablocode.roadpotholeapp.presentation.ui.theme.severityLow

@Composable
fun AdminDashboardScreen(
    viewModel: AdminViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val adminStats by viewModel.adminStats.collectAsState()
    val allReports by viewModel.allReports.collectAsState()
    val filteredReports by viewModel.filteredReports.collectAsState()
    val selectedStatusFilter by viewModel.selectedStatusFilter.collectAsState()
    val selectedReport by viewModel.selectedReport.collectAsState()
    val error by viewModel.error.collectAsState()

    var showDeleteDialog by remember { mutableStateOf(false) }
    var reportToDelete by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Dashboard") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Statistics Section
                Text(
                    text = "Overview",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Main Stats Cards
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AdminStatCard(
                        title = "Total Reports",
                        value = adminStats.totalReports.toString(),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                    AdminStatCard(
                        title = "Pending",
                        value = adminStats.pendingReports.toString(),
                        color = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AdminStatCard(
                        title = "Verified",
                        value = adminStats.verifiedReports.toString(),
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.weight(1f)
                    )
                    AdminStatCard(
                        title = "Repaired",
                        value = adminStats.repairedReports.toString(),
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Severity Breakdown
                Text(
                    text = "Severity Breakdown",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AdminStatCard(
                        title = "High",
                        value = adminStats.highSeverityCount.toString(),
                        color = severityHigh,
                        modifier = Modifier.weight(1f)
                    )
                    AdminStatCard(
                        title = "Medium",
                        value = adminStats.mediumSeverityCount.toString(),
                        color = severityMedium,
                        modifier = Modifier.weight(1f)
                    )
                    AdminStatCard(
                        title = "Low",
                        value = adminStats.lowSeverityCount.toString(),
                        color = severityLow,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Filter Section
                Text(
                    text = "Reports Management",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedStatusFilter == null,
                        onClick = { viewModel.filterByStatus(null) },
                        label = { Text("All (${allReports.size})") },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = selectedStatusFilter == "PENDING",
                        onClick = { viewModel.filterByStatus("PENDING") },
                        label = { Text("Pending") },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = selectedStatusFilter == "VERIFIED",
                        onClick = { viewModel.filterByStatus("VERIFIED") },
                        label = { Text("Verified") },
                        modifier = Modifier.weight(1f)
                    )
                }

                // Reports List
                if (filteredReports.isEmpty()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "No reports found",
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(filteredReports) { report ->
                            AdminReportCard(
                                pothole = report,
                                onSelect = { viewModel.selectReport(report.potholeId) },
                                onDelete = {
                                    reportToDelete = report.potholeId
                                    showDeleteDialog = true
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Report Details Dialog
    if (selectedReport != null) {
        AdminReportDetailsDialog(
            pothole = selectedReport!!,
            onStatusChange = { newStatus ->
                viewModel.updateReportStatus(selectedReport!!.potholeId, newStatus)
            },
            onDismiss = { selectedReport = null }
        )
    }

    // Delete Confirmation Dialog
    if (showDeleteDialog && reportToDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Report") },
            text = { Text("Are you sure you want to delete this report? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteReport(reportToDelete!!)
                        showDeleteDialog = false
                        reportToDelete = null
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    // Error message
    if (error != null) {
        Snackbar(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(error!!)
        }
    }
}

@Composable
fun AdminStatCard(
    title: String,
    value: String,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                color = color,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun AdminReportCard(
    pothole: com.pablocode.roadpotholeapp.domain.model.Pothole,
    onSelect: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        onClick = onSelect
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = pothole.description.ifEmpty { "Pothole Report" },
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Chip(
                        onClick = {},
                        label = { Text(pothole.severity.name, style = MaterialTheme.typography.labelSmall) }
                    )
                    Chip(
                        onClick = {},
                        label = { Text(pothole.status.name, style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }
}

@Composable
fun AdminReportDetailsDialog(
    pothole: com.pablocode.roadpotholeapp.domain.model.Pothole,
    onStatusChange: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedStatus by remember { mutableStateOf(pothole.status.name) }
    var showStatusMenu by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Report Details") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(pothole.description, style = MaterialTheme.typography.bodyMedium)
                Text("Location: ${String.format("%.4f", pothole.latitude)}, ${String.format("%.4f", pothole.longitude)}", style = MaterialTheme.typography.labelSmall)
                Text("Severity: ${pothole.severity.name}", style = MaterialTheme.typography.labelSmall)
                Text("Verifications: ${pothole.verificationCount}", style = MaterialTheme.typography.labelSmall)
                
                Text("Status:", style = MaterialTheme.typography.labelMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { onStatusChange("PENDING") },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedStatus == "PENDING") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                        )
                    ) { Text("Pending") }
                    Button(
                        onClick = { onStatusChange("VERIFIED") },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedStatus == "VERIFIED") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                        )
                    ) { Text("Verified") }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { onStatusChange("REPAIRED") },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedStatus == "REPAIRED") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                        )
                    ) { Text("Repaired") }
                    Button(
                        onClick = { onStatusChange("DISMISSED") },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedStatus == "DISMISSED") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                        )
                    ) { Text("Dismissed") }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}
