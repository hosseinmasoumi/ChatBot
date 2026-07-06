package com.example.data.repository

import android.util.Log
import com.example.BuildConfig
import com.example.data.local.ChatMessageEntity
import com.example.data.remote.Content
import com.example.data.remote.GenerateContentRequest
import com.example.data.remote.GenerationConfig
import com.example.data.remote.Part
import com.example.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GeminiRepository {

    suspend fun generateResponse(
        currentQuery: String,
        history: List<ChatMessageEntity>
    ): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext "خطا: کلید API معتبر یافت نشد. لطفاً کلید API خود را در پنل Secrets در AI Studio تنظیم کنید تا دستیار هوشمند فعال شود."
        }

        // 1. RAG Step: Retrieve relevant context from local FAQ database
        val relevantFaqs = UniversityData.searchFaqs(currentQuery).take(2)
        val contextPrompt = if (relevantFaqs.isNotEmpty()) {
            buildString {
                append("بافتار اطلاعات موثق و رسمی دانشگاه برای پاسخ به سوال دانشجو:\n")
                relevantFaqs.forEach { faq ->
                    append("- سوال متداول: ${faq.question}\n")
                    append("  پاسخ موثق: ${faq.answer}\n\n")
                }
                append("قانون بسیار مهم و حیاتی:\n")
                append("فقط و فقط پاسخ مرتبط با سوال کاربر را نمایش دهید. از تولید مقدمه، خوشامدگویی‌های تکراری، توضیحات اضافی، جملات تعارفی یا محتوای غیرمرتبط با جواب دقیق سوال اکیداً خودداری کنید. پاسخ باید مستقیم، خلاصه و دقیقاً متمرکز بر سوال باشد.\n\n")
            }
        } else {
            ""
        }

        // 2. Build multi-turn content sequence
        val contentsList = mutableListOf<Content>()
        
        // Map history (excluding potential network/error messages)
        history.filter { !it.isError }.takeLast(8).forEach { msg ->
            val roleName = if (msg.sender == "user") "user" else "model"
            contentsList.add(
                Content(
                    parts = listOf(Part(text = msg.text)),
                    role = roleName
                )
            )
        }

        // Add current user query (augmented with RAG context if retrieved)
        val finalUserMessage = if (contextPrompt.isNotEmpty()) {
            "$contextPrompt\nسوال کاربر:\n$currentQuery"
        } else {
            currentQuery
        }

        contentsList.add(
            Content(
                parts = listOf(Part(text = finalUserMessage)),
                role = "user"
            )
        )

        val request = GenerateContentRequest(
            contents = contentsList,
            generationConfig = GenerationConfig(
                temperature = 0.5f
            ),
            systemInstruction = Content(
                parts = listOf(
                    Part(
                        text = "شما چت‌بات رسمی دانشگاه آزاد هستید. وظیفه شما راهنمایی دقیق دانشجو در موضوعاتی مثل ثبت‌نام، انتخاب واحد، امتحانات، قوانین دانشگاه، امور مالی، خوابگاه، کلاس‌ها و ارتباط با اساتید است. به زبان فارسی پاسخ دهید. پاسخ شما باید بسیار دقیق، مستقیم و مرتبط با سوال کاربر باشد. از آوردن مقدمه‌چینی، جملات حاشیه‌ای، تکرار خوشامدگویی، تعارفات طولانی یا هرگونه محتوای نامرتبط به پاسخ سوال کاربر اکیداً خودداری کنید."
                    )
                )
            )
        )

        val modelsToTry = listOf(
            "gemini-3.5-flash",
            "gemini-3.1-flash-lite-preview",
            "gemini-3.1-pro-preview"
        )
        var lastException: Exception? = null
        var finalResponseText: String? = null

        for (modelName in modelsToTry) {
            try {
                Log.d("GeminiRepository", "Attempting call with model: $modelName")
                val response = RetrofitClient.service.generateContent(modelName, apiKey, request)
                val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                if (text != null) {
                    finalResponseText = text
                    Log.d("GeminiRepository", "Success with model: $modelName")
                    break
                }
            } catch (e: Exception) {
                lastException = e
                Log.w("GeminiRepository", "Failed with model: $modelName - ${e.localizedMessage}")
                if (e is retrofit2.HttpException) {
                    try {
                        val errorBody = e.response()?.errorBody()?.string()
                        Log.w("GeminiRepository", "HTTP Error details: $errorBody")
                    } catch (ioe: Exception) {
                        Log.w("GeminiRepository", "Could not read error body", ioe)
                    }
                }
            }
        }

        if (finalResponseText != null) {
            finalResponseText
        } else {
            val errorMsg = lastException?.localizedMessage ?: "Unknown error"
            val isHttp403 = lastException is retrofit2.HttpException && (lastException as retrofit2.HttpException).code() == 403
            if (isHttp403) {
                "متاسفانه دسترسی به سرویس هوش مصنوعی امکان‌پذیر نیست (خطای HTTP 403). لطفاً اطمینان حاصل کنید که کلید API معتبر در بخش Secrets تنظیم شده و دسترسی به مدل‌های گوگل برای کشور یا آی‌پی شما مجاز است."
            } else {
                "متاسفانه در ارتباط با سرور هوش مصنوعی خطایی رخ داد: $errorMsg. لطفا اتصال اینترنت خود را بررسی کرده و مجدد تلاش کنید."
            }
        }
    }
}
