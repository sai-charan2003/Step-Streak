package com.charan.stepstreak


import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import com.charan.stepstreak.data.repository.HealthConnectRepo
import com.charan.stepstreak.ui.theme.StepStreakTheme
import com.google.accompanist.permissions.rememberPermissionState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject
import kotlin.collections.containsAll

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var healthConnectRepo: HealthConnectRepo
    val permissions = setOf(
        HealthPermission.getReadPermission(StepsRecord::class)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StepStreakTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val permissionLaunch = rememberLauncherForActivityResult(healthConnectRepo.requestPermission()){

                    }
                    LaunchedEffect(true) {
                        permissionLaunch.launch(permissions)
                        Log.d("TAG", "onCreate: ${healthConnectRepo.hasPermission()}")
                        healthConnectRepo.getTotalSteps().collect {  }
                    }





                }
            }
        }
    }
}


