package com.example.reminderdemo.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.reminderdemo.data.ReminderDatabase
import com.example.reminderdemo.data.UserRepository
import com.example.reminderdemo.model.User
import com.example.reminderdemo.utils.PreferencesManager
import com.example.reminderdemo.utils.ValidationUtils
import kotlinx.coroutines.launch

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    
    private val userRepository: UserRepository
    private val preferencesManager: PreferencesManager
    
    // UI状态
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _loginSuccess = MutableLiveData<Boolean>()
    val loginSuccess: LiveData<Boolean> = _loginSuccess
    
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage
    
    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> = _currentUser
    
    init {
        val database = ReminderDatabase.getDatabase(application)
        userRepository = UserRepository(database.userDao())
        preferencesManager = PreferencesManager(application)
        
        // 检查是否已登录
        if (preferencesManager.hasValidUserSession()) {
            loadCurrentUser()
        }
    }
    
    private fun loadCurrentUser() {
        viewModelScope.launch {
            try {
                val userId = preferencesManager.currentUserId
                if (userId > 0) {
                    val user = userRepository.getUserById(userId)
                    _currentUser.value = user
                }
            } catch (e: Exception) {
                // 如果加载失败，清除会话
                preferencesManager.clearUserSession()
            }
        }
    }
    
    fun login(username: String, password: String, rememberLogin: Boolean) {
        // 验证输入
        val usernameError = ValidationUtils.getUsernameError(username)
        if (usernameError != null) {
            _errorMessage.value = usernameError
            return
        }
        
        val passwordError = ValidationUtils.getPasswordError(password)
        if (passwordError != null) {
            _errorMessage.value = passwordError
            return
        }
        
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                
                val user = userRepository.authenticateUser(username, password)
                if (user != null) {
                    preferencesManager.saveUserSession(user, rememberLogin)
                    _currentUser.value = user
                    _loginSuccess.value = true
                } else {
                    _errorMessage.value = "用户名或密码错误"
                }
            } catch (e: Exception) {
                _errorMessage.value = "登录失败: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun logout() {
        preferencesManager.clearUserSession()
        _currentUser.value = null
        _loginSuccess.value = false
    }
    
    fun isLoggedIn(): Boolean {
        return preferencesManager.hasValidUserSession()
    }
    
    fun getCurrentUserId(): Long {
        return preferencesManager.currentUserId
    }
    
    fun getCurrentUsername(): String? {
        return preferencesManager.username
    }
    
    fun getCurrentUserDisplayName(): String? {
        return preferencesManager.currentUserDisplayName
    }
    
    fun clearErrorMessage() {
        _errorMessage.value = null
    }
} 