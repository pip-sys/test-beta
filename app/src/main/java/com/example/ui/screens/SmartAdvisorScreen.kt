package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoGraph
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material.icons.rounded.Savings
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.domain.model.CurrencyFormatter
import com.example.ui.components.MonthSelectorBar
import com.example.ui.theme.AmberBright
import com.example.ui.theme.AmberContainer
import com.example.ui.theme.AmberHighlight
import com.example.ui.theme.AmberOnContainer
import com.example.ui.theme.CoralDeep
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldOnContainer
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.PureWhiteSurface
import com.example.ui.theme.SkyAdvisor
import com.example.ui.theme.SkyContainer
import com.example.ui.theme.SkyOnContainer
import com.example.ui.theme.SlateMutedInk
import com.example.ui.theme.SlateOutline
import com.example.ui.theme.SoftSlateSurfaceVariant
import com.example.ui.viewmodel.FinSmartUiState
import java.util.Locale
import kotlin.math.roundToInt

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SmartAdvisorScreen(
    uiState: FinSmartUiState,
    onSelectMonth: (String) -> Unit,
    onStepMonth: (Int) -> Unit,
    onSliderChange: (Int) -> Unit,
    onSelectTargetCategory: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val advice = uiState.smartAdvice

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Column(
                modifier = Modifier.widthIn(max = 720.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                MonthSelectorBar(
                    selectedYearMonth = uiState.selectedYearMonth,
                    availableMonths = uiState.availableMonths,
                    onSelectMonth = onSelectMonth,
                    onStepMonth = onStepMonth
                )

                if (advice == null || advice.selectedTargetCategory == null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = SkyContainer.copy(alpha = 0.55f)),
                        border = BorderStroke(1.dp, SkyAdvisor.copy(alpha = 0.3f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Lightbulb,
                                contentDescription = null,
                                tint = AmberHighlight,
                                modifier = Modifier.size(44.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = "Belum Ada Data Pengeluaran di Bulan Ini",
                                style = MaterialTheme.typography.titleMedium,
                                color = SkyOnContainer,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Tambahkan transaksi pengeluaran atau tekan \"Muat Demo 6 Bulan\" di Beranda untuk melihat analisis Saran Hemat Pintar.",
                                style = MaterialTheme.typography.bodySmall,
                                color = SkyOnContainer.copy(alpha = 0.85f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    // Smart Saving Advisor Hero Panel (Sky Blue + Amber bright panel)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("smart_advisor_panel"),
                        shape = RoundedCornerShape(22.dp),
                        border = BorderStroke(1.dp, SkyAdvisor.copy(alpha = 0.35f)),
                        colors = CardDefaults.cardColors(containerColor = PureWhiteSurface)
                    ) {
                        Column {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        Brush.linearGradient(
                                            colors = listOf(SkyContainer, AmberContainer.copy(alpha = 0.85f))
                                        )
                                    )
                                    .padding(16.dp)
                            ) {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(38.dp)
                                                .clip(CircleShape)
                                                .background(SkyAdvisor),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Rounded.Lightbulb,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = "Saran Hemat Pintar",
                                                style = MaterialTheme.typography.titleMedium,
                                                color = SkyOnContainer
                                            )
                                            Text(
                                                text = "Deteksi Otomatis Kategori Non-Esensial Terboros",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = AmberOnContainer
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(14.dp))

                                    Surface(
                                        shape = RoundedCornerShape(14.dp),
                                        color = PureWhiteSurface.copy(alpha = 0.92f),
                                        border = BorderStroke(1.dp, AmberBright.copy(alpha = 0.45f))
                                    ) {
                                        Column(modifier = Modifier.padding(14.dp)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Rounded.WarningAmber,
                                                    contentDescription = null,
                                                    tint = AmberHighlight,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = "Rekomendasi Utama Bulan Depan",
                                                    style = MaterialTheme.typography.labelLarge,
                                                    color = AmberOnContainer
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = advice.headlineAdvice,
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = advice.actionTip,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = SlateMutedInk
                                            )
                                        }
                                    }
                                }
                            }

                            // Interactive "Target Hemat" Slider Section
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Rounded.Tune,
                                            contentDescription = null,
                                            tint = SkyAdvisor,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Slider Target Hemat",
                                            style = MaterialTheme.typography.titleSmall,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(50),
                                        color = AmberContainer
                                    ) {
                                        Text(
                                            text = "Potong ${advice.sliderPercent}%",
                                            style = MaterialTheme.typography.labelLarge,
                                            color = AmberOnContainer,
                                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                // Category Switcher for Simulation
                                Text(
                                    text = "Pilih kategori yang ingin disimulasikan:",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = SlateMutedInk
                                )
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    advice.rankedExpenses.take(4).forEach { item ->
                                        val isTarget = item.category.name == advice.selectedTargetCategory.category.name
                                        FilterChip(
                                            selected = isTarget,
                                            onClick = { onSelectTargetCategory(item.category.name) },
                                            label = {
                                                Text(
                                                    text = if (item.category.isNonEssential) {
                                                        "${item.category.name} • Non-Esensial"
                                                    } else {
                                                        item.category.name
                                                    },
                                                    style = MaterialTheme.typography.labelSmall
                                                )
                                            },
                                            colors = FilterChipDefaults.filterChipColors(
                                                selectedContainerColor = SkyContainer,
                                                selectedLabelColor = SkyOnContainer
                                            )
                                        )
                                    }
                                }

                                Slider(
                                    value = advice.sliderPercent.toFloat(),
                                    onValueChange = { onSliderChange(it.roundToInt()) },
                                    valueRange = 10f..50f,
                                    steps = 7,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("target_hemat_slider"),
                                    colors = SliderDefaults.colors(
                                        thumbColor = SkyAdvisor,
                                        activeTrackColor = SkyAdvisor,
                                        inactiveTrackColor = SkyContainer
                                    )
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("10% (Santai)", style = MaterialTheme.typography.labelSmall, color = SlateMutedInk)
                                    Text("30% (Ideal)", style = MaterialTheme.typography.labelSmall, color = SkyAdvisor)
                                    Text("50% (Ketat)", style = MaterialTheme.typography.labelSmall, color = AmberHighlight)
                                }

                                // Simulation Result Cards
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Surface(
                                        modifier = Modifier
                                            .weight(1f)
                                            .testTag("monthly_savings_simulation"),
                                        shape = RoundedCornerShape(14.dp),
                                        color = EmeraldContainer
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Rounded.Savings,
                                                    contentDescription = null,
                                                    tint = EmeraldOnContainer,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = "Hemat / Bulan",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = EmeraldOnContainer
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = CurrencyFormatter.formatRupiah(advice.estimatedMonthlySavings),
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = EmeraldPrimary
                                            )
                                        }
                                    }

                                    Surface(
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(14.dp),
                                        color = SkyContainer
                                    ) {
                                        Column(modifier = Modifier.padding(12.dp)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Rounded.AutoGraph,
                                                    contentDescription = null,
                                                    tint = SkyOnContainer,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = "Proyeksi 6 Bulan",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = SkyOnContainer
                                                )
                                            }
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = CurrencyFormatter.formatRupiah(advice.estimatedSixMonthSavings),
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = SkyAdvisor
                                            )
                                        }
                                    }
                                }

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
                                            text = "Proyeksi Sisa Saldo Bulan Depan",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = SlateMutedInk
                                        )
                                        Text(
                                            text = CurrencyFormatter.formatRupiah(advice.projectedNewBalance),
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = EmeraldPrimary
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Ranking Card: "Uangmu paling banyak habis di..."
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("biggest_expense_ranking_card"),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = PureWhiteSurface),
                        border = BorderStroke(1.dp, SlateOutline)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Uangmu Paling Banyak Habis di...",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            advice.rankedExpenses.forEachIndexed { index, item ->
                                Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            shape = CircleShape,
                                            color = if (index == 0) CoralDeep else SoftSlateSurfaceVariant,
                                            modifier = Modifier.size(26.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(
                                                    text = "#${index + 1}",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (index == 0) Color.White else SlateMutedInk
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = item.category.name,
                                                    style = MaterialTheme.typography.titleSmall,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                                if (item.category.isNonEssential) {
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Surface(
                                                        shape = RoundedCornerShape(6.dp),
                                                        color = AmberContainer
                                                    ) {
                                                        Text(
                                                            text = "Bisa Dihemat",
                                                            style = MaterialTheme.typography.labelSmall,
                                                            color = AmberOnContainer,
                                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                                                        )
                                                    }
                                                }
                                            }
                                            Text(
                                                text = "${item.transactionCount} transaksi • ${
                                                    String.format(Locale.US, "%.1f%%", item.percentageOfExpense)
                                                }",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = SlateMutedInk
                                            )
                                        }
                                        Text(
                                            text = CurrencyFormatter.formatRupiah(item.totalAmount),
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = if (index == 0) CoralDeep else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    LinearProgressIndicator(
                                        progress = { (item.percentageOfExpense / 100f).coerceIn(0f, 1f) },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(6.dp)
                                            .clip(RoundedCornerShape(50)),
                                        color = item.category.color,
                                        trackColor = SoftSlateSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}
