package com.example.reminderdemo.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.reminderdemo.model.User
import com.example.reminderdemo.utils.PreferencesManager
import com.example.reminderdemo.utils.ValidationUtils

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    
    private val preferencesManager = PreferencesManager(application)
    
    // UI state
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult
    
    private val _usernameError = MutableLiveData<String?>()
    val usernameError: LiveData<String?> = _usernameError
    
    private val _passwordError = MutableLiveData<String?>()
    val passwordError: LiveData<String?> = _passwordError
    
    // Check if user is already logged in
    fun isLoggedIn(): Boolean {
        return preferencesManager.isLoggedIn
    }
    
    fun getCurrentUsername(): String? {
        return preferencesManager.username
    }
    
    fun login(username: String, password: String, rememberLogin: Boolean) {
        _isLoading.value = true
        
        // Clear previous errors
        _usernameError.value = null
        _passwordError.value = null
        
        // Validate input
        val usernameError = ValidationUtils.getUsernameError(username)
        val passwordError = ValidationUtils.getPasswordError(password)
        
        if (usernameError != null) {
            _usernameError.value = usernameError
            _isLoading.value = false
            return
        }
        
        if (passwordError != null) {
            _passwordError.value = passwordError
            _isLoading.value = false
            return
        }
        
        // Simulate network delay
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            // Check credentials against default users
            val user = User.DEFAULT_USERS.find { 
                it.username == username && it.password == password 
            }
            
            if (user != null) {
                // Login successful
                preferencesManager.login(username, rememberLogin)
                _loginResult.value = LoginResult.Success(username)
            } else {
                // Login failed
                _loginResult.value = LoginResult.Error("用户名或密码错误")
            }
            
            _isLoading.value = false
        }, 1000) // 1 second delay to simulate network request
    }
    
    fun logout() {
        preferencesManager.logout()
        _loginResult.value = LoginResult.LoggedOut
    }
    
    fun clearLoginResult() {
        _loginResult.value = null
    }
    
    fun clearErrors() {
        _usernameError.value = null
        _passwordError.value = null
    }
    
    sealed class LoginResult {
        data class Success(val username: String) : LoginResult()
        data class Error(val message: String) : LoginResult()
        object LoggedOut : LoginResult()
    }
} 