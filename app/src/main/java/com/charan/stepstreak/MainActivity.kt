package com.charan.stepstreak


import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContract
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.expressiveLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.navigation.compose.rememberNavController
import com.charan.stepstreak.data.model.ThemeEnum
import com.charan.stepstreak.data.repository.DataStoreRepo
import com.charan.stepstreak.data.repository.HealthConnectRepo
import com.charan.stepstreak.data.worker.StepsUpdateWorker
import com.charan.stepstreak.presentation.navigation.NavAppHost
import com.charan.stepstreak.ui.theme.StepStreakTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject
import kotlin.collections.containsAll

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var dataStoreRepo: DataStoreRepo
    val keepScreen = mutableStateOf(true)

    @OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        StepsUpdateWorker.setup(this)
        installSplashScreen().setKeepOnScreenCondition {
            keepScreen.value
        }
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )
        setContent {
            val systemTheme = if (isSystemInDarkTheme()) ThemeEnum.DARK else ThemeEnum.LIGHT
            val isDynamicColor by dataStoreRepo.isDynamicColor.collectAsState(initial = true)
            val themeData by dataStoreRepo.theme.collectAsState(initial = systemTheme)
            val isOnBoardingCompleted by dataStoreRepo.isOnBoardingCompleted.collectAsState(initial = true)
            val notificationPermission = rememberPermissionState(android.Manifest.permission.POST_NOTIFICATIONS)
            LaunchedEffect(Unit) {
                keepScreen.value = false
            }
            LaunchedEffect(notificationPermission) {
                notificationPermission.launchPermissionRequest()
            }

            StepStreakTheme(
                dynamicColor = isDynamicColor,
                darkTheme = when (themeData) {
                    ThemeEnum.SYSTEM -> isSystemInDarkTheme()
                    ThemeEnum.LIGHT -> false
                    ThemeEnum.DARK -> true
                }
            ) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    NavAppHost(isOnBoardingCompleted = isOnBoardingCompleted)
                }
            }
        }
    }
}



