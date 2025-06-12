package com.example.reminderdemo.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.example.reminderdemo.R
import com.example.reminderdemo.databinding.ActivityRegisterBinding
import com.example.reminderdemo.model.User
import com.example.reminderdemo.utils.HapticUtils
import com.example.reminderdemo.utils.AnimationUtils
import com.example.reminderdemo.utils.PasswordStrengthHelper
import com.example.reminderdemo.utils.UsernameValidator
import com.example.reminderdemo.data.UserRepository
import kotlinx.coroutines.Job
import com.example.reminderdemo.viewmodel.RegisterViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewModel: RegisterViewModel
    
    // 防抖验证Job
    private var usernameValidationJob: Job? = null
    private var passwordValidationJob: Job? = null
    
    // UserRepository实例
    private lateinit var userRepository: UserRepository
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // 初始化ViewModel
        viewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        )[RegisterViewModel::class.java]
        
        // 初始化UserRepository
        userRepository = UserRepository.getInstance(this)
        
        setupUI()
        setupObservers()
        setupValidation()
    }
    
    private fun setupUI() {
        // 设置点击事件
        binding.btnRegister.setOnClickListener {
            HapticUtils.lightTap(it)
            performRegister()
        }
        
        binding.tvLoginLink.setOnClickListener {
            HapticUtils.lightTap(it)
            navigateToLogin()
        }
        
        // 设置输入框焦点变化监听
        binding.etUsername.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                validateUsername()
            }
        }
        
        binding.etEmail.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                validateEmail()
            }
        }
        
        binding.etPassword.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                validatePassword()
            }
        }
        
        binding.etConfirmPassword.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                validateConfirmPassword()
            }
        }
    }
    
    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.uiState.collect { state ->
                updateUIState(state)
            }
        }
    }
    
    private fun updateUIState(state: RegisterViewModel.RegisterUiState) {
        // 更新loading状态
        binding.progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE
        binding.groupFormElements.visibility = if (state.isLoading) View.GONE else View.VISIBLE
        
        // 处理错误信息
        state.errorMessage?.let { message ->
            showError(message)
            viewModel.clearError()
        }
        
        // 处理注册成功
        if (state.isSuccess) {
            showSuccess("注册成功！正在跳转...")
            navigateToMain()
        }
    }
    
    private fun setupValidation() {
        // 实时验证用户名
        binding.etUsername.addTextChangedListener { text ->
            if (!text.isNullOrEmpty()) {
                binding.tilUsername.error = null
                binding.tilUsername.helperText = null
                // 异步验证用户名唯一性
                validateUsernameAsync(text.toString())
            } else {
                binding.tilUsername.helperText = null
                usernameValidationJob?.cancel()
            }
        }
        
        // 实时验证邮箱
        binding.etEmail.addTextChangedListener { text ->
            if (!text.isNullOrEmpty()) {
                binding.tilEmail.error = null
            }
        }
        
        // 实时验证密码
        binding.etPassword.addTextChangedListener { text ->
            if (!text.isNullOrEmpty()) {
                binding.tilPassword.error = null
                // 更新密码强度指示
                updatePasswordStrength()
                // 当密码改变时，重新验证确认密码
                if (binding.etConfirmPassword.text?.isNotEmpty() == true) {
                    validateConfirmPassword()
                }
            } else {
                binding.tilPassword.helperText = null
            }
        }
        
        // 实时验证确认密码
        binding.etConfirmPassword.addTextChangedListener { text ->
            if (!text.isNullOrEmpty()) {
                binding.tilConfirmPassword.error = null
            }
        }
    }
    
    private fun performRegister() {
        // 验证所有输入
        val isUsernameValid = validateUsername()
        val isEmailValid = validateEmail()
        val isPasswordValid = validatePassword()
        val isConfirmPasswordValid = validateConfirmPassword()
        
        if (!isUsernameValid || !isEmailValid || !isPasswordValid || !isConfirmPasswordValid) {
            showError("请检查输入信息")
            return
        }
        
        // 创建用户对象
        val user = User(
            username = binding.etUsername.text.toString().trim(),
            password = binding.etPassword.text.toString(),
            displayName = binding.etUsername.text.toString().trim(),
            email = binding.etEmail.text.toString().trim(),
            createdAt = System.currentTimeMillis()
        )
        
        // 执行注册
        viewModel.register(user)
    }
    
    private fun validateUsername(): Boolean {
        val username = binding.etUsername.text.toString().trim()
        
        // 使用增强的用户名验证
        val result = UsernameValidator.validateFormat(username)
        if (!result.isValid) {
            binding.tilUsername.error = result.errorMessage
            return false
        }
        
        binding.tilUsername.error = null
        return true
    }
    
    private fun validateUsernameAsync(username: String) {
        // 取消之前的验证任务
        usernameValidationJob?.cancel()
        
        usernameValidationJob = lifecycleScope.launch {
            // 防抖延迟
            kotlinx.coroutines.delay(500)
            
            try {
                val result = UsernameValidator.validateComplete(username.trim(), userRepository)
                
                // 更新UI需要在主线程
                if (!result.isValid) {
                    binding.tilUsername.error = result.errorMessage
                } else {
                    binding.tilUsername.error = null
                    binding.tilUsername.helperText = "用户名可用 ✓"
                }
            } catch (e: Exception) {
                // 忽略取消异常
            }
        }
    }
    
    private fun validateEmail(): Boolean {
        val email = binding.etEmail.text.toString().trim()
        return when {
            email.isEmpty() -> {
                binding.tilEmail.error = "请输入邮箱地址"
                false
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                binding.tilEmail.error = "请输入有效的邮箱地址"
                false
            }
            else -> {
                binding.tilEmail.error = null
                true
            }
        }
    }
    
    private fun validatePassword(): Boolean {
        val password = binding.etPassword.text.toString()
        
        // 使用增强的密码验证
        if (!PasswordStrengthHelper.isPasswordValid(password)) {
            val result = PasswordStrengthHelper.evaluatePassword(password, this)
            binding.tilPassword.error = result.suggestions.firstOrNull() ?: "密码不符合要求"
            return false
        }
        
        binding.tilPassword.error = null
        return true
    }
    
    private fun updatePasswordStrength() {
        val password = binding.etPassword.text.toString()
        if (password.isNotEmpty()) {
            val result = PasswordStrengthHelper.evaluatePassword(password, this)
            // 这里可以添加密码强度指示器的UI更新
            // 例如更新颜色、进度条等
            binding.tilPassword.helperText = "${result.message} (${result.score}%)"
        } else {
            binding.tilPassword.helperText = null
        }
    }
    
    private fun validateConfirmPassword(): Boolean {
        val password = binding.etPassword.text.toString()
        val confirmPassword = binding.etConfirmPassword.text.toString()
        return when {
            confirmPassword.isEmpty() -> {
                binding.tilConfirmPassword.error = "请确认密码"
                false
            }
            confirmPassword != password -> {
                binding.tilConfirmPassword.error = "两次输入的密码不一致"
                false
            }
            else -> {
                binding.tilConfirmPassword.error = null
                true
            }
        }
    }
    
    private fun showError(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
            .setBackgroundTint(getColor(R.color.error_color))
            .setTextColor(getColor(R.color.white))
            .show()
    }
    
    private fun showSuccess(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT)
            .setBackgroundTint(getColor(R.color.success_color))
            .setTextColor(getColor(R.color.white))
            .show()
    }
    
    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        AnimationUtils.applyFadeAnimation(this)
        finish()
    }
    
    private fun navigateToMain() {
        val intent = Intent(this, com.example.reminderdemo.MainActivity::class.java)
        // 清除任务栈，防止返回到登录页面
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        AnimationUtils.applyFadeAnimation(this)
        finish()
    }
    
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        navigateToLogin()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // 取消所有异步验证任务
        usernameValidationJob?.cancel()
        passwordValidationJob?.cancel()
    }
} 