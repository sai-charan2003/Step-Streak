package com.charan.stepstreak.presentation.common.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.charan.stepstreak.data.model.StatType
import com.charan.stepstreak.presentation.common.PeriodStepsData
import com.charan.stepstreak.presentation.common.StepsData
import com.charan.stepstreak.presentation.home.GraphData
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisLabelComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.marker.rememberDefaultCartesianMarker
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.ProvideVicoTheme
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.compose.common.vicoTheme
import com.patrykandpatrick.vico.compose.m3.common.rememberM3VicoTheme
import com.patrykandpatrick.vico.core.cartesian.Zoom
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.data.CartesianValueFormatter
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer.ColumnProvider
import com.patrykandpatrick.vico.core.cartesian.marker.CartesianMarker
import com.patrykandpatrick.vico.core.common.Defaults
import com.patrykandpatrick.vico.core.common.data.ExtraStore
import com.patrykandpatrick.vico.core.common.shape.CorneredShape
import com.patrykandpatrick.vico.core.common.shape.Shape

@Composable
fun StatGraph(
    graphData: List<GraphData> = emptyList(),
    isSidePane: Boolean,
    targetStep: Long = 5000L,
    statType: StatType = StatType.WEEKLY
) {
    val modelProducer = remember { CartesianChartModelProducer() }
    val labelListKey = remember { ExtraStore.Key<List<String>>() }
    val labels = graphData.map{ it.xAxis }
    val values = graphData.map{ it.yAxis }

    LaunchedEffect(graphData) {
        if(graphData.isNotEmpty()) {
            modelProducer.runTransaction {
                columnSeries { series(values) }
                extras { it[labelListKey] = labels }
            }
        }
    }

    ProvideVicoTheme(rememberM3VicoTheme()) {
        CartesianChartHost(
            chart = rememberCartesianChart(
                rememberColumnCartesianLayer(
                    columnProvider =ColumnProvider.series(
                        vicoTheme.columnCartesianLayerColors.map { color ->
                            rememberLineComponent(
                                fill(color),
                                shape = CorneredShape.Pill,
                                thickness = 8.dp

                            )
                        }
                    ),
                ),
                startAxis = VerticalAxis.rememberStart(
                    guideline = null,
                    line = null,
                    tick = null,
                    itemPlacer = VerticalAxis.ItemPlacer.count(
                        count = {3}
                    )
                ),
                bottomAxis = HorizontalAxis.rememberBottom(
                    valueFormatter = CartesianValueFormatter { context, x, _ ->
                        val index = x.toInt()
                        context.model.extraStore[labelListKey]
                            ?.getOrNull(index)
                            ?: ""
                    },
                    guideline = null,
                    line = null,
                    tick = null,
                    itemPlacer = if(statType == StatType.MONTHLY) HorizontalAxis.ItemPlacer.aligned(spacing = {4}) else HorizontalAxis.ItemPlacer.aligned()
                ),
            ),
            modelProducer = modelProducer,
            zoomState = rememberVicoZoomState(zoomEnabled = false, maxZoom = Zoom.Content),
            scrollState = rememberVicoScrollState(scrollEnabled = false)
        )

    }


}

@Composable
@Preview
fun StatGraphPreview() {
    val sampleSteps = listOf(
        GraphData(xAxis = "Mon", yAxis = 5000f),
        GraphData(xAxis = "Tue", yAxis = 6000f),
        GraphData(xAxis = "Wed", yAxis = 7000f),
        GraphData(xAxis = "Thu", yAxis = 8000f),
        GraphData(xAxis = "Fri", yAxis = 9000f),

    )

    StatGraph(
        graphData = sampleSteps,
        isSidePane = false,
        targetStep = 5000L,
    )
}
