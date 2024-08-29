package com.xcvi.stepcounter.ui

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.xcvi.stepcounter.service.SensorService
import com.xcvi.stepcounter.ui.theme.StepCounterTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (SensorService.hasPermissions(this)) {
            startStepCounterService()
        }

        setContent {
            StepCounterTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel: StepCounterViewModel = hiltViewModel()
                    val steps = viewModel.steps
                    MainScreen(
                        steps = steps,
                        updateSteps = { viewModel.editSteps(it) },
                        onGoToSettings = { openAppSettings() },
                        onPermissionsGranted = { startStepCounterService() }
                    )
                }
            }
        }
    }

    private fun openAppSettings() {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", this.packageName, null)
        )
        this.startActivity(intent)
    }

    private fun startStepCounterService() {
        Intent(applicationContext, SensorService::class.java).also {
            it.action = SensorService.Actions.START.name
            startForegroundService(it)
        }
    }
}


@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MainScreen(
    steps: Int,
    updateSteps: (String) -> Unit,
    onPermissionsGranted: () -> Unit,
    onGoToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        listOf(
            Manifest.permission.POST_NOTIFICATIONS,
            Manifest.permission.ACTIVITY_RECOGNITION
        )
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        listOf(Manifest.permission.ACTIVITY_RECOGNITION)
    } else {
        emptyList()
    }

    val permissionsState = rememberMultiplePermissionsState(
        permissions = permissions
    )

    var showUpdate by remember {
        mutableStateOf(false)
    }
    var value by remember {
        mutableStateOf("")
    }

    if (permissionsState.allPermissionsGranted || permissions.isEmpty()) {
        onPermissionsGranted()
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier.fillMaxSize()
        ) {
            Text(
                text = "Accelerometer Steps: $steps",
                fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                fontWeight = MaterialTheme.typography.headlineMedium.fontWeight
            )
            Spacer(modifier = modifier.size(8.dp))
            Button(onClick = { showUpdate = true }) {
                Text(text = "Update Steps")
            }
            if (showUpdate) {
                AlertDialog(
                    onDismissRequest = { showUpdate = false },
                    confirmButton = {
                        Button(
                            onClick = {
                                if (value.isNotBlank()) {
                                    updateSteps(value)
                                }
                                showUpdate = false
                            }
                        ) {
                            Text(text = "Update Steps")
                        }
                    },
                    title = { Text(text = "Update Steps") },
                    text = {
                        TextField(value = value, onValueChange = { value = it })
                    },
                    dismissButton = {
                        TextButton(onClick = { showUpdate = false }) {
                            Text(text = "Cancel")
                        }
                    }
                )
            }
        }

    } else if (permissionsState.shouldShowRationale) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier.fillMaxSize()
        ) {
            Text("Permissions are denied. Please enable permission to use Step Counter.")
            Button(
                onClick = {
                    onGoToSettings()
                }
            ) {
                Text(text = "Go to Settings")
            }
        }
    } else {
        SideEffect {
            permissionsState.run { launchMultiplePermissionRequest() }
        }
    }
}
