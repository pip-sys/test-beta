package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.DeleteOutline
import androidx.compose.material.icons.rounded.ReceiptLong
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.domain.model.CategoryCatalog
import com.example.domain.model.CurrencyFormatter
import com.example.domain.model.FinancialTransaction
import com.example.domain.model.TransactionType
import com.example.ui.components.MonthSelectorBar
import com.example.ui.theme.CoralContainer
import com.example.ui.theme.CoralDeep
import com.example.ui.theme.CoralOnContainer
import com.example.ui.theme.EmeraldContainer
import com.example.ui.theme.EmeraldOnContainer
import com.example.ui.theme.EmeraldPrimary
import com.example.ui.theme.PureWhiteSurface
import com.example.ui.theme.SlateMutedInk
import com.example.ui.theme.SlateOutline
import com.example.ui.theme.SoftSlateSurfaceVariant
import com.example.ui.viewmodel.FinSmartUiState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

private enum class HistoryFilter(val label: String) {
    ALL("Semua"),
    EXPENSE("Pengeluaran"),
    INCOME("Pemasukan")
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun TransactionsScreen(
    uiState: FinSmartUiState,
    onSelectMonth: (String) -> Unit,
    onStepMonth: (Int) -> Unit,
    onAddTransaction: (title: String, amount: Long, type: TransactionType, category: String, dateIso: String) -> Unit,
    onDeleteTransaction: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedType by remember { mutableStateOf(TransactionType.EXPENSE) }
    var titleInput by remember { mutableStateOf("") }
    var amountInput by remember { mutableStateOf("") }
    var selectedDateIso by remember { mutableStateOf("${uiState.selectedYearMonth}-14") }
    var selectedCategory by remember {
        mutableStateOf(CategoryCatalog.expenseCategories.first().name)
    }
    var showDatePicker by remember { mutableStateOf(false) }
    var historyFilter by remember { mutableStateOf(HistoryFilter.ALL) }
    var formError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(uiState.selectedYearMonth) {
        if (!selectedDateIso.startsWith(uiState.selectedYearMonth)) {
            selectedDateIso = "${uiState.selectedYearMonth}-14"
        }
    }

    LaunchedEffect(selectedType) {
        val categories = if (selectedType == TransactionType.EXPENSE) {
            CategoryCatalog.expenseCategories
        } else {
            CategoryCatalog.incomeCategories
        }
        if (categories.none { it.name == selectedCategory }) {
            selectedCategory = categories.first().name
        }
    }

    val filteredHistory = remember(uiState.monthlyTransactions, historyFilter) {
        when (historyFilter) {
            HistoryFilter.ALL -> uiState.monthlyTransactions
            HistoryFilter.EXPENSE -> uiState.monthlyTransactions.filter { it.type == TransactionType.EXPENSE }
            HistoryFilter.INCOME -> uiState.monthlyTransactions.filter { it.type == TransactionType.INCOME }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
                                timeZone = TimeZone.getTimeZone("UTC")
                            }
                            selectedDateIso = fmt.format(Date(millis))
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("Pilih")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Batal")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Column(
                modifier = Modifier.widthIn(max = 720.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MonthSelectorBar(
                    selectedYearMonth = uiState.selectedYearMonth,
                    availableMonths = uiState.availableMonths,
                    onSelectMonth = onSelectMonth,
                    onStepMonth = onStepMonth
                )

                // Transaction Input Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = PureWhiteSurface),
                    border = BorderStroke(1.dp, SlateOutline),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Catat Transaksi Baru",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                            TransactionType.entries.forEachIndexed { index, type ->
                                SegmentedButton(
                                    selected = selectedType == type,
                                    onClick = { selectedType = type },
                                    shape = SegmentedButtonDefaults.itemShape(
                                        index = index,
                                        count = TransactionType.entries.size
                                    ),
                                    colors = SegmentedButtonDefaults.colors(
                                        activeContainerColor = if (type == TransactionType.EXPENSE) CoralContainer else EmeraldContainer,
                                        activeContentColor = if (type == TransactionType.EXPENSE) CoralOnContainer else EmeraldOnContainer
                                    ),
                                    modifier = Modifier.testTag("tx_type_${type.name.lowercase()}")
                                ) {
                                    Text(type.label)
                                }
                            }
                        }

                        OutlinedTextField(
                            value = titleInput,
                            onValueChange = {
                                titleInput = it
                                formError = null
                            },
                            label = { Text("Nama Transaksi") },
                            placeholder = {
                                Text(
                                    if (selectedType == TransactionType.EXPENSE) "Misal: Es Kopi Susu & Camilan"
                                    else "Misal: Uang Saku Bulanan"
                                )
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("tx_title_input")
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = amountInput,
                                onValueChange = { raw ->
                                    amountInput = raw.filter { it.isDigit() }.take(10)
                                    formError = null
                                },
                                label = { Text("Nominal (Rp)") },
                                placeholder = { Text("50000") },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .weight(1.2f)
                                    .testTag("tx_amount_input")
                            )

                            Surface(
                                shape = RoundedCornerShape(14.dp),
                                color = SoftSlateSurfaceVariant,
                                border = BorderStroke(1.dp, SlateOutline),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(56.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .clickable { showDatePicker = true }
                                    .testTag("tx_date_picker_button")
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.CalendarToday,
                                        contentDescription = "Pilih tanggal",
                                        tint = EmeraldPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = CurrencyFormatter.formatDateDisplay(selectedDateIso),
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.SemiBold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }

                        val parsedPreview = amountInput.toLongOrNull()
                        if (parsedPreview != null && parsedPreview > 0L) {
                            Text(
                                text = "Preview: ${CurrencyFormatter.formatRupiah(parsedPreview)}",
                                style = MaterialTheme.typography.labelMedium,
                                color = if (selectedType == TransactionType.EXPENSE) CoralDeep else EmeraldPrimary
                            )
                        }

                        Text(
                            text = if (selectedType == TransactionType.EXPENSE) {
                                "Kategori Pengeluaran (Pelacak Uang Habis)"
                            } else {
                                "Kategori Pemasukan"
                            },
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        val availableCats = if (selectedType == TransactionType.EXPENSE) {
                            CategoryCatalog.expenseCategories
                        } else {
                            CategoryCatalog.incomeCategories
                        }

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            availableCats.forEach { cat ->
                                val isSelected = selectedCategory == cat.name
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { selectedCategory = cat.name },
                                    label = { Text(cat.name) },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = cat.icon,
                                            contentDescription = null,
                                            tint = if (isSelected) Color.White else cat.color,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = cat.color,
                                        selectedLabelColor = Color.White
                                    )
                                )
                            }
                        }

                        if (formError != null) {
                            Text(
                                text = formError!!,
                                style = MaterialTheme.typography.bodySmall,
                                color = CoralDeep
                            )
                        }

                        Button(
                            onClick = {
                                val nominal = amountInput.toLongOrNull() ?: 0L
                                when {
                                    titleInput.isBlank() -> formError = "Nama transaksi wajib diisi."
                                    nominal <= 0L -> formError = "Masukkan nominal Rupiah yang valid (> 0)."
                                    else -> {
                                        onAddTransaction(
                                            titleInput,
                                            nominal,
                                            selectedType,
                                            selectedCategory,
                                            selectedDateIso
                                        )
                                        titleInput = ""
                                        amountInput = ""
                                        formError = null
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .minimumInteractiveComponentSize()
                                .testTag("submit_transaction_button"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selectedType == TransactionType.EXPENSE) CoralDeep else EmeraldPrimary
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Add,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Simpan ${selectedType.label}")
                        }
                    }
                }

                // History section header + filter chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Riwayat Bulan Ini (${filteredHistory.size})",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        HistoryFilter.entries.forEach { filter ->
                            FilterChip(
                                selected = historyFilter == filter,
                                onClick = { historyFilter = filter },
                                label = { Text(filter.label, style = MaterialTheme.typography.labelSmall) }
                            )
                        }
                    }
                }
            }
        }

        if (filteredHistory.isEmpty()) {
            item {
                Surface(
                    modifier = Modifier
                        .widthIn(max = 720.dp)
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    color = PureWhiteSurface,
                    border = BorderStroke(1.dp, SlateOutline)
                ) {
                    Column(
                        modifier = Modifier.padding(28.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.ReceiptLong,
                            contentDescription = null,
                            tint = SlateMutedInk,
                            modifier = Modifier.size(38.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Belum ada transaksi di bulan ini",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Tambahkan transaksi di form atas atau muat data demo 6 bulan dari Beranda.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(
                items = filteredHistory,
                key = { it.id }
            ) { tx ->
                TransactionHistoryCard(
                    transaction = tx,
                    monthlyExpenseTotal = uiState.monthlyExpense,
                    onDelete = { onDeleteTransaction(tx.id) },
                    modifier = Modifier.widthIn(max = 720.dp)
                )
            }
        }

        item { Spacer(modifier = Modifier.height(8.dp)) }
    }
}

@Composable
private fun TransactionHistoryCard(
    transaction: FinancialTransaction,
    monthlyExpenseTotal: Long,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val categoryInfo = remember(transaction.category, transaction.type) {
        CategoryCatalog.findInfo(transaction.category, transaction.type)
    }
    val isExpense = transaction.type == TransactionType.EXPENSE
    val expenseSharePct = if (isExpense && monthlyExpenseTotal > 0L) {
        (transaction.amount.toFloat() / monthlyExpenseTotal.toFloat()) * 100f
    } else null

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("transaction_item_card_${transaction.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PureWhiteSurface),
        border = BorderStroke(1.dp, SlateOutline)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(categoryInfo.color.copy(alpha = 0.14f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = categoryInfo.icon,
                    contentDescription = categoryInfo.name,
                    tint = categoryInfo.color,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(3.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = categoryInfo.color.copy(alpha = 0.14f)
                    ) {
                        Text(
                            text = categoryInfo.name,
                            style = MaterialTheme.typography.labelSmall,
                            color = categoryInfo.color,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Text(
                        text = CurrencyFormatter.formatDateDisplay(transaction.dateIso),
                        style = MaterialTheme.typography.labelSmall,
                        color = SlateMutedInk
                    )
                }
                if (expenseSharePct != null) {
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = String.format(
                            Locale.US,
                            "%.1f%% dari total pengeluaran bulan ini",
                            expenseSharePct
                        ),
                        style = MaterialTheme.typography.labelSmall,
                        color = CoralDeep.copy(alpha = 0.85f)
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = (if (isExpense) "-" else "+") + CurrencyFormatter.formatRupiah(transaction.amount),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isExpense) CoralDeep else EmeraldPrimary
                )
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("delete_tx_${transaction.id}")
                ) {
                    Icon(
                        imageVector = Icons.Rounded.DeleteOutline,
                        contentDescription = "Hapus transaksi",
                        tint = SlateMutedInk
                    )
                }
            }
        }
    }
}
