package com.charan.stepstreak.presentation.home

import android.util.Log
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
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
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumFlexibleTopAppBar
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDragHandle
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.PaneExpansionState
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffold
import androidx.compose.material3.adaptive.layout.rememberPaneExpansionState
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavHostController
import androidx.window.core.layout.WindowSizeClass
import com.charan.stepstreak.presentation.home.components.DailyStepsCard
import com.charan.stepstreak.presentation.home.components.SimpleBarChartWithAxes
import com.charan.stepstreak.presentation.home.components.StreakCard
import com.charan.stepstreak.presentation.home.components.TodayProgressCard
import com.charan.stepstreak.presentation.navigation.SettingsScreenNav
import com.charan.stepstreak.utils.DateUtils
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalMaterial3AdaptiveApi::class
)
@Composable

fun HomeScreen(
    onSettingNavigate : () -> Unit,
    viewModel: HomeScreenViewModel = hiltViewModel()
){
    val lifecycleOwner = LocalLifecycleOwner.current
    val state = viewModel.state.collectAsStateWithLifecycle()
    val effect by viewModel.effects.collectAsStateWithLifecycle(
        initialValue = null,
        lifecycle = lifecycleOwner.lifecycle
    )
    val scroll = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val pullToRefreshState = rememberPullToRefreshState()
    val scaffoldNavigator = rememberSupportingPaneScaffoldNavigator()
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val isMinimumSize = windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)
    LaunchedEffect(Unit) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            viewModel.onEvent(HomeEvent.OnRefresh)
        }
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
                        text = state.value.motivationText
                    )
                },
                actions = {
                    IconButton (
                        onClick = {
                            onSettingNavigate.invoke()
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
            SupportingPaneScaffold(
                directive = scaffoldNavigator.scaffoldDirective,
                value = scaffoldNavigator.scaffoldValue,
                mainPane = {
                    AnimatedPane {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            item {
                                Spacer(Modifier.height(20.dp))
                                StreakCard(
                                    streakCount = state.value.streakCount,
                                    motivationText = state.value.motivationText
                                )
                                Spacer(Modifier.height(15.dp))
                                TodayProgressCard(
                                    steps = state.value.todayStepsData.steps,
                                    targetSteps = state.value.todayStepsData.targetSteps,
                                )
                                if(!isMinimumSize) {
                                    Spacer(Modifier.height(15.dp))
                                    SimpleBarChartWithAxes(
                                        weeklySteps = state.value.currentWeekData,
                                        targetStep = state.value.currentTargetSteps

                                    )
                                }
                                Spacer(Modifier.height(15.dp))
                                DailyStepsCard(
                                    stepsData = state.value.allStepsData
                                )
                            }


                        }
                    }

                },
                supportingPane = {
                    if(isMinimumSize) {
                        AnimatedPane(
                            modifier = Modifier.preferredWidth(500.dp)
                        ) {
                            SimpleBarChartWithAxes(
                                weeklySteps = state.value.currentWeekData,
                                targetStep = state.value.currentTargetSteps,
                                isSidePane = true
                            )
                        }
                    }

                },

//                paneExpansionDragHandle = { state ->
//                    val interactionSource =
//                        remember { MutableInteractionSource() }
//                    VerticalDragHandle(
//                        modifier =
//                            Modifier.paneExpansionDraggable(
//                                state,
//                                LocalMinimumInteractiveComponentSize.current,
//                                interactionSource
//                            ), interactionSource = interactionSource
//                    )
//                }



            )

        }

    }

}
