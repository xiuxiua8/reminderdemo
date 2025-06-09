package com.example.reminderdemo.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "reminders",
    indices = [
        Index(value = ["userId"]),
        Index(value = ["createdAt"]),
        Index(value = ["category"])
    ]
)
data class Reminder(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: Long, // 关联用户ID，暂时不使用外键约束
    val title: String,
    val content: String,
    val createdAt: Date,
    val updatedAt: Date,
    val category: String = "默认",
    val priority: Int = 0 // 0: 普通, 1: 重要, 2: 紧急
) 