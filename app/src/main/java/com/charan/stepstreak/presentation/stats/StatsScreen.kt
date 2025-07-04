package com.charan.stepstreak.presentation.stats

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.charan.stepstreak.data.model.StatType
import com.charan.stepstreak.presentation.common.components.StatEvents
import com.charan.stepstreak.presentation.common.components.StatGraph
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun StatsScreen(
    viewModel: StatsViewModel = hiltViewModel()
) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    Scaffold (
        topBar = {
            MediumFlexibleTopAppBar(
                title = {
                    Text(
                        text = "Stats",
                    )
                },
            )
        }
    ){ padding->
        LazyColumn(
            modifier = Modifier.padding(padding)
        ) {
            item {
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
                        ) {
                            Text(item.name)

                        }

                    }
                }
                Spacer(Modifier.padding(bottom = 20.dp))
                Row(modifier = Modifier.fillMaxWidth().padding(top = 10.dp),horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            viewModel.onEvent(StatEvents.DecrementPeriod)

                        },
                        shapes = IconButtonDefaults.shapes()
                    ) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowLeft,null)
                    }
                    Spacer(Modifier.weight(1f))
                    if(state.value.selectedStatType == StatType.WEEKLY) {
                        Text(state.value.formatedWeek)
                    } else {
                        Text(state.value.currentMonth)
                    }
                    Spacer(Modifier.weight(1f))
                    IconButton(
                        onClick = {
                            viewModel.onEvent(StatEvents.IncrementPeriod)

                        },
                        shapes = IconButtonDefaults.shapes()
                    ) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowRight,null)
                    }

                }

                Box(
                    modifier = Modifier.padding(20.dp)
                ) {
                    StatGraph(
                        graphData = state.value.graphData,
                        isSidePane = false,
                        statType = state.value.selectedStatType
                    )
                }


            }

        }

    }
}