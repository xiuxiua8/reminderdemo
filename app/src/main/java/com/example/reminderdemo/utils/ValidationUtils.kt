package com.example.reminderdemo.utils

object ValidationUtils {
    
    private const val MIN_TITLE_LENGTH = 1
    private const val MAX_TITLE_LENGTH = 200
    private const val MIN_CONTENT_LENGTH = 1
    private const val MAX_CONTENT_LENGTH = 2000
    
    /**
     * 验证备忘录标题
     */
    fun getReminderTitleError(title: String): String? {
        return when {
            title.isBlank() -> "标题不能为空"
            title.length < MIN_TITLE_LENGTH -> "标题至少需要${MIN_TITLE_LENGTH}个字符"
            title.length > MAX_TITLE_LENGTH -> "标题不能超过${MAX_TITLE_LENGTH}个字符"
            else -> null
        }
    }
    
    /**
     * 验证备忘录内容
     */
    fun getReminderContentError(content: String): String? {
        return when {
            content.isBlank() -> "内容不能为空"
            content.length < MIN_CONTENT_LENGTH -> "内容至少需要${MIN_CONTENT_LENGTH}个字符"
            content.length > MAX_CONTENT_LENGTH -> "内容不能超过${MAX_CONTENT_LENGTH}个字符"
            else -> null
        }
    }
    
    /**
     * 验证用户名
     */
    fun getUsernameError(username: String): String? {
        return when {
            username.isBlank() -> "用户名不能为空"
            username.length < 3 -> "用户名至少需要3个字符"
            username.length > 20 -> "用户名不能超过20个字符"
            !username.matches(Regex("^[a-zA-Z0-9_]+$")) -> "用户名只能包含字母、数字和下划线"
            else -> null
        }
    }
    
    /**
     * 验证密码
     */
    fun getPasswordError(password: String): String? {
        return when {
            password.isBlank() -> "密码不能为空"
            password.length < 6 -> "密码至少需要6个字符"
            password.length > 50 -> "密码不能超过50个字符"
            else -> null
        }
    }
    
    /**
     * 验证搜索关键词
     */
    fun getSearchQueryError(query: String): String? {
        return when {
            query.length > 100 -> "搜索关键词不能超过100个字符"
            else -> null
        }
    }
    
    /**
     * 检查是否为有效的优先级值
     */
    fun isValidPriority(priority: Int): Boolean {
        return priority in 0..3
    }
    
    /**
     * 检查是否为有效的分类名称
     */
    fun isValidCategory(category: String): Boolean {
        return category.isNotBlank() && category.length <= 50
    }
} 