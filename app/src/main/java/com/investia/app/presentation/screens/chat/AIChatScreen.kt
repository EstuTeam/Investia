package com.investia.app.presentation.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.investia.app.domain.model.ChatMessage
import com.investia.app.presentation.components.GlassCard
import com.investia.app.presentation.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIChatScreen(
    navController: NavController,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Auto-scroll when new messages arrive
    LaunchedEffect(state.messages.size, state.isTyping) {
        if (state.messages.isNotEmpty()) {
            listState.animateScrollToItem(
                index = listState.layoutInfo.totalItemsCount.coerceAtLeast(1) - 1
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Geri", tint = MaterialTheme.colorScheme.onSurface)
            }
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(listOf(InvestiaPrimary, InvestiaAccent))
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.SmartToy, null, tint = Color.White, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text("AI Asistan", fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onBackground)
                Text("Yatırım danışmanınız", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        // Messages
        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (state.messages.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(top = 48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(InvestiaPrimary.copy(alpha = 0.12f))
                                .border(1.dp, InvestiaPrimary.copy(alpha = 0.25f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.SmartToy, null, modifier = Modifier.size(36.dp),
                                tint = InvestiaPrimary)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Merhaba! Ben AI yatırım asistanınız.",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onBackground)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("BIST hisseleri, teknik analiz, strateji hakkında\nsorularınızı sorabilirsiniz.",
                            style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    val suggestions = listOf(
                        "THYAO hakkında ne düşünüyorsun?",
                        "Bugün hangi hisselere bakmalıyım?",
                        "RSI nedir, nasıl kullanılır?"
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        suggestions.forEach { suggestion ->
                            OutlinedButton(
                                onClick = {
                                    viewModel.sendMessage(suggestion)
                                    inputText = ""
                                },
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.fillMaxWidth(),
                                border = ButtonDefaults.outlinedButtonBorder.copy(
                                    brush = Brush.linearGradient(
                                        listOf(InvestiaPrimary.copy(alpha = 0.3f), InvestiaAccent.copy(alpha = 0.15f))
                                    )
                                ),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
                                )
                            ) {
                                Text(suggestion, color = InvestiaPrimaryLight,
                                    style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }

            items(state.messages) { message ->
                ChatBubble(message = message)
            }

            if (state.isTyping) {
                item {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(InvestiaPrimary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.SmartToy, null, modifier = Modifier.size(14.dp), tint = InvestiaPrimary)
                        }
                        Text("Yazıyor...", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        // Input bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.85f))
                .border(width = 1.dp, color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = { Text("Mesajınızı yazın...", color = MaterialTheme.colorScheme.outline) },
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = InvestiaPrimary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedTextColor = MaterialTheme.colorScheme.onBackground,
                        unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                        cursorColor = InvestiaPrimary
                    )
                )
                FilledIconButton(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            viewModel.sendMessage(inputText)
                            inputText = ""
                        }
                    },
                    enabled = inputText.isNotBlank() && !state.isTyping,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = InvestiaPrimary,
                        disabledContainerColor = InvestiaPrimary.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, "Gönder", tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

@Composable
private fun ChatBubble(message: ChatMessage) {
    val isUser = message.role == "user"

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        val shape = RoundedCornerShape(
            topStart = 18.dp, topEnd = 18.dp,
            bottomStart = if (isUser) 18.dp else 4.dp,
            bottomEnd = if (isUser) 4.dp else 18.dp
        )

        if (isUser) {
            Box(
                modifier = Modifier
                    .widthIn(max = 300.dp)
                    .clip(shape)
                    .background(
                        Brush.linearGradient(
                            listOf(InvestiaPrimary, InvestiaSecondary),
                            start = Offset(0f, 0f),
                            end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                        )
                    )
            ) {
                Text(
                    text = message.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    modifier = Modifier.padding(14.dp)
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .widthIn(max = 300.dp)
                    .clip(shape)
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.85f))
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), shape)
            ) {
                Text(
                    text = message.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(14.dp)
                )
            }
        }
    }
}
