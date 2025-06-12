package com.example.reminderdemo.utils

import android.content.Context
import android.graphics.Color
import com.example.reminderdemo.R

class PasswordStrengthHelper {
    
    enum class PasswordStrength {
        WEAK,
        FAIR, 
        GOOD,
        STRONG
    }
    
    data class PasswordStrengthResult(
        val strength: PasswordStrength,
        val score: Int, // 0-100
        val message: String,
        val color: Int,
        val suggestions: List<String>
    )
    
    companion object {
        
        fun evaluatePassword(password: String, context: Context): PasswordStrengthResult {
            if (password.isEmpty()) {
                return PasswordStrengthResult(
                    strength = PasswordStrength.WEAK,
                    score = 0,
                    message = "请输入密码",
                    color = Color.parseColor("#B00020"),
                    suggestions = listOf("密码不能为空")
                )
            }
            
            var score = 0
            val suggestions = mutableListOf<String>()
            
            // 长度检查 (最多30分)
            when {
                password.length < 6 -> {
                    suggestions.add("密码至少需要6个字符")
                }
                password.length < 8 -> {
                    score += 15
                    suggestions.add("建议使用8个或更多字符")
                }
                password.length < 12 -> {
                    score += 25
                }
                else -> {
                    score += 30
                }
            }
            
            // 字符类型检查
            var hasLower = false
            var hasUpper = false
            var hasDigit = false
            var hasSpecial = false
            
            for (char in password) {
                when {
                    char.isLowerCase() -> hasLower = true
                    char.isUpperCase() -> hasUpper = true
                    char.isDigit() -> hasDigit = true
                    "!@#$%^&*()_+-=[]{}|;:,.<>?".contains(char) -> hasSpecial = true
                }
            }
            
            // 小写字母 (10分)
            if (hasLower) {
                score += 10
            } else {
                suggestions.add("包含小写字母")
            }
            
            // 大写字母 (15分)
            if (hasUpper) {
                score += 15
            } else {
                suggestions.add("包含大写字母")
            }
            
            // 数字 (15分)
            if (hasDigit) {
                score += 15
            } else {
                suggestions.add("包含数字")
            }
            
            // 特殊字符 (20分)
            if (hasSpecial) {
                score += 20
            } else {
                suggestions.add("包含特殊字符 (!@#$%^&* 等)")
            }
            
            // 复杂度检查 (10分)
            val uniqueChars = password.toSet().size
            if (uniqueChars >= password.length * 0.7) {
                score += 10
            } else {
                suggestions.add("避免重复字符")
            }
            
            // 常见密码检查 (-20分)
            val commonPasswords = listOf(
                "password", "123456", "123456789", "qwerty", "abc123",
                "password123", "admin", "letmein", "welcome", "monkey"
            )
            if (commonPasswords.any { password.contains(it, ignoreCase = true) }) {
                score -= 20
                suggestions.add("避免使用常见密码")
            }
            
            // 确保分数在0-100范围内
            score = score.coerceIn(0, 100)
            
            // 确定强度等级
            val strength = when {
                score < 30 -> PasswordStrength.WEAK
                score < 60 -> PasswordStrength.FAIR
                score < 80 -> PasswordStrength.GOOD
                else -> PasswordStrength.STRONG
            }
            
            // 获取消息和颜色
            val (message, color) = when (strength) {
                PasswordStrength.WEAK -> Pair("弱密码", Color.parseColor("#F44336"))
                PasswordStrength.FAIR -> Pair("一般密码", Color.parseColor("#FF9800"))
                PasswordStrength.GOOD -> Pair("良好密码", Color.parseColor("#2196F3"))
                PasswordStrength.STRONG -> Pair("强密码", Color.parseColor("#4CAF50"))
            }
            
            return PasswordStrengthResult(
                strength = strength,
                score = score,
                message = message,
                color = color,
                suggestions = if (strength == PasswordStrength.STRONG) {
                    listOf("密码强度很好！")
                } else {
                    suggestions.take(3) // 最多显示3个建议
                }
            )
        }
        
        fun isPasswordValid(password: String): Boolean {
            return password.length >= 6 && 
                   password.any { it.isLetter() } &&
                   password.any { it.isDigit() }
        }
        
        fun getMinimumRequirements(): List<String> {
            return listOf(
                "至少6个字符",
                "包含字母",
                "包含数字"
            )
        }
    }
} 