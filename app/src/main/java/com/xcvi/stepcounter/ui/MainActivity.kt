package com.xcvi.stepcounter.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startForegroundService
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
                        onStep = { viewModel.addSteps() },
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
    onStep: () -> Unit,
    onPermissionsGranted: () -> Unit,
    onGoToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val permissions = when (Build.VERSION.SDK_INT) {
        Build.VERSION_CODES.TIRAMISU -> {
            listOf(
                Manifest.permission.POST_NOTIFICATIONS,
                Manifest.permission.ACTIVITY_RECOGNITION
            )
        }

        Build.VERSION_CODES.Q -> {
            listOf(

                Manifest.permission.ACTIVITY_RECOGNITION
            )
        }

        else -> {
            emptyList()
        }
    }
    val permissionsState = rememberMultiplePermissionsState(
        permissions = permissions
    )

    if (permissionsState.allPermissionsGranted || permissions.isEmpty()) {
        onPermissionsGranted()
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier.fillMaxSize()
        ) {
            Text(
                text = "Steps: $steps",
                fontSize = MaterialTheme.typography.headlineMedium.fontSize,
                fontWeight = MaterialTheme.typography.headlineMedium.fontWeight
            )
            Button(
                onClick = { onStep() },
            ) {
                Text(text = "Step")
            }
        }

    } else if (permissionsState.shouldShowRationale) {
        Column {
            Text("Permissions permanently denied.")
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
        Text("No Camera Permission")
    }
}
