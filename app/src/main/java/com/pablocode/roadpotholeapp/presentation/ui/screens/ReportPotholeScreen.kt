package com.pablocode.roadpotholeapp.presentation.ui.screens

import android.Manifest
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.pablocode.roadpotholeapp.domain.model.PotholeSeverity
import com.pablocode.roadpotholeapp.domain.model.UserLocation
import com.pablocode.roadpotholeapp.presentation.viewmodel.LocationViewModel
import com.pablocode.roadpotholeapp.presentation.viewmodel.ReportViewModel
import com.pablocode.roadpotholeapp.presentation.ui.theme.severityHigh
import com.pablocode.roadpotholeapp.presentation.ui.theme.severityLow
import com.pablocode.roadpotholeapp.presentation.ui.theme.severityMedium
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ReportPotholeScreen(
    locationViewModel: LocationViewModel = hiltViewModel(),
    reportViewModel: ReportViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onReportSuccess: () -> Unit
) {
    val context = LocalContext.current
    val userLocation by locationViewModel.userLocation.collectAsState()
    val selectedImageUri by reportViewModel.selectedImageUri.collectAsState()
    val selectedSeverity by reportViewModel.selectedSeverity.collectAsState()
    val isLoading by reportViewModel.isLoading.collectAsState()
    val error by reportViewModel.error.collectAsState()
    val successMessage by reportViewModel.successMessage.collectAsState()

    var description by remember { mutableStateOf("") }
    var photoUri by remember { mutableStateOf<Uri?>(null) }

    // Camera launcher
    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success && photoUri != null) {
                reportViewModel.setImageUri(photoUri.toString())
            }
        }
    )

    // Gallery launcher
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            if (uri != null) {
                reportViewModel.setImageUri(uri.toString())
            }
        }
    )

    // Camera permission launcher
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                val file = createImageFile(context)
                photoUri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )
                photoUri?.let { takePictureLauncher.launch(it) }
            }
        }
    )

    LaunchedEffect(successMessage) {
        if (successMessage != null) {
            onReportSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Report Pothole") },
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
                .padding(16.dp)
        ) {
            // Image Section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                if (selectedImageUri != null) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        AsyncImage(
                            model = selectedImageUri,
                            contentDescription = "Pothole image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        IconButton(
                            onClick = { reportViewModel.setImageUri("") },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Remove image",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.Default.CameraAlt,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            "Capture pothole image",
                            modifier = Modifier.padding(top = 8.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            // Image Upload Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Camera")
                }

                Button(
                    onClick = { pickImageLauncher.launch("image/*") },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Gallery")
                }
            }

            // Severity Selection
            Text(
                text = "Severity Level",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SeverityButton(
                    label = "Low",
                    severity = PotholeSeverity.LOW,
                    isSelected = selectedSeverity == PotholeSeverity.LOW,
                    color = severityLow,
                    onClick = { reportViewModel.setSeverity(PotholeSeverity.LOW) },
                    modifier = Modifier.weight(1f)
                )

                SeverityButton(
                    label = "Medium",
                    severity = PotholeSeverity.MEDIUM,
                    isSelected = selectedSeverity == PotholeSeverity.MEDIUM,
                    color = severityMedium,
                    onClick = { reportViewModel.setSeverity(PotholeSeverity.MEDIUM) },
                    modifier = Modifier.weight(1f)
                )

                SeverityButton(
                    label = "High",
                    severity = PotholeSeverity.HIGH,
                    isSelected = selectedSeverity == PotholeSeverity.HIGH,
                    color = severityHigh,
                    onClick = { reportViewModel.setSeverity(PotholeSeverity.HIGH) },
                    modifier = Modifier.weight(1f)
                )
            }

            // Location Info
            Text(
                text = "Location",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    if (userLocation != null) {
                        Text(
                            text = "Latitude: ${String.format("%.4f", userLocation!!.latitude)}",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "Longitude: ${String.format("%.4f", userLocation!!.longitude)}",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Text(
                            text = "Accuracy: ${String.format("%.1f", userLocation!!.accuracy)} m",
                            style = MaterialTheme.typography.bodySmall
                        )
                    } else {
                        Text(
                            text = "Getting location...",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            // Description
            Text(
                text = "Description",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Describe the pothole") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(bottom = 16.dp),
                maxLines = 5
            )

            // Error Message
            if (error != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = error!!,
                        modifier = Modifier.padding(12.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            // Submit Button
            Button(
                onClick = {
                    if (userLocation != null) {
                        reportViewModel.reportPothole(
                            description = description,
                            location = userLocation!!,
                            userId = "user_id",
                            userName = "User Name"
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                enabled = !isLoading && selectedImageUri != null && description.isNotEmpty()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Submit Report")
                }
            }
        }
    }
}

@Composable
fun SeverityButton(
    label: String,
    severity: PotholeSeverity,
    isSelected: Boolean,
    color: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) color else MaterialTheme.colorScheme.surfaceVariant
        ),
        border = if (isSelected) {
            androidx.compose.foundation.border.BorderStroke(
                2.dp,
                color
            )
        } else null
    ) {
        Text(
            label,
            color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private fun createImageFile(context: Context): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
    val imageFileName = "JPEG_${timeStamp}_"
    val storageDir = File(context.cacheDir, "images")
    if (!storageDir.exists()) {
        storageDir.mkdirs()
    }
    return File.createTempFile(imageFileName, ".jpg", storageDir)
}