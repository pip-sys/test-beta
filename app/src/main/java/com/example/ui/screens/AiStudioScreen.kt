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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.Chat
import androidx.compose.material.icons.rounded.Image
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.ai.AiChatMessage
import com.example.data.ai.AiChatRolePreset
import com.example.data.ai.ImageSizeOption
import com.example.ui.theme.AmberContainer
import com.example.ui.theme.AmberHighlight
import com.example.ui.theme.AmberOnContainer
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
import com.example.ui.viewmodel.AiImageUiState

private enum class AiStudioTab(val label: String) {
    CHATBOT("Chatbot Keuangan"),
    IMAGE_GEN("Visual Target (1K-4K)")
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AiStudioScreen(
    isLiveAiConnected: Boolean,
    chatRole: AiChatRolePreset,
    chatMessages: List<AiChatMessage>,
    isChatLoading: Boolean,
    selectedImageSize: ImageSizeOption,
    imageUiState: AiImageUiState,
    onSelectChatRole: (AiChatRolePreset) -> Unit,
    onSendChatMessage: (String) -> Unit,
    onSelectImageSize: (ImageSizeOption) -> Unit,
    onGenerateGoalImage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var activeTab by remember { mutableStateOf(AiStudioTab.CHATBOT) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 720.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (!isLiveAiConnected) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = AmberContainer,
                    border = BorderStroke(1.dp, AmberHighlight.copy(alpha = 0.45f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("ai_firebase_notice_banner")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Info,
                            contentDescription = null,
                            tint = AmberOnContainer,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Mode contoh (placeholder): Fitur AI live Gemini & pembuatan gambar memerlukan Firebase.",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = AmberOnContainer
                        )
                    }
                }
            }

            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                AiStudioTab.entries.forEachIndexed { idx, tab ->
                    SegmentedButton(
                        selected = activeTab == tab,
                        onClick = { activeTab = tab },
                        shape = SegmentedButtonDefaults.itemShape(idx, AiStudioTab.entries.size),
                        icon = {
                            Icon(
                                imageVector = if (tab == AiStudioTab.CHATBOT) Icons.Rounded.Chat else Icons.Rounded.Image,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        modifier = Modifier.testTag("ai_tab_${tab.name.lowercase()}")
                    ) {
                        Text(tab.label, maxLines = 1)
                    }
                }
            }

            when (activeTab) {
                AiStudioTab.CHATBOT -> ChatbotTabContent(
                    chatRole = chatRole,
                    chatMessages = chatMessages,
                    isChatLoading = isChatLoading,
                    onSelectChatRole = onSelectChatRole,
                    onSendChatMessage = onSendChatMessage,
                    modifier = Modifier.weight(1f)
                )

                AiStudioTab.IMAGE_GEN -> ImageGeneratorTabContent(
                    selectedImageSize = selectedImageSize,
                    imageUiState = imageUiState,
                    onSelectImageSize = onSelectImageSize,
                    onGenerateGoalImage = onGenerateGoalImage,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ChatbotTabContent(
    chatRole: AiChatRolePreset,
    chatMessages: List<AiChatMessage>,
    isChatLoading: Boolean,
    onSelectChatRole: (AiChatRolePreset) -> Unit,
    onSendChatMessage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(chatMessages.size) {
        if (chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(chatMessages.lastIndex)
        }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Role & system instruction selector
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            AiChatRolePreset.entries.forEach { preset ->
                FilterChip(
                    selected = chatRole == preset,
                    onClick = { onSelectChatRole(preset) },
                    label = { Text("${preset.title} (${preset.modelLabel})", style = MaterialTheme.typography.labelSmall) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = SkyContainer,
                        selectedLabelColor = SkyOnContainer
                    )
                )
            }
        }

        // Scrollable chat thread
        Surface(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            color = PureWhiteSurface,
            border = BorderStroke(1.dp, SlateOutline)
        ) {
            LazyColumn(
                state = listState,
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("chat_message_list")
            ) {
                items(chatMessages, key = { it.id }) { msg ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (msg.isUser) Arrangement.End else Arrangement.Start
                    ) {
                        Surface(
                            shape = RoundedCornerShape(
                                topStart = 16.dp,
                                topEnd = 16.dp,
                                bottomStart = if (msg.isUser) 16.dp else 4.dp,
                                bottomEnd = if (msg.isUser) 4.dp else 16.dp
                            ),
                            color = if (msg.isUser) EmeraldPrimary else SoftSlateSurfaceVariant,
                            modifier = Modifier.widthIn(max = 310.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                if (!msg.isUser && msg.roleLabel.isNotEmpty()) {
                                    Text(
                                        text = if (msg.isPlaceholder) "${msg.roleLabel} • Contoh Lokal" else msg.roleLabel,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = SkyAdvisor
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                }
                                Text(
                                    text = msg.text,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (msg.isUser) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
                if (isChatLoading) {
                    item {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Menyiapkan respons...",
                                style = MaterialTheme.typography.bodySmall,
                                color = SlateMutedInk
                            )
                        }
                    }
                }
            }
        }

        // Quick prompts
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            listOf(
                "Cara hemat uang jajan?",
                "Evaluasi saldo bulan ini",
                "Tips capai target tabungan"
            ).forEach { suggestion ->
                SuggestionChip(
                    onClick = { onSendChatMessage(suggestion) },
                    label = { Text(suggestion, style = MaterialTheme.typography.labelSmall) }
                )
            }
        }

        // Message composer
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = { Text("Tanya soal keuanganmu...") },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .weight(1f)
                    .testTag("chat_input_field")
            )
            IconButton(
                onClick = {
                    val toSend = inputText.trim()
                    if (toSend.isNotEmpty()) {
                        onSendChatMessage(toSend)
                        inputText = ""
                    }
                },
                enabled = inputText.isNotBlank() && !isChatLoading,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = EmeraldPrimary,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .size(50.dp)
                    .minimumInteractiveComponentSize()
                    .testTag("chat_send_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.Send,
                    contentDescription = "Kirim pesan"
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ImageGeneratorTabContent(
    selectedImageSize: ImageSizeOption,
    imageUiState: AiImageUiState,
    onSelectImageSize: (ImageSizeOption) -> Unit,
    onGenerateGoalImage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var promptText by remember { mutableStateOf("") }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 12.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = PureWhiteSurface),
                border = BorderStroke(1.dp, SlateOutline)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Generator Visual Target Tabungan",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Visualisasikan barang atau tujuan impian dari hasil hematmu (contoh: laptop tugas akhir, dana darurat, atau beasiswa).",
                        style = MaterialTheme.typography.bodySmall,
                        color = SlateMutedInk
                    )

                    OutlinedTextField(
                        value = promptText,
                        onValueChange = { promptText = it },
                        label = { Text("Deskripsi Target Impian") },
                        placeholder = { Text("Misal: Laptop tipis modern di meja belajar rapi") },
                        minLines = 2,
                        maxLines = 4,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("image_prompt_input")
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf(
                            "Laptop baru untuk tugas akhir",
                            "Tabungan liburan kelulusan",
                            "Kamera mirrorless untuk konten"
                        ).forEach { preset ->
                            SuggestionChip(
                                onClick = { promptText = preset },
                                label = { Text(preset, style = MaterialTheme.typography.labelSmall) }
                            )
                        }
                    }

                    Text(
                        text = "Pilih Ukuran Gambar (Resolusi Output)",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ImageSizeOption.entries.forEach { sizeOption ->
                            val isSelected = selectedImageSize == sizeOption
                            FilterChip(
                                selected = isSelected,
                                onClick = { onSelectImageSize(sizeOption) },
                                label = {
                                    Text(
                                        text = "${sizeOption.label} (${sizeOption.pixelsLabel})",
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = EmeraldContainer,
                                    selectedLabelColor = EmeraldOnContainer
                                ),
                                modifier = Modifier.testTag("image_size_${sizeOption.label.lowercase()}")
                            )
                        }
                    }

                    Button(
                        onClick = { onGenerateGoalImage(promptText) },
                        enabled = promptText.isNotBlank() && imageUiState !is AiImageUiState.Loading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .minimumInteractiveComponentSize()
                            .testTag("generate_goal_image_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SkyAdvisor)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.AutoAwesome,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Buat Visual Target (${selectedImageSize.label})")
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("image_result_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = PureWhiteSurface),
                border = BorderStroke(1.dp, SlateOutline)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    when (imageUiState) {
                        is AiImageUiState.Idle -> {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Rounded.Image,
                                    contentDescription = null,
                                    tint = SlateMutedInk,
                                    modifier = Modifier.size(40.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Masukkan deskripsi target & pilih ukuran 1K, 2K, atau 4K.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = SlateMutedInk
                                )
                            }
                        }

                        is AiImageUiState.Loading -> {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator()
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Menyiapkan pratinjau visual (${selectedImageSize.label})...",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = SlateMutedInk
                                )
                            }
                        }

                        is AiImageUiState.PlaceholderPreview -> {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = SkyContainer,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(14.dp)) {
                                        Text(
                                            text = "Pratinjau Placeholder (${imageUiState.size.label})",
                                            style = MaterialTheme.typography.titleSmall,
                                            color = SkyOnContainer
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = imageUiState.summary,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = SkyOnContainer
                                        )
                                    }
                                }
                                Text(
                                    text = imageUiState.notice,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = AmberOnContainer
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
