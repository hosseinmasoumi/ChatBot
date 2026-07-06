package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ChatMessageEntity

@Composable
fun ChatTabContent(
    chatMessages: List<ChatMessageEntity>,
    isTyping: Boolean,
    listState: LazyListState,
    onSendMessage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var textInput by remember { mutableStateOf("") }
    val isDark = isSystemInDarkTheme()

    Column(modifier = modifier.fillMaxSize()) {
        // Main Chat Insight
        FrostedCard(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = MaterialTheme.shapes.extraLarge
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Status Header
                Surface(
                    color = if (isDark) Color.White.copy(alpha = 0.03f) else Color.Black.copy(alpha = 0.02f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF10B981))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "دستیار هوشمند فعال است",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                val lastMessageId = remember(chatMessages) { chatMessages.lastOrNull()?.id }

                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 16.dp)
                ) {
                    item {
                        WelcomeHeader()
                    }

                    items(chatMessages, key = { it.id }) { message ->
                        val shouldAnimate = message.id == lastMessageId
                        ChatBubble(message = message, shouldAnimate = shouldAnimate)
                    }

                    if (isTyping) {
                        item {
                            TypingIndicator()
                        }
                    }
                }
            }
        }

        ChatInputArea(
            textInput = textInput,
            onTextInputChange = { textInput = it },
            isTyping = isTyping,
            onSendMessage = {
                onSendMessage(textInput)
                textInput = ""
            }
        )
    }
}

@Composable
fun WelcomeHeader() {
    val isDark = isSystemInDarkTheme()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        UniLogo(modifier = Modifier.size(100.dp))
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "دستیار هوشمند Uni AI",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "همراه و راهنمای دیجیتال شما در طول دوران تحصیل",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
    }
}

@Composable
fun TypingIndicator() {
    val isDark = isSystemInDarkTheme()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(end = 64.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp, 16.dp, 16.dp, 4.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(16.dp, 16.dp, 16.dp, 4.dp)
                )
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Rounded.AutoAwesome,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "دستیار در حال نوشتن...",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(6.dp))
                LoadingDotsIndicator()
            }
        }
    }
}

@Composable
fun ChatInputArea(
    textInput: String,
    onTextInputChange: (String) -> Unit,
    isTyping: Boolean,
    onSendMessage: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val isSendActive = textInput.isNotBlank() && !isTyping

    Surface(
        color = Color.Transparent,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = textInput,
                onValueChange = onTextInputChange,
                placeholder = {
                    Text(
                        text = "سوال خود را بپرسید...",
                        style = MaterialTheme.typography.bodyMedium
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .testTag("chat_input"),
                shape = MaterialTheme.shapes.extraLarge,
                maxLines = 4,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(
                    onSend = { if (isSendActive) onSendMessage() }
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            FilledIconButton(
                onClick = onSendMessage,
                modifier = Modifier
                    .size(52.dp)
                    .graphicsLayer {
                        if (isSendActive) {
                            scaleX = pulse
                            scaleY = pulse
                        }
                    },
                enabled = isSendActive,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Default.Send,
                    contentDescription = "ارسال",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessageEntity, shouldAnimate: Boolean = false) {
    val isUser = message.sender == "user"
    val isDark = isSystemInDarkTheme()

    @Composable
    fun BubbleContent() {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .testTag("chat_message_bubble_${message.id}"),
            horizontalArrangement = if (isUser) Arrangement.Start else Arrangement.End,
            verticalAlignment = Alignment.Bottom
        ) {
            if (isUser) {
                UserAvatar()
                Spacer(modifier = Modifier.width(8.dp))
            }

            Column(
                modifier = Modifier.widthIn(max = 300.dp),
                horizontalAlignment = if (isUser) Alignment.Start else Alignment.End
            ) {
                val bubbleShape = if (isUser) {
                    RoundedCornerShape(20.dp, 20.dp, 20.dp, 4.dp)
                } else {
                    RoundedCornerShape(20.dp, 20.dp, 4.dp, 20.dp)
                }

                val containerColor = if (isUser) {
                    MaterialTheme.colorScheme.primary
                } else if (message.isError) {
                    MaterialTheme.colorScheme.errorContainer
                } else {
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f)
                }

                val textColor = if (isUser) {
                    MaterialTheme.colorScheme.onPrimary
                } else if (message.isError) {
                    MaterialTheme.colorScheme.onErrorContainer
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                }

                Box(
                    modifier = Modifier
                        .clip(bubbleShape)
                        .background(containerColor)
                        .border(
                            width = 1.dp,
                            color = if (isUser) Color.Transparent else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                            shape = bubbleShape
                        )
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = message.text,
                        color = textColor,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            lineHeight = 24.sp,
                            textDirection = TextDirection.ContentOrRtl
                        )
                    )
                }

                val timeString = remember(message.timestamp) {
                    val sdf = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
                    sdf.format(java.util.Date(message.timestamp))
                }

                Text(
                    text = if (isUser) "شما • $timeString" else "دستیار • $timeString",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.padding(top = 4.dp, start = 4.dp, end = 4.dp)
                )
            }

            if (!isUser) {
                Spacer(modifier = Modifier.width(8.dp))
                AssistantAvatar()
            }
        }
    }

    if (shouldAnimate) {
        var visible by remember { mutableStateOf(false) }
        LaunchedEffect(message.id) { visible = true }

        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(300)) + slideInVertically(
                initialOffsetY = { if (isUser) it / 4 else -it / 4 },
                animationSpec = tween(300)
            )
        ) {
            BubbleContent()
        }
    } else {
        BubbleContent()
    }
}

@Composable
fun UserAvatar() {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFF6366F1), Color(0xFF4F46E5))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.People,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
fun AssistantAvatar() {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.SmartToy,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(18.dp)
        )
    }
}
