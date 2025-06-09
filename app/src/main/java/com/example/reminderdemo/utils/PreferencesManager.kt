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
        private const val KEY_REMEMBER_LOGIN = "remember_login"
        private const val KEY_LAST_LOGIN_TIME = "last_login_time"
        
        // 应用设置
        private const val KEY_SORT_ORDER = "sort_order"
        private const val KEY_DEFAULT_CATEGORY = "default_category"
        private const val KEY_THEME_MODE = "theme_mode"
    }
    
    // 登录相关
    var isLoggedIn: Boolean
        get() = sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false)
        set(value) = sharedPreferences.edit().putBoolean(KEY_IS_LOGGED_IN, value).apply()
    
    var username: String?
        get() = sharedPreferences.getString(KEY_USERNAME, null)
        set(value) = sharedPreferences.edit().putString(KEY_USERNAME, value).apply()
    
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
    
    fun login(username: String, rememberLogin: Boolean) {
        this.isLoggedIn = true
        this.username = username
        this.rememberLogin = rememberLogin
        this.lastLoginTime = System.currentTimeMillis()
    }
    
    fun logout() {
        if (!rememberLogin) {
            sharedPreferences.edit().clear().apply()
        } else {
            isLoggedIn = false
            lastLoginTime = 0
        }
    }
    
    fun clearAll() {
        sharedPreferences.edit().clear().apply()
    }
} 