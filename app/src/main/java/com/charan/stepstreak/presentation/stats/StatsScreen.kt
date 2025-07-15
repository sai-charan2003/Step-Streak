package com.charan.stepstreak.presentation.stats

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowLeft
import androidx.compose.material.icons.automirrored.rounded.ArrowRight
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonGroup
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumFlexibleTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.AnimatedPaneScope
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffold
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowSizeClass
import com.charan.stepstreak.data.model.StatType
import com.charan.stepstreak.presentation.common.components.StatEvents
import com.charan.stepstreak.presentation.common.components.StatGraph
import com.charan.stepstreak.presentation.stats.components.StepsCard
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class,
    ExperimentalMaterial3AdaptiveApi::class
)
@Composable
fun StatsScreen(
    viewModel: StatsViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    val steps = state.value.stepData.stepsData.filter { it.steps.toInt() != 0 }
    val scroll = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val scaffoldNavigator = rememberSupportingPaneScaffoldNavigator()
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val isMinimumSize = windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)

    Scaffold(
        topBar = {
            MediumFlexibleTopAppBar(
                title = { Text("Stats") },
                scrollBehavior = scroll
            )
        },
        modifier = Modifier.nestedScroll(scroll.nestedScrollConnection)
    ) { padding ->
        SupportingPaneScaffold(
            modifier = Modifier.padding(padding),
            directive = scaffoldNavigator.scaffoldDirective,
            value = scaffoldNavigator.scaffoldValue,
            mainPane = {
                AnimatedPane {
                    LazyColumn(
                        modifier = Modifier
                            .padding(horizontal = 16.dp),
                    ) {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Row(
                                    Modifier
                                        .padding(horizontal = 8.dp)
                                        .fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    StatType.entries.forEachIndexed { index, item ->
                                        ToggleButton(
                                            checked = item == state.value.selectedStatType,
                                            onCheckedChange = {
                                                viewModel.onEvent(StatEvents.OnStatTypeSelected(item))
                                            },
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Text(item.name)

                                        }

                                    }
                                }
                            }
                        }

                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = {
                                        viewModel.onEvent(StatEvents.DecrementPeriod)
                                    },
                                    shapes = IconButtonDefaults.shapes()
                                ) {
                                    Icon(
                                        Icons.AutoMirrored.Rounded.ArrowLeft,
                                        contentDescription = "Previous"
                                    )
                                }

                                Text(
                                    text = if (state.value.selectedStatType == StatType.WEEKLY)
                                        state.value.formatedWeek
                                    else state.value.currentMonth,
                                    style = MaterialTheme.typography.titleMedium
                                )

                                IconButton(
                                    onClick = {
                                        viewModel.onEvent(StatEvents.IncrementPeriod)
                                    },
                                    shapes = IconButtonDefaults.shapes()
                                ) {
                                    Icon(
                                        Icons.AutoMirrored.Rounded.ArrowRight,
                                        contentDescription = "Next"
                                    )
                                }
                            }
                        }
                        item {
                            if(!isMinimumSize){
                                StatGraph(
                                    graphData = state.value.graphData,
                                    isSidePane = false,
                                    statType = state.value.selectedStatType
                                )
                            }
                        }

                        if (steps.isNotEmpty()) {
                            item {
                                Text(
                                    text = "Daily Summary",
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.padding(vertical = 12.dp)
                                )
                            }

                            items(steps.size) { index ->
                                val stepData = steps[index]
                                StepsCard(
                                    totalSteps = stepData.steps,
                                    targetSteps = stepData.targetSteps,
                                    formatDate = stepData.formattedDate,
                                    isFirst = index == 0,
                                    isLast = index == steps.size - 1,
                                    isTargetReached = stepData.targetCompleted,
                                    progress = stepData.currentProgress,
                                    modifier = Modifier
                                )
                            }
                        } else {
                            item {
                                Text(
                                    text = "No Data",
                                    style = MaterialTheme.typography.headlineMediumEmphasized.copy(
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    modifier = Modifier.padding(vertical = 20.dp)
                                )
                            }
                        }
                    }
                }
            },
            supportingPane = {
                if(isMinimumSize) {
                    AnimatedPane(
                        modifier = Modifier.preferredWidth(500.dp)
                    ) {
                        StatGraph(
                            graphData = state.value.graphData,
                            isSidePane = true,
                            statType = state.value.selectedStatType
                        )

                    }
                }

            }

        )

    }
}
