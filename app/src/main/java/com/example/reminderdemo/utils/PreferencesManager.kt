package com.example.reminderdemo.utils

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {
    
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    
    companion object {
        private const val PREF_NAME = "reminder_prefs"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USERNAME = "username"
        private const val KEY_CURRENT_USER_ID = "current_user_id"
        private const val KEY_CURRENT_USER_DISPLAY_NAME = "current_user_display_name"
        private const val KEY_REMEMBER_LOGIN = "remember_login"
        private const val KEY_LAST_LOGIN_TIME = "last_login_time"
        
        // 应用设置
        private const val KEY_SORT_ORDER = "sort_order"
        private const val KEY_DEFAULT_CATEGORY = "default_category"
        private const val KEY_THEME_MODE = "theme_mode"
        
        // 单例模式
        @Volatile
        private var INSTANCE: PreferencesManager? = null
        
        fun getInstance(context: Context): PreferencesManager {
            return INSTANCE ?: synchronized(this) {
                val instance = PreferencesManager(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
    
    // 登录相关
    var isLoggedIn: Boolean
        get() = sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
        set(value) = sharedPreferences.edit().putBoolean(KEY_IS_LOGGED_IN, value).apply()
    
    var username: String?
        get() = sharedPreferences.getString(KEY_USERNAME, null)
        set(value) = sharedPreferences.edit().putString(KEY_USERNAME, value).apply()
    
    var currentUserId: Long
        get() = sharedPreferences.getLong(KEY_CURRENT_USER_ID, -1L)
        set(value) = sharedPreferences.edit().putLong(KEY_CURRENT_USER_ID, value).apply()
    
    var currentUserDisplayName: String?
        get() = sharedPreferences.getString(KEY_CURRENT_USER_DISPLAY_NAME, null)
        set(value) = sharedPreferences.edit().putString(KEY_CURRENT_USER_DISPLAY_NAME, value).apply()
    
    var rememberLogin: Boolean
        get() = sharedPreferences.getBoolean(KEY_REMEMBER_LOGIN, false)
        set(value) = sharedPreferences.edit().putBoolean(KEY_REMEMBER_LOGIN, value).apply()
    
    var lastLoginTime: Long
        get() = sharedPreferences.getLong(KEY_LAST_LOGIN_TIME, 0)
        set(value) = sharedPreferences.edit().putLong(KEY_LAST_LOGIN_TIME, value).apply()
    
    // 应用设置
    var sortOrder: String
        get() = sharedPreferences.getString(KEY_SORT_ORDER, "date_desc") ?: "date_desc"
        set(value) = sharedPreferences.edit().putString(KEY_SORT_ORDER, value).apply()
    
    var defaultCategory: String
        get() = sharedPreferences.getString(KEY_DEFAULT_CATEGORY, "默认") ?: "默认"
        set(value) = sharedPreferences.edit().putString(KEY_DEFAULT_CATEGORY, value).apply()
    
    var themeMode: String
        get() = sharedPreferences.getString(KEY_THEME_MODE, "system") ?: "system"
        set(value) = sharedPreferences.edit().putString(KEY_THEME_MODE, value).apply()
    
    /**
     * 登录成功后保存用户信息
     */
    fun saveUserSession(user: com.example.reminderdemo.model.User, rememberLogin: Boolean = false) {
        isLoggedIn = true
        username = user.username
        currentUserId = user.id
        currentUserDisplayName = user.displayName
        this.rememberLogin = rememberLogin
        lastLoginTime = System.currentTimeMillis()
    }
    
    /**
     * 退出登录，清除用户会话
     */
    fun clearUserSession() {
        isLoggedIn = false
        username = null
        currentUserId = -1L
        currentUserDisplayName = null
        lastLoginTime = 0
        // 根据设置决定是否保留记住登录状态
        if (!rememberLogin) {
            // 如果不记住登录，则清除所有信息
            sharedPreferences.edit().clear().apply()
        }
    }
    
    /**
     * 检查是否有有效的用户会话
     */
    fun hasValidUserSession(): Boolean {
        return isLoggedIn && currentUserId > 0 && !username.isNullOrBlank()
    }
    
    fun clearAll() {
        sharedPreferences.edit().clear().apply()
    }
    
    /**
     * 便捷方法：保存用户信息（用于注册/登录）
     */
    fun saveUser(username: String, displayName: String) {
        this.username = username
        this.currentUserDisplayName = displayName
    }
    
    /**
     * 便捷方法：设置登录状态
     */
    fun updateLoginStatus(loggedIn: Boolean) {
        this.isLoggedIn = loggedIn
        if (loggedIn) {
            this.lastLoginTime = System.currentTimeMillis()
        }
    }
} 