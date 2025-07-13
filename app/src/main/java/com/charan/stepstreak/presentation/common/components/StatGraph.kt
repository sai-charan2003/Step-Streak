package com.charan.stepstreak.presentation.common.components

import android.graphics.Typeface
import android.icu.text.DecimalFormat
import android.util.Log
import androidx.compose.material3.MaterialTheme
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
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisGuidelineComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisLabelComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisTickComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.marker.rememberDefaultCartesianMarker
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.ProvideVicoTheme
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.component.rememberShapeComponent
import com.patrykandpatrick.vico.compose.common.component.rememberTextComponent
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.compose.common.shape.markerCorneredShape
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
import com.patrykandpatrick.vico.core.cartesian.marker.ColumnCartesianLayerMarkerTarget
import com.patrykandpatrick.vico.core.cartesian.marker.DefaultCartesianMarker
import com.patrykandpatrick.vico.core.common.Defaults
import com.patrykandpatrick.vico.core.common.Insets
import com.patrykandpatrick.vico.core.common.data.ExtraStore
import com.patrykandpatrick.vico.core.common.shape.CorneredShape
import com.patrykandpatrick.vico.core.common.shape.MarkerCorneredShape
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
    val labels = graphData.map{ if(statType == StatType.WEEKLY) it.day else it.date }
    val values = graphData.map{ it.steps }

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
                marker = rememberDefaultCartesianMarker(
                    label = rememberTextComponent(
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        padding = Insets(horizontalDp = 12f, verticalDp = 8f),
                        margins = Insets(verticalDp = 10f),
                        background = rememberShapeComponent(
                            shape = CorneredShape.rounded(100f),
                            fill = fill(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f)),
                        ),
                        typeface = Typeface.DEFAULT_BOLD,
                    ),
                    labelPosition = DefaultCartesianMarker.LabelPosition.AbovePoint,
                    guideline = rememberAxisTickComponent(),
                    valueFormatter = DefaultCartesianMarker.ValueFormatter { context, targets ->
                        val target = targets.firstOrNull() as? ColumnCartesianLayerMarkerTarget
                        val column = target?.columns?.firstOrNull()
                        val x = target?.x?.toInt() ?: 0
                        val y = column?.entry?.y ?: 0.0

                        val label = context.model.extraStore[labelListKey]?.getOrNull(x) ?: "Unknown"
                        val stepsFormatted = DecimalFormat("#,###").format(y.toInt())
                        val labelString = graphData.find { it.day == label || it.date == label }

                        "$stepsFormatted steps on ${labelString?.date} ${labelString?.day}"
                    }

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

