package com.charan.stepstreak.presentation.onboarding

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.glance.AndroidResourceImageProvider
import androidx.glance.ButtonDefaults
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.StepsRecord
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.charan.stepstreak.R
import com.charan.stepstreak.data.model.DataProviders
import com.charan.stepstreak.presentation.navigation.HomeScreenNav
import com.charan.stepstreak.utils.DateUtils
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun OnBoardingScreen(
    navHostController: NavHostController,
    viewModel: OnBoardingViewModel = hiltViewModel()
) {
    val permissions = setOf(
        HealthPermission.getReadPermission(StepsRecord::class),
        HealthPermission.PERMISSION_READ_HEALTH_DATA_IN_BACKGROUND
    )
    val state = viewModel.state.collectAsState()
    val pageState = rememberPagerState(initialPage = 0, pageCount = { 3 })
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
                    navHostController.popBackStack()
                    navHostController.navigate(HomeScreenNav)
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
                    2 -> SelectProviderPage(state.value.dataProviders) {
                        viewModel.onEvent(OnBoardingEvents.OnSelectProvider(it))
                    }
                }
            }

            PagerIndicator(
                currentPage = pageState.currentPage,
                pageCount = 3
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if(pageState.currentPage == 1 && state.value.isPermissionGranted == false){
                        viewModel.onEvent(OnBoardingEvents.OnRequestPermission)
                    } else if(pageState.currentPage == 2 && state.value.dataProviders.any { it.isConnected == true }) {
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
                            "Next"
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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Image(
            painter = painterResource(R.drawable.applogo),
            null
        )
            Text(
                text = "Welcome to StepStreak!",
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center
            )



            Text(
                text = "Stay motivated by tracking your daily steps and achieving streaks!",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
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


@Composable
fun SelectProviderPage(
    providers: List<DataProviders>,
    onSelect: (DataProviders) -> Unit
) {
    if (providers.isNotEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState())
                .selectableGroup(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .weight(0.5f)
            ) {
                ProviderConnectImageitem(providers.map { it.icon })
                Text(
                    text = "Choose Your Step Tracking App",
                    style = MaterialTheme.typography.headlineLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )



                Text(
                    text = "We need a provider that tracks your steps and writes them to Health Connect.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }


            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                providers.forEach { provider ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .selectable(
                                selected = provider.isConnected,
                                onClick = { onSelect(provider) },
                                role = Role.RadioButton
                            )
                            .padding(16.dp)
                    ) {
                        Image(
                            painter = rememberDrawablePainter(provider.icon),
                            contentDescription = null,
                            modifier = Modifier.size(48.dp)
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        Text(
                            text = provider.name,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f)
                        )

                        RadioButton(
                            selected = provider.isConnected,
                            onClick = null
                        )
                    }
                }
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "No Providers Found",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Connect an app like Samsung Health or Google Fit that writes steps data to Health Connect.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}







