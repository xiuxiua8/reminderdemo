package com.example.reminderdemo.utils

object ValidationUtils {
    
    fun isValidUsername(username: String): Boolean {
        return username.isNotBlank() && username.length >= 3 && username.length <= 20
    }
    
    fun isValidPassword(password: String): Boolean {
        return password.isNotBlank() && password.length >= 6
    }
    
    fun isValidReminderTitle(title: String): Boolean {
        return title.isNotBlank() && title.length <= 100
    }
    
    fun isValidReminderContent(content: String): Boolean {
        return content.length <= 1000
    }
    
    fun getUsernameError(username: String): String? {
        return when {
            username.isBlank() -> "用户名不能为空"
            username.length < 3 -> "用户名至少需要3个字符"
            username.length > 20 -> "用户名不能超过20个字符"
            else -> null
        }
    }
    
    fun getPasswordError(password: String): String? {
        return when {
            password.isBlank() -> "密码不能为空"
            password.length < 6 -> "密码至少需要6个字符"
            else -> null
        }
    }
    
    fun getReminderTitleError(title: String): String? {
        return when {
            title.isBlank() -> "标题不能为空"
            title.length > 100 -> "标题不能超过100个字符"
            else -> null
        }
    }
    
    fun getReminderContentError(content: String): String? {
        return when {
            content.length > 1000 -> "内容不能超过1000个字符"
            else -> null
        }
    }
} 