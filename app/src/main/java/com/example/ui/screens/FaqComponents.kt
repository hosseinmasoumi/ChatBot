package com.example.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.SmartToy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FaqItem
import com.example.data.model.UniCategory

@OptIn(ExperimentalMaterial3Api::class)
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
            placeholder = { Text("جستجو در قوانین و پرسش‌های متداول...", style = MaterialTheme.typography.bodyMedium) },
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
            shape = MaterialTheme.shapes.extraLarge,
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Categories Chips Row
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    selected = selectedCategory == null,
                    onClick = { onCategorySelected(null) },
                    label = { Text("همه موضوعات") },
                    leadingIcon = { Icon(Icons.Default.School, null, Modifier.size(18.dp)) },
                    shape = CircleShape
                )
            }

            items(UniCategory.cachedValues, key = { it.id }) { category ->
                val icon = remember(category.iconName) { getCategoryIcon(category.iconName) }
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = { onCategorySelected(category) },
                    label = { Text(category.titlePersian) },
                    leadingIcon = { Icon(icon, null, Modifier.size(18.dp)) },
                    shape = CircleShape,
                    modifier = Modifier.testTag("category_chip_${category.id}"),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = category.parsedColor.copy(alpha = 0.2f),
                        selectedLabelColor = category.parsedColor,
                        selectedLeadingIconColor = category.parsedColor
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // FAQs List
        if (faqList.isEmpty()) {
            EmptyFaqState()
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
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
fun EmptyFaqState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.7f)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.SearchOff,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outline,
            modifier = Modifier.size(80.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "پرسش متداولی یافت نشد!",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "کلمه‌کلیدی متفاوتی را جستجو کنید یا سوالتان را مستقیماً در بخش چت بپرسید.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
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
    val category = remember(faq.categoryId) { UniCategory.getById(faq.categoryId) }

    FrostedCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable { isExpanded = !isExpanded },
        shape = MaterialTheme.shapes.medium
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
                // Accent Indicator
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(28.dp)
                        .clip(CircleShape)
                        .background(category.parsedColor)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = faq.question,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium.copy(
                        textDirection = TextDirection.ContentOrRtl
                    )
                )
                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = faq.answer,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 24.sp,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        textDirection = TextDirection.ContentOrRtl
                    )
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = { onAskAi(faq.question) },
                    colors = ButtonDefaults.filledTonalButtonColors(),
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.SmartToy,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "پرسش از دستیار هوشمند 🤖",
                        style = MaterialTheme.typography.labelLarge
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
