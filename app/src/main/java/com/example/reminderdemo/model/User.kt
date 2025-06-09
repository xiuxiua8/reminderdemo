package com.example.reminderdemo.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val username: String,
    val password: String,
    val displayName: String = username,
    val email: String? = null,
    val createdAt: Long = System.currentTimeMillis()
) {
    companion object {
        // 默认用户数据，用于初始化数据库
        fun getDefaultUsers(): List<User> {
            return listOf(
                User(
                    id = 1,
                    username = "admin",
                    password = "123456",
                    displayName = "管理员",
                    email = "admin@example.com"
                ),
                User(
                    id = 2,
                    username = "user",
                    password = "password",
                    displayName = "普通用户",
                    email = "user@example.com"
                ),
                User(
                    id = 3,
                    username = "zilong",
                    password = "zilong",
                    displayName = "测试用户",
                    email = "zilong@example.com"
                )
            )
        }
        
        // 保持向后兼容性
        @Deprecated("使用 getDefaultUsers() 替代")
        val DEFAULT_USERS = listOf(
            User(username = "user", password = "password"),
            User(username = "zilong", password = "zilong")
        )
    }
} 