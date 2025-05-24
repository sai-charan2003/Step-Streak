package com.charan.stepstreak.presentation.onboarding


import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.glance.AndroidResourceImageProvider
import androidx.glance.ButtonDefaults
import androidx.glance.LocalContext
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.StepsRecord
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.charan.stepstreak.R
import com.charan.stepstreak.data.model.DataProviders
import com.charan.stepstreak.presentation.navigation.HomeScreenNav
import com.charan.stepstreak.presentation.onboarding.components.HealthConnectAnimationItem
import com.charan.stepstreak.presentation.onboarding.components.ProviderConnectImageitem
import com.charan.stepstreak.utils.DateUtils
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun OnBoardingScreen(
    onHomeScreenNavigate : () -> Unit,
    viewModel: OnBoardingViewModel = hiltViewModel()
) {
    val permissions = setOf(
        HealthPermission.getReadPermission(StepsRecord::class),
        HealthPermission.PERMISSION_READ_HEALTH_DATA_IN_BACKGROUND,
        HealthPermission.PERMISSION_READ_HEALTH_DATA_HISTORY
    )
    val state = viewModel.state.collectAsState()
    val pageState = rememberPagerState(initialPage = 0, pageCount = { 2 })
    val permissionLaunch = rememberLauncherForActivityResult(PermissionController.createRequestPermissionResultContract()) { result->
        if(result == permissions){
            viewModel.onEvent(OnBoardingEvents.OnPermissionGranted)
        }

    }


    LaunchedEffect(Unit) {
        viewModel.effects.collectLatest {
            when (it) {
                OnBoardingViewEffect.ScrollPage -> {
                    pageState.animateScrollToPage(pageState.currentPage + 1)
                }
                OnBoardingViewEffect.RequestPermission -> {
                    permissionLaunch.launch(permissions)
                }

                OnBoardingViewEffect.OnBoardingComplete -> {
                    onHomeScreenNavigate.invoke()
                }
            }

        }

    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            HorizontalPager(
                state = pageState,
                modifier = Modifier.weight(1f),
                userScrollEnabled = true
            ) { page ->
                when (page) {
                    0 -> IntroPage()
                    1 -> HealthConnectPermissionScreen(state.value.isPermissionGranted)
//                    2 -> SelectProviderPage(state.value.dataProviders) {
//                        viewModel.onEvent(OnBoardingEvents.OnSelectProvider(it))
//                    }
                }
            }

            PagerIndicator(
                currentPage = pageState.currentPage,
                pageCount = 2
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if(pageState.currentPage == 1 && state.value.isPermissionGranted == false){
                        viewModel.onEvent(OnBoardingEvents.OnRequestPermission)
                    } else if(pageState.currentPage == 1 && state.value.isPermissionGranted == true) {
                        viewModel.onEvent(OnBoardingEvents.OnBoardingComplete)
                    } else {
                        viewModel.onEvent(OnBoardingEvents.OnChangePage)
                    }
                },
                shapes = androidx.compose.material3.ButtonDefaults.shapes(),

                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    if (pageState.currentPage == 0)
                        "Get Started"
                    else if(pageState.currentPage == 1) {
                        if(!state.value.isPermissionGranted){
                            "Grant"
                        } else {
                            "Finish"
                        }
                    }
                    else "Finish"
                )
            }
        }
    }
}

@Composable
fun PagerIndicator(currentPage: Int, pageCount: Int) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        repeat(pageCount) { index ->
            val color = if (index == currentPage) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)

            Box(
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .size(10.dp)
                    .background(color, shape = CircleShape)
            )
        }
    }
}

@Composable
fun IntroPage() {
    val visible = remember { mutableStateOf(false) }

    // Trigger animations on composition
    LaunchedEffect(Unit) {
        visible.value = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        AnimatedVisibility(
            visible = visible.value,
            enter = fadeIn(animationSpec = tween(800)) + slideInVertically(
                initialOffsetY = { -100 },
                animationSpec = tween(800)
            )
        ) {
            Image(
                painter = painterResource(R.drawable.applogo),
                contentDescription = null
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        AnimatedVisibility(
            visible = visible.value,
            enter = fadeIn(animationSpec = tween(800, delayMillis = 300)) +
                    slideInVertically(initialOffsetY = { 100 }, animationSpec = tween(800, delayMillis = 300))
        ) {
            Text(
                text = "Welcome to StepStreak!",
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center
            )
        }

        AnimatedVisibility(
            visible = visible.value,
            enter = fadeIn(animationSpec = tween(800, delayMillis = 600)) +
                    slideInVertically(initialOffsetY = { 100 }, animationSpec = tween(800, delayMillis = 600))
        ) {
            Text(
                text = "Stay motivated by tracking your daily steps and achieving streaks!",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
        }
    }
}



@Composable
fun HealthConnectPermissionScreen(
    isConnected: Boolean = false
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            ,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        HealthConnectAnimationItem(
            modifier = Modifier
                .size(150.dp)
                .padding(vertical = 16.dp),
            isConnected = isConnected
        )
            Text(
                text = "Connect with Health Connect",
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center
            )



            Text(
                text = "Allow access to your steps data so we can track your streaks!",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )

    }
}






