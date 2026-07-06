package com.example.ui.screens

import android.app.Application
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.viewmodel.AssistantViewModel
import com.example.ui.viewmodel.AssistantViewModelFactory

enum class MainTab {
    CHAT, FAQ
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: AssistantViewModel = viewModel(
        factory = AssistantViewModelFactory(LocalContext.current.applicationContext as Application)
    )
) {
    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        FrostedGlassBackground(modifier = modifier) {
            var currentTab by remember { mutableStateOf(MainTab.CHAT) }
            val chatMessages by viewModel.chatMessages.collectAsState()
            val isTyping by viewModel.isTyping.collectAsState()
            val searchQuery by viewModel.searchQuery.collectAsState()
            val selectedCategory by viewModel.selectedCategory.collectAsState()
            val faqList by viewModel.faqList.collectAsState()

            val listState = rememberLazyListState()

            LaunchedEffect(chatMessages.size, isTyping, currentTab) {
                if (chatMessages.isNotEmpty() && currentTab == MainTab.CHAT) {
                    val targetIndex = if (isTyping) chatMessages.size else chatMessages.size - 1
                    listState.animateScrollToItem(targetIndex)
                }
            }

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                topBar = {
                    CenterAlignedTopAppBar(
                        title = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                UniLogo(modifier = Modifier.size(32.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "دستیار هوشمند دانشجو",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        },
                        actions = {
                            if (currentTab == MainTab.CHAT && chatMessages.size > 1) {
                                IconButton(onClick = { viewModel.clearChatHistory() }) {
                                    Icon(
                                        imageVector = Icons.Default.DeleteSweep,
                                        contentDescription = "پاک کردن",
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
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
                        tonalElevation = 0.dp
                    ) {
                        NavigationBarItem(
                            selected = currentTab == MainTab.CHAT,
                            onClick = { currentTab = MainTab.CHAT },
                            label = { Text("گفتگو", style = MaterialTheme.typography.labelMedium) },
                            icon = { Icon(Icons.Default.Forum, null) }
                        )
                        NavigationBarItem(
                            selected = currentTab == MainTab.FAQ,
                            onClick = { currentTab = MainTab.FAQ },
                            label = { Text("راهنما", style = MaterialTheme.typography.labelMedium) },
                            icon = { Icon(Icons.Default.MenuBook, null) }
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
                    ApiKeyWarning(isConfigured = viewModel.isApiKeyConfigured)

                    Crossfade(
                        targetState = currentTab,
                        modifier = Modifier.weight(1f),
                        animationSpec = tween(300)
                    ) { tab ->
                        // Constrain content width for responsiveness on large screens
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .widthIn(max = 800.dp)
                                .align(Alignment.CenterHorizontally)
                        ) {
                            when (tab) {
                                MainTab.CHAT -> ChatTabContent(
                                    chatMessages = chatMessages,
                                    isTyping = isTyping,
                                    listState = listState,
                                    onSendMessage = { viewModel.sendMessage(it) }
                                )
                                MainTab.FAQ -> FaqTabContent(
                                    faqList = faqList,
                                    searchQuery = searchQuery,
                                    onSearchQueryChanged = { viewModel.updateSearchQuery(it) },
                                    selectedCategory = selectedCategory,
                                    onCategorySelected = { viewModel.selectCategory(it) },
                                    onAskAi = { 
                                        viewModel.sendMessage(it)
                                        currentTab = MainTab.CHAT
                                    }
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
fun ApiKeyWarning(isConfigured: Boolean) {
    if (!isConfigured) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
            shape = MaterialTheme.shapes.medium
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Warning, null, tint = MaterialTheme.colorScheme.error)
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "کلید API یافت نشد. پاسخ‌های زنده غیرفعال هستند.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    }
}
