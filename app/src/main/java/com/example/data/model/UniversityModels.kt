package com.example.data.model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class FaqItem(
    val id: String,
    val categoryId: String,
    val question: String,
    val answer: String,
    val keywords: List<String>
)

enum class UniCategory(
    val id: String,
    val titlePersian: String,
    val description: String,
    val iconName: String,
    val primaryColorHex: String
) {
    REGISTRATION("registration", "ثبت‌نام", "مدارک، مراحل پذیرش حضوری و تاییدیه تحصیلی", "AppRegistration", "#1E88E5"),
    COURSE_SELECTION("course_selection", "انتخاب واحد", "قوانین پیش‌نیازها، حذف و اضافه و مشروطی", "MenuBook", "#43A047"),
    EXAMS("exams", "امتحانات", "نمرات قبولی، کارت ورود به جلسه و قوانین غیبت", "Assignment", "#E53935"),
    RULES("rules", "قوانین دانشگاه", "حضور و غیاب ۳ شانزدهم، مرخصی و سنوات مجاز", "Gavel", "#8E24AA"),
    FINANCIAL("financial", "امور مالی", "شهریه ثابت و متغیر، نحوه دریافت وام و تخفیف‌ها", "Payments", "#FFB300"),
    DORMITORY("dormitory", "خوابگاه", "شرایط اسکان، مدارک لازم و قوانین رفت‌وآمد", "Home", "#00ACC1"),
    CLASSES("classes", "کلاس‌ها", "آدرس ساختمان‌ها، ساعات کلاس‌ها و آموزش مجازی", "School", "#3949AB"),
    PROFESSORS("professors", "ارتباط با اساتید", "ساعت حضور، ایمیل رسمی و فرآیند تجدیدنظر نمره", "People", "#D81B60");

    val parsedColor: Color by lazy {
        Color(android.graphics.Color.parseColor(primaryColorHex))
    }

    companion object {
        val cachedValues = values()
        
        fun getById(id: String): UniCategory {
            for (category in cachedValues) {
                if (category.id == id) return category
            }
            return REGISTRATION
        }
    }
}
