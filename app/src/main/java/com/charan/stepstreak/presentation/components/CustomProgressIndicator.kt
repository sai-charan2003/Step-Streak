package com.charan.stepstreak.presentation.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.gestures.AnchoredDraggableDefaults.PositionalThreshold
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.IndicatorBox
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CustomIndicator(
    state: PullToRefreshState,
    isRefreshing: Boolean,
    modifier: Modifier = Modifier,
    threshold: Dp = PullToRefreshDefaults.PositionalThreshold,
) {
    IndicatorBox(
        modifier = modifier,
        state = state,
        isRefreshing = isRefreshing,
        threshold = threshold,
    ) {

        Crossfade(
            targetState = isRefreshing,
        ) { refreshing ->
            ContainedLoadingIndicator()

        }
    }
}