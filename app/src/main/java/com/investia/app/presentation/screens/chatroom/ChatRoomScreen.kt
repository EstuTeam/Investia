package com.investia.app.presentation.screens.chatroom

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.investia.app.domain.model.ChatRoom
import com.investia.app.domain.model.RoomMessage
import com.investia.app.presentation.components.*
import com.investia.app.presentation.navigation.Screen
import com.investia.app.presentation.theme.*

@Composable
fun ChatRoomListScreen(
    navController: NavController,
    viewModel: ChatRoomViewModel = hiltViewModel()
) {
    val state by viewModel.listState.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Geri", tint = MaterialTheme.colorScheme.onBackground)
                }
                Text(
                    "Sohbet Odaları",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { viewModel.loadRooms() }) {
                    Icon(Icons.Filled.Refresh, "Yenile", tint = InvestiaPrimary)
                }
            }
        }

        if (state.isLoading) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) {
                    InvestiaLoadingSpinner()
                }
            }
        }

        state.error?.let { error ->
            item {
                GlassCard(modifier = Modifier.padding(16.dp)) {
                    Text(error, color = LossRed, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        if (!state.isLoading && state.rooms.isEmpty()) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(48.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Outlined.Forum, null, modifier = Modifier.size(56.dp), tint = MaterialTheme.colorScheme.outline)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Henüz sohbet odası yok", style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Text("Yakında aktif olacak", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        items(state.rooms, key = { it.id }) { room ->
            RoomCard(room) {
                navController.navigate(Screen.ChatRoomDetail.createRoute(room.id))
            }
        }
    }
}

@Composable
private fun RoomCard(room: ChatRoom, onClick: () -> Unit) {
    GlassCard(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
        onClick = onClick
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(InvestiaPrimary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Forum, null, tint = InvestiaPrimary, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(room.name, style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                if (room.description.isNotBlank()) {
                    Text(room.description, style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
                }
                if (room.lastMessage.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(room.lastMessage, style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline, maxLines = 1)
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.People, null, tint = MaterialTheme.colorScheme.outline, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("${room.memberCount}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                }
                if (room.lastMessageTime.isNotBlank()) {
                    Text(room.lastMessageTime, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                }
            }
        }
    }
}

@Composable
fun ChatRoomDetailScreen(
    roomId: String,
    navController: NavController,
    viewModel: ChatRoomViewModel = hiltViewModel()
) {
    val state by viewModel.detailState.collectAsState()
    var messageText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(roomId) {
        viewModel.loadMessages(roomId)
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
                Icon(Icons.AutoMirrored.Filled.ArrowBack, "Geri", tint = MaterialTheme.colorScheme.onBackground)
            }
            Text(
                state.roomName.ifBlank { "Sohbet" },
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = { viewModel.refreshMessages() }) {
                Icon(Icons.Filled.Refresh, "Yenile", tint = InvestiaPrimary)
            }
        }

        Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), thickness = 1.dp)

        // Messages
        LazyColumn(
            modifier = Modifier.weight(1f).padding(horizontal = 12.dp),
            state = listState,
            reverseLayout = false,
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            if (state.isLoading) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                        InvestiaLoadingSpinner(size = 24.dp, strokeWidth = 3.dp)
                    }
                }
            }

            items(state.messages, key = { it.id }) { message ->
                MessageBubble(message)
            }
        }

        // Input
        Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), thickness = 1.dp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .navigationBarsPadding(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = messageText,
                onValueChange = { messageText = it },
                placeholder = { Text("Mesaj yazın...", color = MaterialTheme.colorScheme.outline) },
                modifier = Modifier.weight(1f),
                maxLines = 3,
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = InvestiaPrimary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                    cursorColor = InvestiaPrimary
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = {
                    if (messageText.isNotBlank()) {
                        viewModel.sendMessage(messageText)
                        messageText = ""
                    }
                },
                enabled = !state.isSending && messageText.isNotBlank()
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Send, "Gönder",
                    tint = if (messageText.isNotBlank()) InvestiaPrimary else MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

@Composable
private fun MessageBubble(message: RoomMessage) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            message.userName,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = InvestiaPrimaryLight
        )
        Spacer(modifier = Modifier.height(2.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp, 12.dp, 12.dp, 4.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(
                message.content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        if (message.timestamp.isNotBlank()) {
            Text(
                message.timestamp,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline,
                modifier = Modifier.padding(start = 4.dp, top = 2.dp)
            )
        }
    }
}
