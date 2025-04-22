package com.charan.stepstreak

import android.annotation.SuppressLint
import android.content.Context
import android.health.connect.datatypes.StepsRecord
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import com.charan.stepstreak.ui.theme.StepStreakTheme
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StepStreakTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        context = this
                    )
                }
            }
        }
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun Greeting(context: Context) {
    try {
        val healthConnectClient = HealthConnectClient.getOrCreate(context)
        CoroutineScope(Dispatchers.Main).launch {
            val response = healthConnectClient.aggregate(
                AggregateRequest(
                    metrics = setOf(androidx.health.connect.client.records.StepsRecord.COUNT_TOTAL),
                    timeRangeFilter = TimeRangeFilter.between(LocalDateTime.now().minusDays(1), LocalDateTime.now())
                )
            )
            // The result may be null if no data is available in the time range
            val stepCount = response[androidx.health.connect.client.records.StepsRecord.COUNT_TOTAL]
        }
    } catch (e: Exception) {
        // Run error handling here
    }
}
