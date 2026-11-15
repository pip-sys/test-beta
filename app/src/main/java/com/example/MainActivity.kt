package com.example

import android.graphics.Color as AndroidColor
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Dashboard
import androidx.compose.material.icons.rounded.Lightbulb
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.AiStudioScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.SmartAdvisorScreen
import com.example.ui.screens.TransactionsScreen
import com.example.ui.theme.CoralDeep
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldOnContainer
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.FinSmartEduTheme
import com.example.ui.theme.PureWhiteSurface
import com.example.ui.theme.SlateBackground
import com.example.ui.theme.SlateMutedInk
import com.example.ui.viewmodel.FinSmartViewModel

enum class FinSmartDestination(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    DASHBOARD("Ringkasan", Icons.Rounded.Dashboard, Icons.Outlined.Dashboard, "nav_dashboard"),
    TRANSACTIONS("Transaksi", Icons.Rounded.ReceiptLong, Icons.Outlined.ReceiptLong, "nav_transactions"),
    ADVISOR("Saran Hemat", Icons.Rounded.Lightbulb, Icons.Outlined.Lightbulb, "nav_advisor"),
    AI_STUDIO("Asisten AI", Icons.Rounded.AutoAwesome, Icons.Outlined.AutoAwesome, "nav_ai_studio")
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                AndroidColor.TRANSPARENT,
                AndroidColor.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.light(
                AndroidColor.TRANSPARENT,
                AndroidColor.TRANSPARENT
            )
        )
        setContent {
            FinSmartEduTheme {
                FinSmartApp()
            }
        }
    }
}

@Composable
fun FinSmartApp(
    viewModel: FinSmartViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val chatRole by viewModel.chatRole.collectAsStateWithLifecycle()
    val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val isChatLoading by viewModel.isChatLoading.collectAsStateWithLifecycle()
    val selectedImageSize by viewModel.selectedImageSize.collectAsStateWithLifecycle()
    val imageUiState by viewModel.imageUiState.collectAsStateWithLifecycle()

    var currentDestination by rememberSaveable { mutableStateOf(FinSmartDestination.DASHBOARD) }
    var showResetConfirm by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.snackbarMessage) {
        val message = uiState.snackbarMessage
        if (message != null) {
            snackbarHostState.showSnackbar(message)
            viewModel.clearSnackbar()
        }
    }

    if (currentDestination != FinSmartDestination.DASHBOARD) {
        BackHandler {
            currentDestination = FinSmartDestination.DASHBOARD
        }
    }

    if (showResetConfirm) {
        AlertDialog(
            onDismissRequest = { showResetConfirm = false },
            title = { Text("Reset Semua Data?") },
            text = {
                Text("Semua catatan transaksi di semua bulan akan dihapus. Kamu dapat memuat ulang Demo 6 Bulan kapan saja.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.resetAllData()
                        showResetConfirm = false
                    }
                ) {
                    Text("Ya, Reset", color = CoralDeep)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirm = false }) {
                    Text("Batal")
                }
            }
        )
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val useRail = maxWidth >= 720.dp

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = SlateBackground,
            contentWindowInsets = WindowInsets.safeDrawing,
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            bottomBar = {
                if (!useRail) {
                    NavigationBar(
                        containerColor = PureWhiteSurface,
                        tonalElevation = 2.dp
                    ) {
                        FinSmartDestination.entries.forEach { dest ->
                            val selected = currentDestination == dest
                            NavigationBarItem(
                                selected = selected,
                                onClick = { currentDestination = dest },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) dest.selectedIcon else dest.unselectedIcon,
                                        contentDescription = dest.label
                                    )
                                },
                                label = {
                                    Text(
                                        text = dest.label,
                                        style = MaterialTheme.typography.labelSmall,
                                        maxLines = 1
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = EmeraldOnContainer,
                                    selectedTextColor = EmeraldPrimary,
                                    indicatorColor = EmeraldContainer,
                                    unselectedIconColor = SlateMutedInk,
                                    unselectedTextColor = SlateMutedInk
                                ),
                                modifier = Modifier.testTag(dest.testTag)
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (useRail) {
                    NavigationRail(
                        containerColor = PureWhiteSurface,
                        modifier = Modifier.fillMaxHeight()
                    ) {
                        FinSmartDestination.entries.forEach { dest ->
                            val selected = currentDestination == dest
                            NavigationRailItem(
                                selected = selected,
                                onClick = { currentDestination = dest },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) dest.selectedIcon else dest.unselectedIcon,
                                        contentDescription = dest.label
                                    )
                                },
                                label = { Text(dest.label) },
                                colors = NavigationRailItemDefaults.colors(
                                    selectedIconColor = EmeraldOnContainer,
                                    selectedTextColor = EmeraldPrimary,
                                    indicatorColor = EmeraldContainer,
                                    unselectedIconColor = SlateMutedInk,
                                    unselectedTextColor = SlateMutedInk
                                ),
                                modifier = Modifier.testTag(dest.testTag)
                            )
                        }
                    }
                }

                Box(modifier = Modifier.weight(1f).fillMaxSize()) {
                    when (currentDestination) {
                        FinSmartDestination.DASHBOARD -> DashboardScreen(
                            uiState = uiState,
                            onSelectMonth = viewModel::selectMonth,
                            onStepMonth = viewModel::stepMonth,
                            onLoadDemo = viewModel::loadSixMonthDemo,
                            onResetData = { showResetConfirm = true },
                            onNavigateToTransactions = {
                                currentDestination = FinSmartDestination.TRANSACTIONS
                            },
                            onNavigateToAdvisor = {
                                currentDestination = FinSmartDestination.ADVISOR
                            }
                        )

                        FinSmartDestination.TRANSACTIONS -> TransactionsScreen(
                            uiState = uiState,
                            onSelectMonth = viewModel::selectMonth,
                            onStepMonth = viewModel::stepMonth,
                            onAddTransaction = viewModel::addTransaction,
                            onDeleteTransaction = viewModel::deleteTransaction
                        )

                        FinSmartDestination.ADVISOR -> SmartAdvisorScreen(
                            uiState = uiState,
                            onSelectMonth = viewModel::selectMonth,
                            onStepMonth = viewModel::stepMonth,
                            onSliderChange = viewModel::updateAdvisorSlider,
                            onSelectTargetCategory = viewModel::selectAdvisorCategory
                        )

                        FinSmartDestination.AI_STUDIO -> AiStudioScreen(
                            isLiveAiConnected = viewModel.isLiveAiConnected,
                            chatRole = chatRole,
                            chatMessages = chatMessages,
                            isChatLoading = isChatLoading,
                            selectedImageSize = selectedImageSize,
                            imageUiState = imageUiState,
                            onSelectChatRole = viewModel::selectChatRole,
                            onSendChatMessage = viewModel::sendChatMessage,
                            onSelectImageSize = viewModel::selectImageSize,
                            onGenerateGoalImage = viewModel::generateGoalImage
                        )
                    }
                }
            }
        }
    }
}
