package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.DonutLarge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.domain.model.CategoryBreakdownItem
import com.example.domain.model.CurrencyFormatter
import com.example.domain.model.MonthlyTrendPoint
import com.example.ui.theme.CoralContainer
import com.example.ui.theme.CoralDeep
import com.example.ui.theme.CoralExpense
import com.example.ui.theme.EmeraldBright
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.PureWhiteSurface
import com.example.ui.theme.SlateMutedInk
import com.example.ui.theme.SlateOutline
import com.example.ui.theme.SoftSlateSurfaceVariant
import java.util.Locale
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.hypot
import kotlin.math.max

@Composable
fun MonthlyTrendCurveCard(
    trends: List<MonthlyTrendPoint>,
    selectedYearMonth: String,
    modifier: Modifier = Modifier
) {
    val defaultIndex = remember(trends, selectedYearMonth) {
        trends.indexOfFirst { it.yearMonth == selectedYearMonth }.takeIf { it >= 0 }
            ?: trends.lastIndex.coerceAtLeast(0)
    }
    var inspectedIndex by remember(trends, selectedYearMonth) { mutableIntStateOf(defaultIndex) }
    val activePoint = trends.getOrNull(inspectedIndex)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("trend_chart_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = PureWhiteSurface),
        border = BorderStroke(1.dp, SlateOutline),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Rounded.BarChart,
                    contentDescription = null,
                    tint = EmeraldPrimary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Kurva Tren Berkala (6 Bulan)",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Perbandingan Pemasukan vs Pengeluaran tiap bulan",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Legend
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LegendDot(color = EmeraldBright, label = "Pemasukan")
                LegendDot(color = CoralExpense, label = "Pengeluaran")
            }

            Spacer(modifier = Modifier.height(12.dp))

            val maxVal = remember(trends) {
                val peak = trends.maxOfOrNull { max(it.totalIncome, it.totalExpense) } ?: 0L
                if (peak <= 0L) 1_000_000L else ((peak * 1.15).toLong()).coerceAtLeast(100_000L)
            }

            // Smooth Bezier Line/Area Chart
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(190.dp)
            ) {
                Canvas(
                    modifier = Modifier
                        .matchParentSize()
                        .pointerInput(trends) {
                            detectTapGestures { tapOffset ->
                                if (trends.isNotEmpty()) {
                                    val leftPad = 16f
                                    val rightPad = 16f
                                    val chartW = (size.width - leftPad - rightPad).coerceAtLeast(1f)
                                    val step = if (trends.size > 1) chartW / (trends.size - 1) else chartW
                                    val nearest = ((tapOffset.x - leftPad) / step).toInt()
                                        .coerceIn(0, trends.lastIndex)
                                    inspectedIndex = nearest
                                }
                            }
                        }
                ) {
                    val leftPad = 14.dp.toPx()
                    val rightPad = 14.dp.toPx()
                    val topPad = 12.dp.toPx()
                    val bottomPad = 14.dp.toPx()
                    val chartW = (size.width - leftPad - rightPad).coerceAtLeast(1f)
                    val chartH = (size.height - topPad - bottomPad).coerceAtLeast(1f)
                    val baselineY = topPad + chartH

                    // Dashed horizontal grid lines
                    val dashEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
                    for (i in 0..3) {
                        val y = topPad + (chartH * i / 3f)
                        drawLine(
                            color = SlateOutline,
                            start = Offset(leftPad, y),
                            end = Offset(leftPad + chartW, y),
                            strokeWidth = 1.dp.toPx(),
                            pathEffect = if (i < 3) dashEffect else null
                        )
                    }

                    if (trends.size >= 2) {
                        val stepX = chartW / (trends.size - 1)

                        fun pointFor(index: Int, value: Long): Offset {
                            val ratio = (value.toFloat() / maxVal.toFloat()).coerceIn(0f, 1f)
                            val x = leftPad + index * stepX
                            val y = baselineY - (ratio * chartH)
                            return Offset(x, y)
                        }

                        val incomePoints = trends.mapIndexed { idx, pt -> pointFor(idx, pt.totalIncome) }
                        val expensePoints = trends.mapIndexed { idx, pt -> pointFor(idx, pt.totalExpense) }

                        fun buildSmoothPath(points: List<Offset>): Path {
                            val p = Path()
                            p.moveTo(points.first().x, points.first().y)
                            for (i in 0 until points.lastIndex) {
                                val curr = points[i]
                                val next = points[i + 1]
                                val cx = (curr.x + next.x) / 2f
                                p.cubicTo(cx, curr.y, cx, next.y, next.x, next.y)
                            }
                            return p
                        }

                        val incomeLine = buildSmoothPath(incomePoints)
                        val incomeArea = Path().apply {
                            addPath(incomeLine)
                            lineTo(incomePoints.last().x, baselineY)
                            lineTo(incomePoints.first().x, baselineY)
                            close()
                        }
                        drawPath(
                            path = incomeArea,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    EmeraldBright.copy(alpha = 0.28f),
                                    EmeraldBright.copy(alpha = 0.02f)
                                ),
                                startY = topPad,
                                endY = baselineY
                            )
                        )

                        val expenseLine = buildSmoothPath(expensePoints)
                        val expenseArea = Path().apply {
                            addPath(expenseLine)
                            lineTo(expensePoints.last().x, baselineY)
                            lineTo(expensePoints.first().x, baselineY)
                            close()
                        }
                        drawPath(
                            path = expenseArea,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    CoralExpense.copy(alpha = 0.24f),
                                    CoralExpense.copy(alpha = 0.02f)
                                ),
                                startY = topPad,
                                endY = baselineY
                            )
                        )

                        // Vertical marker line on inspected month
                        val selX = incomePoints[inspectedIndex.coerceIn(0, incomePoints.lastIndex)].x
                        drawLine(
                            color = SlateMutedInk.copy(alpha = 0.35f),
                            start = Offset(selX, topPad),
                            end = Offset(selX, baselineY),
                            strokeWidth = 1.5.dp.toPx()
                        )

                        drawPath(
                            path = incomeLine,
                            color = EmeraldBright,
                            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                        )
                        drawPath(
                            path = expenseLine,
                            color = CoralExpense,
                            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                        )

                        incomePoints.forEachIndexed { idx, pt ->
                            val radius = if (idx == inspectedIndex) 6.dp.toPx() else 3.5.dp.toPx()
                            drawCircle(color = PureWhiteSurface, radius = radius + 2.dp.toPx(), center = pt)
                            drawCircle(color = EmeraldPrimary, radius = radius, center = pt)
                        }
                        expensePoints.forEachIndexed { idx, pt ->
                            val radius = if (idx == inspectedIndex) 6.dp.toPx() else 3.5.dp.toPx()
                            drawCircle(color = PureWhiteSurface, radius = radius + 2.dp.toPx(), center = pt)
                            drawCircle(color = CoralDeep, radius = radius, center = pt)
                        }
                    }
                }
            }

            // Month pills row (tappable to inspect curve)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                trends.forEachIndexed { idx, pt ->
                    val isSelected = idx == inspectedIndex
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) EmeraldContainer else Color.Transparent,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { inspectedIndex = idx }
                    ) {
                        Text(
                            text = pt.shortLabel,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) EmeraldPrimary else SlateMutedInk,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            if (activePoint != null) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SoftSlateSurfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = CurrencyFormatter.formatYearMonthFull(activePoint.yearMonth),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(
                                text = "+${CurrencyFormatter.formatCompactRupiah(activePoint.totalIncome)}",
                                style = MaterialTheme.typography.labelLarge,
                                color = EmeraldPrimary
                            )
                            Text(
                                text = "-${CurrencyFormatter.formatCompactRupiah(activePoint.totalExpense)}",
                                style = MaterialTheme.typography.labelLarge,
                                color = CoralDeep
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ExpenseDoughnutChartCard(
    breakdown: List<CategoryBreakdownItem>,
    totalExpense: Long,
    modifier: Modifier = Modifier
) {
    var selectedCategoryName by remember(breakdown) { mutableStateOf<String?>(null) }
    val selectedItem = breakdown.firstOrNull { it.category.name == selectedCategoryName }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("doughnut_chart_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = PureWhiteSurface),
        border = BorderStroke(1.dp, SlateOutline),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Rounded.DonutLarge,
                    contentDescription = null,
                    tint = CoralDeep,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Rincian Pengeluaran per Kategori",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Ketuk segmen atau kategori untuk melihat porsi detail",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (breakdown.isEmpty() || totalExpense <= 0L) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = SoftSlateSurfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.DonutLarge,
                            contentDescription = null,
                            tint = SlateMutedInk,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Belum ada pengeluaran di bulan ini",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Catat transaksi pengeluaran atau muat Demo 6 Bulan untuk melihat grafik kategori.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(210.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(
                        modifier = Modifier
                            .size(196.dp)
                            .pointerInput(breakdown) {
                                detectTapGestures { offset ->
                                    val center = Offset(size.width / 2f, size.height / 2f)
                                    val dx = offset.x - center.x
                                    val dy = offset.y - center.y
                                    val dist = hypot(dx, dy)
                                    val outerR = size.width / 2f
                                    if (dist in (outerR * 0.42f)..outerR) {
                                        var angle = (atan2(dy, dx) * 180.0 / PI).toFloat() + 90f
                                        if (angle < 0f) angle += 360f
                                        var running = 0f
                                        val tapped = breakdown.firstOrNull { item ->
                                            val sweep = (item.percentageOfExpense / 100f) * 360f
                                            val inside = angle in running..(running + sweep)
                                            running += sweep
                                            inside
                                        }
                                        selectedCategoryName = if (selectedCategoryName == tapped?.category?.name) {
                                            null
                                        } else {
                                            tapped?.category?.name
                                        }
                                    }
                                }
                            }
                    ) {
                        val defaultStroke = 30.dp.toPx()
                        val selectedStroke = 38.dp.toPx()
                        val pad = selectedStroke / 2f + 4.dp.toPx()
                        val arcSize = Size(size.width - pad * 2, size.height - pad * 2)
                        var startAngle = -90f
                        val gap = if (breakdown.size > 1) 2.2f else 0f

                        breakdown.forEach { item ->
                            val sweep = ((item.percentageOfExpense / 100f) * 360f - gap).coerceAtLeast(1f)
                            val isSelected = item.category.name == selectedCategoryName
                            drawArc(
                                color = item.category.color,
                                startAngle = startAngle,
                                sweepAngle = sweep,
                                useCenter = false,
                                topLeft = Offset(pad, pad),
                                size = arcSize,
                                style = Stroke(
                                    width = if (isSelected) selectedStroke else defaultStroke,
                                    cap = StrokeCap.Butt
                                )
                            )
                            startAngle += sweep + gap
                        }
                    }

                    // Center donut summary
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.width(108.dp)
                    ) {
                        if (selectedItem != null) {
                            Text(
                                text = selectedItem.category.name,
                                style = MaterialTheme.typography.labelSmall,
                                color = selectedItem.category.color,
                                maxLines = 2,
                                textAlign = TextAlign.Center,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = CurrencyFormatter.formatCompactRupiah(selectedItem.totalAmount),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = String.format(Locale.US, "%.1f%%", selectedItem.percentageOfExpense),
                                style = MaterialTheme.typography.labelSmall,
                                color = SlateMutedInk
                            )
                        } else {
                            Text(
                                text = "Total Keluar",
                                style = MaterialTheme.typography.labelSmall,
                                color = SlateMutedInk
                            )
                            Text(
                                text = CurrencyFormatter.formatCompactRupiah(totalExpense),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${breakdown.size} kategori",
                                style = MaterialTheme.typography.labelSmall,
                                color = SlateMutedInk
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Quick category pills
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    breakdown.forEach { item ->
                        val isSelected = item.category.name == selectedCategoryName
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = if (isSelected) item.category.color.copy(alpha = 0.16f) else SoftSlateSurfaceVariant,
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) item.category.color else Color.Transparent
                            ),
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .clickable {
                                    selectedCategoryName = if (isSelected) null else item.category.name
                                }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(item.category.color)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = item.category.name,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Ranked horizontal progress breakdown bars
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    breakdown.forEach { item ->
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = item.category.icon,
                                        contentDescription = null,
                                        tint = item.category.color,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = item.category.name,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Text(
                                    text = "${CurrencyFormatter.formatRupiah(item.totalAmount)} (${
                                        String.format(Locale.US, "%.1f%%", item.percentageOfExpense)
                                    })",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { (item.percentageOfExpense / 100f).coerceIn(0f, 1f) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(7.dp)
                                    .clip(RoundedCornerShape(50)),
                                color = item.category.color,
                                trackColor = SoftSlateSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LegendDot(color: Color, label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
