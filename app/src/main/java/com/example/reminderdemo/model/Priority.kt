package com.example.reminderdemo.model

enum class Priority(val value: Int, val displayName: String, val color: String) {
    NORMAL(0, "普通", "#757575"),
    IMPORTANT(1, "重要", "#FF9800"),
    URGENT(2, "紧急", "#F44336");
    
    companion object {
        fun fromValue(value: Int): Priority {
            return values().find { it.value == value } ?: NORMAL
        }
        
        fun getAllPriorities(): List<Priority> {
            return values().toList()
        }
    }
} 