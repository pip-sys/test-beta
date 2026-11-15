package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.components.ExpenseDoughnutChartCard
import com.example.ui.components.FinSmartHeaderBanner
import com.example.ui.components.MonthSelectorBar
import com.example.ui.components.MonthlyTrendCurveCard
import com.example.ui.components.SummaryCardsSection
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldOnContainer
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.SkyAdvisor
import com.example.ui.theme.SlateOutline
import com.example.ui.viewmodel.FinSmartUiState

@Composable
fun DashboardScreen(
    uiState: FinSmartUiState,
    onSelectMonth: (String) -> Unit,
    onStepMonth: (Int) -> Unit,
    onLoadDemo: () -> Unit,
    onResetData: () -> Unit,
    onNavigateToTransactions: () -> Unit,
    onNavigateToAdvisor: () -> Unit,
    modifier: Modifier = Modifier
) {
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
                FinSmartHeaderBanner(
                    onLoadDemoClick = onLoadDemo,
                    onResetClick = onResetData
                )

                MonthSelectorBar(
                    selectedYearMonth = uiState.selectedYearMonth,
                    availableMonths = uiState.availableMonths,
                    onSelectMonth = onSelectMonth,
                    onStepMonth = onStepMonth
                )

                SummaryCardsSection(
                    monthlyIncome = uiState.monthlyIncome,
                    monthlyExpense = uiState.monthlyExpense,
                    monthlyBalance = uiState.monthlyBalance
                )

                // Quick Hero Card with bundled student finance illustration
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = EmeraldContainer.copy(alpha = 0.7f)),
                    border = BorderStroke(1.dp, SlateOutline)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.img_hero_finance_1794669898065),
                                contentDescription = "Ilustrasi tabungan pelajar FinSmart Edu",
                                modifier = Modifier
                                    .size(width = 112.dp, height = 68.dp)
                                    .clip(RoundedCornerShape(14.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Lacak Uang Habis dari Mana",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = EmeraldOnContainer
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Catat pemasukan & cek simulasi hemat otomatis tiap bulan.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = EmeraldOnContainer.copy(alpha = 0.85f)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = onNavigateToTransactions,
                                modifier = Modifier
                                    .weight(1f)
                                    .minimumInteractiveComponentSize()
                                    .testTag("quick_add_tx_button"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.AddCircle,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Catat Transaksi")
                            }
                            OutlinedButton(
                                onClick = onNavigateToAdvisor,
                                modifier = Modifier
                                    .weight(1f)
                                    .minimumInteractiveComponentSize()
                                    .testTag("quick_advisor_button"),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, SkyAdvisor.copy(alpha = 0.5f)),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = SkyAdvisor)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.Lightbulb,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Saran Hemat")
                            }
                        }
                    }
                }

                MonthlyTrendCurveCard(
                    trends = uiState.monthlyTrends,
                    selectedYearMonth = uiState.selectedYearMonth
                )

                ExpenseDoughnutChartCard(
                    breakdown = uiState.categoryBreakdown,
                    totalExpense = uiState.monthlyExpense
                )

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}
