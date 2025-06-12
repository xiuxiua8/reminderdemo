package com.example.reminderdemo.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.reminderdemo.model.User
import com.example.reminderdemo.data.UserRepository
import com.example.reminderdemo.utils.PreferencesManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

class RegisterViewModel(application: Application) : AndroidViewModel(application) {
    
    private val userRepository: UserRepository by lazy {
        UserRepository.getInstance(application)
    }
    
    private val preferencesManager: PreferencesManager by lazy {
        PreferencesManager.getInstance(application)
    }
    
    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()
    
    data class RegisterUiState(
        val isLoading: Boolean = false,
        val isSuccess: Boolean = false,
        val errorMessage: String? = null
    )
    
    fun register(user: User) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            try {
                // 检查用户名是否已存在
                val existingUser = userRepository.getUserByUsername(user.username)
                if (existingUser != null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "用户名已存在，请选择其他用户名"
                    )
                    return@launch
                }
                
                // 检查邮箱是否已存在（如果用户提供了邮箱）
                if (!user.email.isNullOrEmpty()) {
                    val existingEmailUser = userRepository.getUserByEmail(user.email!!)
                    if (existingEmailUser != null) {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = "邮箱已被注册，请使用其他邮箱"
                        )
                        return@launch
                    }
                }
                
                // 创建用户
                val success = userRepository.createUser(user)
                
                if (success) {
                    // 注册成功，自动登录
                    val loginResult = userRepository.authenticateUser(user.username, user.password)
                    if (loginResult != null) {
                        // 保存登录状态
                        preferencesManager.saveUser(loginResult.username, loginResult.displayName)
                        preferencesManager.updateLoginStatus(true)
                        
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isSuccess = true
                        )
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = "注册成功但自动登录失败，请手动登录"
                        )
                    }
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "注册失败，请稍后重试"
                    )
                }
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "注册过程中发生错误：${e.localizedMessage ?: "未知错误"}"
                )
            }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
} 