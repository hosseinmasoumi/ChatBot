package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.data.local.AppDatabase
import com.example.data.local.ChatMessageEntity
import com.example.data.model.FaqItem
import com.example.data.model.UniCategory
import com.example.data.repository.GeminiRepository
import com.example.data.repository.UniversityData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AssistantViewModel(application: Application) : AndroidViewModel(application) {

    private val chatDao = AppDatabase.getDatabase(application).chatDao()
    private val geminiRepository = GeminiRepository()

    // Observe Chat History from Room
    val chatMessages: StateFlow<List<ChatMessageEntity>> = chatDao.getAllMessages()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // State for AI Typing Status
    private val _isTyping = MutableStateFlow(false)
    val isTyping: StateFlow<Boolean> = _isTyping.asStateFlow()

    // State for FAQ Search & Filters
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow<UniCategory?>(null)
    val selectedCategory: StateFlow<UniCategory?> = _selectedCategory.asStateFlow()

    // Reactive FAQ list combining Search Query and Category Filter
    val faqList: StateFlow<List<FaqItem>> = combine(_searchQuery, _selectedCategory) { query, category ->
        val baseList = if (query.isBlank()) {
            UniversityData.faqs
        } else {
            UniversityData.searchFaqs(query)
        }

        if (category != null) {
            baseList.filter { it.categoryId == category.id }
        } else {
            baseList
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UniversityData.faqs
    )

    // Check if API key is properly configured
    val isApiKeyConfigured: Boolean
        get() {
            val key = BuildConfig.GEMINI_API_KEY
            return key.isNotBlank() && key != "MY_GEMINI_API_KEY"
        }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectCategory(category: UniCategory?) {
        _selectedCategory.value = category
    }

    fun sendMessage(text: String) {
        if (text.isBlank() || _isTyping.value) return

        viewModelScope.launch {
            // 1. Save user message to database
            val userMsg = ChatMessageEntity(
                sender = "user",
                text = text.trim()
            )
            chatDao.insertMessage(userMsg)

            // 2. Set loading status
            _isTyping.value = true

            // 3. Retrieve current messages for history context
            val currentHistory = chatMessages.value

            // 4. Call Gemini Repository to get RAG-augmented response
            val responseText = geminiRepository.generateResponse(text, currentHistory)

            // 5. Save assistant response to database
            val assistantMsg = ChatMessageEntity(
                sender = "assistant",
                text = responseText,
                isError = responseText.startsWith("خطا") || responseText.startsWith("متاسفانه در ارتباط")
            )
            chatDao.insertMessage(assistantMsg)

            // 6. Disable typing status
            _isTyping.value = false
        }
    }

    fun clearChatHistory() {
        viewModelScope.launch {
            chatDao.clearHistory()
            val welcomeMsg = ChatMessageEntity(
                sender = "assistant",
                text = "سلام! من دستیار هوشمند Uni AI شما هستم. 🎓✨\n\nشما می‌توانید هر سوالی در مورد امور دانشگاهی خود دارید از من بپرسید. چند نمونه سوال متداول:\n" +
                        "● «مراحل انتخاب واحد و زمان‌بندی آن چگونه است؟»\n" +
                        "● «قوانین غیبت مجاز در کلاس‌ها چیست؟»\n" +
                        "● «چگونه وام دانشجویی و تسهیلات مالی دریافت کنم؟»\n" +
                        "● «نحوه ثبت‌نام خوابگاه و رزرو غذا در سلف چیست؟»\n\n" +
                        "چطور می‌توانم کمکتان کنم؟ 😊"
            )
            chatDao.insertMessage(welcomeMsg)
        }
    }

    init {
        // Seed an initial welcome message if history is empty
        viewModelScope.launch {
            chatDao.getAllMessages().collect { list ->
                if (list.isEmpty()) {
                    val welcomeMsg = ChatMessageEntity(
                        sender = "assistant",
                        text = "سلام! به دانشگاه خوش آمدید. 🎓✨\n" +
                                "من دستیار هوشمند شما (Uni AI Assistant) برای پاسخ به تمام سوالات آموزشی و دانشجویی شما هستم.\n\n" +
                                "شما می‌توانید سوالاتی در زمینه‌های زیر از من بپرسید:\n" +
                                "● ثبت‌نام و پذیرش: «شرایط ثبت‌نام نهایی نودانشجویان چیست؟»\n" +
                                "● انتخاب واحد: «چطور می‌توانم در سیستم گلستان انتخاب واحد کنم؟»\n" +
                                "● امتحانات: «در صورت غیبت در امتحان پایانی چه اتفاقی می‌افتد؟»\n" +
                                "● امور مالی: «شرایط اقساطی کردن شهریه یا دریافت وام دانشجویی چیست؟»\n" +
                                "● خوابگاه و غذا: «چگونه می‌توانم خوابگاه و ژتون غذای سلف را رزرو کنم؟»\n\n" +
                                "چه سوالی در ذهن دارید؟ با من در میان بگذارید! 😊👇"
                    )
                    chatDao.insertMessage(welcomeMsg)
                }
            }
        }
    }
}

class AssistantViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AssistantViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AssistantViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
