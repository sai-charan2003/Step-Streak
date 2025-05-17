package com.charan.stepstreak.presentation.home

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumFlexibleTopAppBar
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import com.charan.stepstreak.presentation.components.CustomIndicator
import com.charan.stepstreak.presentation.home.components.DailyStepsCard
import com.charan.stepstreak.presentation.home.components.HomeTopBar
import com.charan.stepstreak.presentation.home.components.StreakCard
import com.charan.stepstreak.presentation.home.components.TodayProgressCard
import com.charan.stepstreak.presentation.navigation.SettingsScreenNav
import com.charan.stepstreak.utils.DateUtils
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable

fun HomeScreen(
    navHostController: NavHostController,
    viewModel: HomeScreenViewModel = hiltViewModel()
){
    val state = viewModel.state.collectAsState()
    val effects = viewModel.effects.collectAsState(initial = null)
    val scroll = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val pullToRefreshState = rememberPullToRefreshState()
    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsState()
    when(lifecycleState){
        Lifecycle.State.RESUMED -> {
            viewModel.onEvent(HomeEvent.OnRefresh)
        }
        else -> Unit
    }


    Scaffold(
        modifier = Modifier
            .nestedScroll(scroll.nestedScrollConnection),
        topBar = {
            MediumFlexibleTopAppBar(
                title = {
                    Text(
                        text = DateUtils.getGreetings(),

                    )
                },
                subtitle = {
                    Text(
                        text = state.value.motiText,
                    )
                },
                actions = {
                    IconButton (
                        onClick = {
                            navHostController.navigate(SettingsScreenNav)
                        },
                        shapes = IconButtonDefaults.shapes()
                    ) {
                        Icon(
                            Icons.Default.Settings,
                            null
                        )
                    }

                },
                scrollBehavior = scroll,
                modifier = Modifier


            )

        }
    ) { padding->
        PullToRefreshBox(
            isRefreshing = state.value.isSyncing,
            onRefresh = { viewModel.onEvent(HomeEvent.OnRefresh) },
            state = pullToRefreshState,
            modifier = Modifier.padding(padding),
            indicator = {
                PullToRefreshDefaults.LoadingIndicator(
                    state = pullToRefreshState,
                    isRefreshing = state.value.isSyncing,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                item {

                    Spacer(Modifier.height(20.dp))
                    StreakCard(
                        streakCount = state.value.streakCount,
                        motivationText = state.value.motiText
                    )
                    Spacer(Modifier.height(15.dp))
                    TodayProgressCard(
                        steps = state.value.todaysStepData.steps,
                        targetSteps = state.value.todaysStepData.targetSteps,
                    )
                    Spacer(Modifier.height(15.dp))
                    DailyStepsCard(
                        stepsData = state.value.stepsData
                    )
                }


            }
        }

    }

}
