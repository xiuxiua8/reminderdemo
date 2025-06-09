package com.example.reminderdemo.model

import com.example.reminderdemo.R

enum class ReminderCategory(val displayName: String, val color: String) {
    DEFAULT("默认", "#FF3030"),    // 红色 - Red
    WORK("工作", "#FF8C00"),       // 橙色 - Dark Orange  
    PERSONAL("个人", "#FFD700"),   // 黄色 - Gold
    STUDY("学习", "#32CD32"),      // 绿色 - Lime Green
    HEALTH("健康", "#00CED1"),     // 青色 - Dark Turquoise
    SHOPPING("购物", "#4169E1"),   // 蓝色 - Royal Blue
    TRAVEL("旅行", "#8A2BE2");     // 紫色 - Blue Violet

    /**
     * 获取对应的颜色资源ID
     */
    val colorRes: Int
        get() = when (this) {
            DEFAULT -> R.color.category_default
            WORK -> R.color.category_work
            PERSONAL -> R.color.category_personal
            STUDY -> R.color.category_study
            HEALTH -> R.color.category_health
            SHOPPING -> R.color.category_shopping
            TRAVEL -> R.color.category_travel
        }
    
    companion object {
        fun fromString(category: String): ReminderCategory {
            return values().find { it.displayName == category } ?: DEFAULT
        }
        
        fun getAllCategories(): List<ReminderCategory> {
            return values().toList()
        }
    }
} 