package com.example.ui.screens

import android.app.Application
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.HowToReg
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.R
import com.example.data.local.ChatMessageEntity
import com.example.data.model.FaqItem
import com.example.data.model.UniCategory
import com.example.ui.viewmodel.AssistantViewModel
import com.example.ui.viewmodel.AssistantViewModelFactory
import kotlinx.coroutines.launch
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.RepeatMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.material3.CircularProgressIndicator

enum class MainTab {
    CHAT, FAQ
}

@Composable
fun FrostedGlassBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val color1 = if (isDark) Color(0xFF1E1B4B) else Color(0xFFEEF2FF) // Indigo 50
    val color2 = if (isDark) Color(0xFF311042) else Color(0xFFFAE8FF) // Violet 50
    val baseBg = if (isDark) Color(0xFF0F172A) else Color(0xFFF3F4F9)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(baseBg)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        color1.copy(alpha = 0.5f),
                        color1.copy(alpha = 0.15f),
                        Color.Transparent
                    ),
                    center = Offset(-size.width * 0.1f, -size.height * 0.1f),
                    radius = size.width * 0.85f
                )
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        color2.copy(alpha = 0.4f),
                        color2.copy(alpha = 0.12f),
                        Color.Transparent
                    ),
                    center = Offset(size.width * 1.1f, size.height * 0.95f),
                    radius = size.width * 0.95f
                )
            )
        }
        content()
    }
}

@Composable
fun FrostedCard(
    modifier: Modifier = Modifier,
    borderWidth: androidx.compose.ui.unit.Dp = 1.dp,
    shape: androidx.compose.ui.graphics.Shape = RoundedCornerShape(28.dp),
    content: @Composable () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val containerColor = if (isDark) {
        Color(0xFF1E293B).copy(alpha = 0.45f)
    } else {
        Color.White.copy(alpha = 0.65f)
    }
    val borderColor = if (isDark) {
        Color.White.copy(alpha = 0.12f)
    } else {
        Color.White.copy(alpha = 0.7f)
    }

    Box(
        modifier = modifier
            .clip(shape)
            .background(containerColor)
            .border(borderWidth, borderColor, shape)
    ) {
        content()
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: AssistantViewModel = viewModel(
        factory = AssistantViewModelFactory(LocalContext.current.applicationContext as Application)
    )
) {
    // Force RTL layout since this app is designed in Persian
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        FrostedGlassBackground(modifier = modifier) {
            var currentTab by remember { mutableStateOf(MainTab.CHAT) }
            val chatMessages by viewModel.chatMessages.collectAsState()
            val isTyping by viewModel.isTyping.collectAsState()
            val searchQuery by viewModel.searchQuery.collectAsState()
            val selectedCategory by viewModel.selectedCategory.collectAsState()
            val faqList by viewModel.faqList.collectAsState()

            val listState = rememberLazyListState()
            val coroutineScope = rememberCoroutineScope()

            // Auto-scroll to the bottom of the chat list on new messages or typing state change
            LaunchedEffect(chatMessages.size, isTyping, currentTab) {
                if (chatMessages.isNotEmpty() && currentTab == MainTab.CHAT) {
                    val targetIndex = if (isTyping) chatMessages.size else chatMessages.size - 1
                    listState.animateScrollToItem(targetIndex)
                }
            }

            val isDark = isSystemInDarkTheme()

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    CenterAlignedTopAppBar(
                        title = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                UniLogo(
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "دستیار هوشمند دانشجو",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 20.sp
                                )
                            }
                        },
                        actions = {
                            if (currentTab == MainTab.CHAT && chatMessages.size > 1) {
                                IconButton(
                                    onClick = { viewModel.clearChatHistory() },
                                    modifier = Modifier.testTag("clear_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "پاک کردن گفتگوها",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = Color.Transparent
                        )
                    )
                },
                bottomBar = {
                    NavigationBar(
                        containerColor = if (isDark) Color(0xFF1E293B).copy(alpha = 0.6f) else Color.White.copy(alpha = 0.6f),
                        tonalElevation = 0.dp
                    ) {
                        NavigationBarItem(
                            selected = currentTab == MainTab.CHAT,
                            onClick = { currentTab = MainTab.CHAT },
                            label = { Text("دستیار هوشمند", fontWeight = FontWeight.Bold) },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.ChatBubble,
                                    contentDescription = "دستیار هوشمند"
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                            modifier = Modifier.testTag("tab_chat")
                        )
                        NavigationBarItem(
                            selected = currentTab == MainTab.FAQ,
                            onClick = { currentTab = MainTab.FAQ },
                            label = { Text("راهنمای جامع", fontWeight = FontWeight.Bold) },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Book,
                                    contentDescription = "راهنمای جامع"
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                            modifier = Modifier.testTag("tab_faq")
                        )
                    }
                },
                containerColor = Color.Transparent
            ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // API Key Warning Banner if missing
                if (!viewModel.isApiKeyConfigured) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "هشدار",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "پیکربندی هوش مصنوعی ناقص است",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.error,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "کلید API یافت نشد. لطفاً کلید خود را در پنل Secrets در AI Studio با نام GEMINI_API_KEY تنظیم کنید تا پاسخ‌های زنده فعال شوند.",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    }
                }

                Crossfade(
                    targetState = currentTab,
                    modifier = Modifier.weight(1f),
                    animationSpec = tween(durationMillis = 250)
                ) { tab ->
                    when (tab) {
                        MainTab.CHAT -> {
                            ChatTabContent(
                                chatMessages = chatMessages,
                                isTyping = isTyping,
                                listState = listState,
                                onSendMessage = { text -> viewModel.sendMessage(text) },
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        MainTab.FAQ -> {
                            FaqTabContent(
                                faqList = faqList,
                                searchQuery = searchQuery,
                                onSearchQueryChanged = { viewModel.updateSearchQuery(it) },
                                selectedCategory = selectedCategory,
                                onCategorySelected = { viewModel.selectCategory(it) },
                                onAskAi = { question ->
                                    viewModel.sendMessage(question)
                                    currentTab = MainTab.CHAT
                                },
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        }
    }
}
}


@Composable
fun LoadingDotsIndicator(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "dots")
    val dotScales = (0..2).map { index ->
        infiniteTransition.animateFloat(
            initialValue = 0.3f,
            targetValue = 1.0f,
            animationSpec = infiniteRepeatable(
                animation = keyframes {
                    durationMillis = 600
                    0.3f at index * 100
                    1.0f at index * 100 + 150
                    0.3f at index * 100 + 300
                },
                repeatMode = RepeatMode.Reverse
            ),
            label = "dot_scale_$index"
        )
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.padding(horizontal = 4.dp)
    ) {
        dotScales.forEach { scale ->
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .graphicsLayer {
                        scaleX = scale.value
                        scaleY = scale.value
                    }
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
            )
        }
    }
}

@Composable
fun ChatTabContent(
    chatMessages: List<ChatMessageEntity>,
    isTyping: Boolean,
    listState: androidx.compose.foundation.lazy.LazyListState,
    onSendMessage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var textInput by remember { mutableStateOf("") }
    val isDark = isSystemInDarkTheme()

    Column(modifier = modifier.fillMaxSize()) {
        // Main Chat Insight (Frosted Glass Card)
        FrostedCard(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            shape = RoundedCornerShape(32.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Frosted Status Header with a pulsing emerald green dot
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(if (isDark) Color.White.copy(alpha = 0.03f) else Color.Black.copy(alpha = 0.02f))
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF10B981)) // Emerald 500
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "دستیار هوشمند دانشجو فعال است (موتور هوش مصنوعی RAG)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color.White.copy(alpha = 0.6f) else Color(0xFF64748B)
                    )
                }

                // Chat Bubbles List
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
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            UniLogo(modifier = Modifier.size(96.dp))
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "دستیار هوشمند Uni AI",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "همراه و راهنمای دیجیتال شما در طول دوران تحصیل",
                                fontSize = 13.sp,
                                color = if (isDark) Color.White.copy(alpha = 0.6f) else Color(0xFF64748B),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    items(chatMessages, key = { it.id }) { message ->
                        val shouldAnimate = message.id == lastMessageId
                        ChatBubble(message = message, shouldAnimate = shouldAnimate)
                    }

                    // Typing Indicator
                    if (isTyping) {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(end = 64.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(16.dp, 16.dp, 16.dp, 0.dp))
                                        .background(if (isDark) Color(0xFF1E293B).copy(alpha = 0.6f) else Color.White.copy(alpha = 0.8f))
                                        .border(
                                            width = 1.dp,
                                            color = if (isDark) Color.White.copy(alpha = 0.1f) else Color(0xFFEEF2F6),
                                            shape = RoundedCornerShape(16.dp, 16.dp, 16.dp, 0.dp)
                                        )
                                        .padding(horizontal = 16.dp, vertical = 12.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(16.dp),
                                            strokeWidth = 2.dp,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "دستیار در حال پاسخگویی...",
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        LoadingDotsIndicator()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Input Field Area
        val infiniteTransition = rememberInfiniteTransition(label = "send_pulse")
        val rawPulse by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(800, easing = androidx.compose.animation.core.EaseInOutSine),
                repeatMode = RepeatMode.Reverse
            ),
            label = "scale"
        )

        val isSendActive = textInput.isNotBlank() && !isTyping
        val activeProgress by androidx.compose.animation.core.animateFloatAsState(
            targetValue = if (isSendActive) 1f else 0f,
            animationSpec = tween(300),
            label = "active_progress"
        )

        val sendScale = 1f + (rawPulse * 0.12f * activeProgress)

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
                    onValueChange = { textInput = it },
                    placeholder = {
                        Text(
                            text = "سوال خود را بپرسید...",
                            fontSize = 14.sp
                        )
                    },
                    supportingText = {
                        if (textInput.isNotEmpty()) {
                            Text(
                                text = "${textInput.length} کاراکتر",
                                fontSize = 11.sp,
                                color = if (isDark) Color.White.copy(alpha = 0.5f) else Color(0xFF64748B),
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.End
                            )
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_input"),
                    shape = RoundedCornerShape(28.dp),
                    maxLines = 4,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Send
                    ),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            if (textInput.isNotBlank()) {
                                onSendMessage(textInput)
                                textInput = ""
                            }
                        }
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = if (isDark) Color(0xFF1E293B).copy(alpha = 0.7f) else Color.White.copy(alpha = 0.75f),
                        unfocusedContainerColor = if (isDark) Color(0xFF1E293B).copy(alpha = 0.5f) else Color.White.copy(alpha = 0.6f),
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = if (isDark) Color.White.copy(alpha = 0.1f) else Color.White.copy(alpha = 0.5f)
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (textInput.isNotBlank()) {
                            onSendMessage(textInput)
                            textInput = ""
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .graphicsLayer {
                            scaleX = sendScale
                            scaleY = sendScale
                        }
                        .background(MaterialTheme.colorScheme.primary, CircleShape)
                        .testTag("send_button"),
                    enabled = textInput.isNotBlank() && !isTyping
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.Send,
                        contentDescription = "ارسال",
                        tint = if (textInput.isNotBlank() && !isTyping) Color.White else Color.White.copy(alpha = 0.5f),
                        modifier = Modifier.size(20.dp)
                    )
                }
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
                // User Avatar with distinct gradient base
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF6366F1), // Indigo 500
                                    Color(0xFF4F46E5)  // Indigo 600
                                )
                            )
                        )
                        .testTag("user_avatar_${message.id}"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.People,
                        contentDescription = "کاربر",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            }

            Column(
                modifier = Modifier.widthIn(max = 280.dp),
                horizontalAlignment = if (isUser) Alignment.Start else Alignment.End
            ) {
                val bubbleShape = if (isUser) {
                    RoundedCornerShape(18.dp, 18.dp, 18.dp, 0.dp)
                } else {
                    RoundedCornerShape(18.dp, 18.dp, 0.dp, 18.dp)
                }

                val containerColor = if (isUser) {
                    MaterialTheme.colorScheme.primary
                } else if (message.isError) {
                    MaterialTheme.colorScheme.errorContainer
                } else {
                    if (isDark) Color(0xFF1E293B).copy(alpha = 0.8f) else Color.White.copy(alpha = 0.85f)
                }

                val textColor = if (isUser) {
                    MaterialTheme.colorScheme.onPrimary
                } else if (message.isError) {
                    MaterialTheme.colorScheme.onErrorContainer
                } else {
                    if (isDark) Color(0xFFE2E8F0) else Color(0xFF1E293B)
                }

                val borderModifier = if (isUser) {
                    Modifier
                } else {
                    Modifier.border(
                        width = 1.dp,
                        color = if (isDark) Color.White.copy(alpha = 0.12f) else Color(0xFFEEF2F6),
                        shape = bubbleShape
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(bubbleShape)
                        .background(containerColor)
                        .then(borderModifier)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = message.text,
                        color = textColor,
                        fontSize = 14.sp,
                        lineHeight = 22.sp,
                        style = MaterialTheme.typography.bodyMedium.copy(
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
                    fontSize = 10.sp,
                    color = if (isDark) Color.White.copy(alpha = 0.4f) else Color.Black.copy(alpha = 0.35f),
                    modifier = Modifier.padding(
                        top = 4.dp,
                        start = if (isUser) 4.dp else 0.dp,
                        end = if (isUser) 0.dp else 4.dp
                    )
                )
            }

            if (!isUser) {
                Spacer(modifier = Modifier.width(8.dp))
                // Assistant Avatar with styled gradient base
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.secondary
                                )
                            )
                        )
                        .testTag("assistant_avatar_${message.id}"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SmartToy,
                        contentDescription = "دستیار هوشمند",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }

    if (shouldAnimate) {
        var visible by remember { mutableStateOf(false) }
        LaunchedEffect(message.id) {
            visible = true
        }

        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(animationSpec = tween(300)) + slideInVertically(
                initialOffsetY = { if (isUser) it / 3 else -it / 3 },
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
fun FaqTabContent(
    faqList: List<FaqItem>,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    selectedCategory: UniCategory?,
    onCategorySelected: (UniCategory?) -> Unit,
    onAskAi: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(top = 8.dp)
    ) {
        val isDark = isSystemInDarkTheme()
        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChanged,
            placeholder = { Text("جستجو در قوانین و پرسش‌های متداول...", fontSize = 14.sp) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchQueryChanged("") }) {
                        Icon(imageVector = Icons.Default.Clear, contentDescription = "پاک کردن")
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .testTag("faq_search_input"),
            shape = RoundedCornerShape(28.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = if (isDark) Color(0xFF1E293B).copy(alpha = 0.7f) else Color.White.copy(alpha = 0.75f),
                unfocusedContainerColor = if (isDark) Color(0xFF1E293B).copy(alpha = 0.5f) else Color.White.copy(alpha = 0.6f),
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = if (isDark) Color.White.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.6f)
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Categories Chips Row
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                val onAllClick = remember { { onCategorySelected(null) } }
                CategoryChip(
                    categoryName = "همه موضوعات",
                    isSelected = selectedCategory == null,
                    onClick = onAllClick,
                    icon = Icons.Default.School,
                    tintColor = MaterialTheme.colorScheme.primary
                )
            }

            items(UniCategory.cachedValues, key = { it.id }) { category ->
                val icon = remember(category.iconName) { getCategoryIcon(category.iconName) }
                val isSelected = selectedCategory == category
                val onChipClick = remember(category) { { onCategorySelected(category) } }
                CategoryChip(
                    categoryName = category.titlePersian,
                    isSelected = isSelected,
                    onClick = onChipClick,
                    icon = icon,
                    tintColor = category.parsedColor,
                    modifier = Modifier.testTag("category_chip_${category.id}")
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // FAQs List
        if (faqList.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Help,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "پرسش متداولی یافت نشد!",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "کلمه‌کلیدی متفاوتی را جستجو کنید یا سوالتان را مستقیماً در بخش چت بپرسید.",
                    color = MaterialTheme.colorScheme.outline,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(faqList, key = { it.id }) { faq ->
                    FaqAccordionItem(
                        faq = faq,
                        onAskAi = onAskAi,
                        modifier = Modifier.testTag("faq_item_${faq.id}")
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryChip(
    categoryName: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    icon: ImageVector,
    tintColor: Color,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val backgroundColor = if (isSelected) tintColor else {
        if (isDark) Color(0xFF1E293B).copy(alpha = 0.5f) else Color.White.copy(alpha = 0.55f)
    }
    val textColor = if (isSelected) Color.White else {
        if (isDark) Color.White.copy(alpha = 0.8f) else Color(0xFF1E293B)
    }
    val iconColor = if (isSelected) Color.White else tintColor
    
    val borderModifier = if (isSelected) {
        Modifier
    } else {
        Modifier.border(
            width = 1.dp,
            color = if (isDark) Color.White.copy(alpha = 0.12f) else Color.White.copy(alpha = 0.7f),
            shape = RoundedCornerShape(20.dp)
        )
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundColor)
            .then(borderModifier)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = categoryName,
            color = textColor,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
fun FaqAccordionItem(
    faq: FaqItem,
    onAskAi: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    val category = remember(faq.categoryId) {
        UniCategory.getById(faq.categoryId)
    }
    val categoryColor = category.parsedColor

    FrostedCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded },
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .animateContentSize()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left border colored accent
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(24.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(categoryColor)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = faq.question,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium.copy(
                        textDirection = TextDirection.ContentOrRtl
                    )
                )
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (isExpanded) "بستن" else "باز کردن",
                    tint = MaterialTheme.colorScheme.outline
                )
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = faq.answer,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 22.sp,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        textDirection = TextDirection.ContentOrRtl
                    )
                )
                Spacer(modifier = Modifier.height(16.dp))
                val onAskClick = remember(faq.question) { { onAskAi(faq.question) } }
                Button(
                    onClick = onAskClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Icon(
                        imageVector = Icons.Default.SmartToy,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "پرسش از دستیار هوشمند 🤖",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

private fun getCategoryIcon(iconName: String): ImageVector {
    return when (iconName) {
        "AppRegistration" -> Icons.Default.HowToReg
        "MenuBook" -> Icons.Default.MenuBook
        "Assignment" -> Icons.Default.Assignment
        "Gavel" -> Icons.Default.Gavel
        "Payments" -> Icons.Default.Payments
        "Home" -> Icons.Default.Home
        "School" -> Icons.Default.School
        "People" -> Icons.Default.People
        else -> Icons.Default.Help
    }
}

@Composable
fun UniLogo(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "logo_pulse")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = androidx.compose.animation.core.tween(2000, easing = androidx.compose.animation.core.EaseInOutSine),
            repeatMode = androidx.compose.animation.core.RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val rotateAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = androidx.compose.animation.core.tween(8000, easing = androidx.compose.animation.core.LinearEasing),
            repeatMode = androidx.compose.animation.core.RepeatMode.Restart
        ),
        label = "rotate"
    )

    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val tertiaryColor = MaterialTheme.colorScheme.tertiary

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = pulse
                scaleY = pulse
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val centerOffset = Offset(size.width / 2f, size.height / 2f)
            val radius = size.minDimension / 2.2f

            // Outer glowing dash-orbit
            drawCircle(
                brush = Brush.sweepGradient(
                    colors = listOf(primaryColor, secondaryColor, tertiaryColor, primaryColor),
                    center = centerOffset
                ),
                radius = radius,
                style = androidx.compose.ui.graphics.drawscope.Stroke(
                    width = 2.dp.toPx(),
                    pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(
                        floatArrayOf(20f, 15f),
                        phase = rotateAngle * (3.14159f / 180f) * radius
                    )
                ),
                alpha = 0.85f
            )

            // Inner solid glow circle
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(primaryColor.copy(alpha = 0.25f), Color.Transparent),
                    center = centerOffset,
                    radius = radius
                )
            )
        }

        // Standard Material White School Icon on Gradient Circle background
        Box(
            modifier = Modifier
                .fillMaxSize(0.6f)
                .clip(CircleShape)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(primaryColor, secondaryColor)
                    )
                )
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.School,
                contentDescription = "Uni AI Logo",
                tint = Color.White,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
