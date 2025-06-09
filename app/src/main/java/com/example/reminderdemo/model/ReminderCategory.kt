package com.example.reminderdemo.model

enum class ReminderCategory(val displayName: String, val color: String) {
    DEFAULT("默认", "#6200EE"),
    WORK("工作", "#FF5722"),
    PERSONAL("个人", "#4CAF50"),
    STUDY("学习", "#2196F3"),
    HEALTH("健康", "#FF9800"),
    SHOPPING("购物", "#9C27B0"),
    TRAVEL("旅行", "#00BCD4"),
    OTHER("其他", "#795548");
    
    companion object {
        fun fromString(category: String): ReminderCategory {
            return values().find { it.displayName == category } ?: DEFAULT
        }
        
        fun getAllCategories(): List<ReminderCategory> {
            return values().toList()
        }
    }
} 