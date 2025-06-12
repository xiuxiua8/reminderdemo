package com.example.reminderdemo.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UsernameValidator {
    
    data class UsernameValidationResult(
        val isValid: Boolean,
        val errorMessage: String? = null,
        val isAvailable: Boolean? = null // null表示尚未检查唯一性
    )
    
    companion object {
        
        // 预定义的不可用用户名列表
        private val RESERVED_USERNAMES = listOf(
            "admin", "administrator", "root", "system", "user", "guest",
            "null", "undefined", "test", "demo", "api", "www", "mail",
            "email", "support", "help", "info", "contact", "service"
        )
        
        /**
         * 验证用户名格式
         */
        fun validateFormat(username: String): UsernameValidationResult {
            return when {
                username.isEmpty() -> {
                    UsernameValidationResult(false, "请输入用户名")
                }
                username.length < 3 -> {
                    UsernameValidationResult(false, "用户名至少3个字符")
                }
                username.length > 20 -> {
                    UsernameValidationResult(false, "用户名不能超过20个字符")
                }
                !username.matches(Regex("^[a-zA-Z0-9_\\u4e00-\\u9fa5]+$")) -> {
                    UsernameValidationResult(false, "用户名只能包含字母、数字、下划线和中文")
                }
                username.startsWith("_") || username.endsWith("_") -> {
                    UsernameValidationResult(false, "用户名不能以下划线开头或结尾")
                }
                username.contains("__") -> {
                    UsernameValidationResult(false, "用户名不能包含连续的下划线")
                }
                RESERVED_USERNAMES.contains(username.lowercase()) -> {
                    UsernameValidationResult(false, "该用户名为系统保留，请选择其他用户名")
                }
                else -> {
                    UsernameValidationResult(true)
                }
            }
        }
        
        /**
         * 检查用户名唯一性
         * 需要外部传入UserRepository实例
         */
        suspend fun checkAvailability(username: String, userRepository: com.example.reminderdemo.data.UserRepository? = null): Boolean = withContext(Dispatchers.IO) {
            // 添加一些延迟，改善用户体验
            kotlinx.coroutines.delay(300)
            
            if (userRepository != null) {
                // 使用真实的数据库检查
                val existingUser = userRepository.getUserByUsername(username)
                return@withContext existingUser == null
            } else {
                // 回退到模拟检查
                val existingUsernames = listOf(
                    "admin", "user", "zilong", "test", "demo", "sample",
                    "example", "hello", "world", "android", "kotlin"
                )
                return@withContext !existingUsernames.contains(username.lowercase())
            }
        }
        
        /**
         * 完整验证（格式 + 唯一性）
         */
        suspend fun validateComplete(username: String, userRepository: com.example.reminderdemo.data.UserRepository? = null): UsernameValidationResult {
            // 首先验证格式
            val formatResult = validateFormat(username)
            if (!formatResult.isValid) {
                return formatResult
            }
            
            // 然后检查唯一性
            val isAvailable = checkAvailability(username, userRepository)
            
            return if (isAvailable) {
                UsernameValidationResult(true, null, true)
            } else {
                UsernameValidationResult(false, "用户名已存在，请选择其他用户名", false)
            }
        }
        
        /**
         * 生成用户名建议
         */
        fun generateSuggestions(username: String): List<String> {
            if (username.length < 3) return emptyList()
            
            val suggestions = mutableListOf<String>()
            
            // 添加数字后缀
            for (i in 1..99) {
                suggestions.add("${username}$i")
                if (suggestions.size >= 3) break
            }
            
            // 添加年份后缀
            val currentYear = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)
            suggestions.add("${username}$currentYear")
            
            // 添加常见后缀
            val suffixes = listOf("_new", "_2024", "_user", "_app")
            suffixes.forEach { suffix ->
                if (suggestions.size < 6) {
                    suggestions.add("$username$suffix")
                }
            }
            
            return suggestions.distinct().take(5)
        }
    }
} 