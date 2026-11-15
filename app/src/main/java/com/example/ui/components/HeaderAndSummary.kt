package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.automirrored.rounded.TrendingDown
import androidx.compose.material.icons.automirrored.rounded.TrendingUp
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.DeleteSweep
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.Savings
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.domain.model.CurrencyFormatter
import com.example.ui.theme.AmberContainer
import com.example.ui.theme.AmberOnContainer
import com.example.ui.theme.CoralContainer
import com.example.ui.theme.CoralDeep
import com.example.ui.theme.CoralOnContainer
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldOnContainer
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.PureWhiteSurface
import com.example.ui.theme.SkyAdvisor
import com.example.ui.theme.SkyContainer
import com.example.ui.theme.SkyOnContainer
import com.example.ui.theme.SlateOutline

@Composable
fun FinSmartHeaderBanner(
    onLoadDemoClick: () -> Unit,
    onResetClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        color = PureWhiteSurface,
        border = BorderStroke(1.dp, SlateOutline),
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_finsmart_logo_1794669882841),
                    contentDescription = "Logo FinSmart Edu",
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "FinSmart Edu",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Pencatat Keuangan Pintar • Tugas Akhir",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                FilledTonalButton(
                    onClick = onLoadDemoClick,
                    modifier = Modifier
                        .weight(1f)
                        .minimumInteractiveComponentSize()
                        .testTag("load_demo_button"),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = EmeraldContainer,
                        contentColor = EmeraldOnContainer
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.AutoAwesome,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Muat Demo 6 Bulan",
                        style = MaterialTheme.typography.labelLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                OutlinedButton(
                    onClick = onResetClick,
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("reset_data_button"),
                    border = BorderStroke(1.dp, CoralDeep.copy(alpha = 0.35f)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = CoralDeep
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.DeleteSweep,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Reset Data",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}

@Composable
fun MonthSelectorBar(
    selectedYearMonth: String,
    availableMonths: List<String>,
    onSelectMonth: (String) -> Unit,
    onStepMonth: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val currentIdx = availableMonths.indexOf(selectedYearMonth)

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = PureWhiteSurface,
        border = BorderStroke(1.dp, SlateOutline)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = { onStepMonth(-1) },
                enabled = currentIdx > 0,
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .testTag("prev_month_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "Bulan sebelumnya"
                )
            }

            Box {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { expanded = true }
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                        .testTag("month_dropdown_trigger"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Rounded.CalendarMonth,
                        contentDescription = null,
                        tint = EmeraldPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = CurrencyFormatter.formatYearMonthFull(selectedYearMonth),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.Rounded.ExpandMore,
                        contentDescription = "Pilih bulan",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    availableMonths.forEach { ym ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = CurrencyFormatter.formatYearMonthFull(ym),
                                    fontWeight = if (ym == selectedYearMonth) FontWeight.Bold else FontWeight.Normal,
                                    color = if (ym == selectedYearMonth) EmeraldPrimary else MaterialTheme.colorScheme.onSurface
                                )
                            },
                            onClick = {
                                onSelectMonth(ym)
                                expanded = false
                            }
                        )
                    }
                }
            }

            IconButton(
                onClick = { onStepMonth(1) },
                enabled = currentIdx in 0 until availableMonths.lastIndex,
                modifier = Modifier
                    .minimumInteractiveComponentSize()
                    .testTag("next_month_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                    contentDescription = "Bulan berikutnya"
                )
            }
        }
    }
}

@Composable
fun SummaryCardsSection(
    monthlyIncome: Long,
    monthlyExpense: Long,
    monthlyBalance: Long,
    modifier: Modifier = Modifier
) {
    val savingsRatePercent = if (monthlyIncome > 0L) {
        ((monthlyBalance.toFloat() / monthlyIncome.toFloat()) * 100f).toInt()
    } else 0

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Sisa Saldo Hero Card
        val isPositiveBalance = monthlyBalance >= 0L
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("balance_summary_card"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isPositiveBalance) SkyContainer.copy(alpha = 0.55f) else CoralContainer
            ),
            border = BorderStroke(
                1.dp,
                if (isPositiveBalance) SkyAdvisor.copy(alpha = 0.25f) else CoralDeep.copy(alpha = 0.3f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(if (isPositiveBalance) SkyAdvisor else CoralDeep),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Savings,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Sisa Saldo / Tabungan",
                        style = MaterialTheme.typography.labelLarge,
                        color = if (isPositiveBalance) SkyOnContainer else CoralOnContainer
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = CurrencyFormatter.formatRupiah(monthlyBalance),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Surface(
                    shape = RoundedCornerShape(50),
                    color = if (isPositiveBalance) AmberContainer else CoralDeep.copy(alpha = 0.14f)
                ) {
                    Text(
                        text = if (monthlyIncome > 0L) "Hemat $savingsRatePercent%" else "Belum ada data",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isPositiveBalance) AmberOnContainer else CoralOnContainer,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // Pemasukan & Pengeluaran side-by-side
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MetricMiniCard(
                title = "Total Pemasukan",
                amount = monthlyIncome,
                accentColor = EmeraldPrimary,
                badgeBackground = EmeraldContainer,
                badgeTextColor = EmeraldOnContainer,
                icon = Icons.AutoMirrored.Rounded.TrendingUp,
                modifier = Modifier
                    .weight(1f)
                    .testTag("income_summary_card")
            )
            MetricMiniCard(
                title = "Total Pengeluaran",
                amount = monthlyExpense,
                accentColor = CoralDeep,
                badgeBackground = CoralContainer,
                badgeTextColor = CoralOnContainer,
                icon = Icons.AutoMirrored.Rounded.TrendingDown,
                modifier = Modifier
                    .weight(1f)
                    .testTag("expense_summary_card")
            )
        }
    }
}

@Composable
private fun MetricMiniCard(
    title: String,
    amount: Long,
    accentColor: Color,
    badgeBackground: Color,
    badgeTextColor: Color,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = PureWhiteSurface),
        border = BorderStroke(1.dp, SlateOutline),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(badgeBackground),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = badgeTextColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = CurrencyFormatter.formatRupiah(amount),
                style = MaterialTheme.typography.titleLarge,
                color = accentColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
