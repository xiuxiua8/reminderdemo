package com.example.reminderdemo.model

data class User(
    val username: String,
    val password: String
) {
    companion object {
        // 简单的默认用户，实际项目中应该从数据库或服务器获取
        val DEFAULT_USERS = listOf(
            User("admin", "123456"),
            User("user", "password"),
            User("test", "test123")
        )
    }
} 