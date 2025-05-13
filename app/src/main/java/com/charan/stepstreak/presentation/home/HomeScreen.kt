package com.charan.stepstreak.presentation.home

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
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
import com.charan.stepstreak.presentation.home.components.DailyStepsCard
import com.charan.stepstreak.presentation.home.components.HomeTopBar
import com.charan.stepstreak.presentation.home.components.StreakCard
import com.charan.stepstreak.presentation.home.components.TodayProgressCard
import com.charan.stepstreak.presentation.navigation.SettingsScreenNav
import com.charan.stepstreak.utils.DateUtils
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
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
            TopAppBar(
                title = {

                },
                actions = {
                    IconButton(
                        onClick = {
                            navHostController.navigate(SettingsScreenNav)
                        }
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
            modifier = Modifier.padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                item {
                    HomeTopBar(
                        greeting = DateUtils.getGreetings(),
                        motivationText = state.value.motiText
                    )
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
