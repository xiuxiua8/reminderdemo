package com.example.reminderdemo.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View

import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.reminderdemo.databinding.ActivityLoginBinding
import com.example.reminderdemo.ui.viewmodel.LoginViewModel
import com.example.reminderdemo.utils.AnimationUtils
import com.example.reminderdemo.utils.ToastUtils
import com.example.reminderdemo.utils.UIUtils
import com.example.reminderdemo.utils.HapticUtils

class LoginActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel: LoginViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Check if user is already logged in
        if (loginViewModel.isLoggedIn()) {
            navigateToMain()
            return
        }
        
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupUI()
        observeViewModel()
    }
    
    private fun setupUI() {
        // 为登录按钮添加点击动画和触觉反馈
        val originalClickListener = View.OnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val rememberLogin = binding.cbRememberLogin.isChecked
            
            HapticUtils.mediumTap(binding.btnLogin)
            loginViewModel.login(username, password, rememberLogin)
        }
        
        binding.btnLogin.tag = originalClickListener
        UIUtils.setupClickAnimation(binding.btnLogin)
    }
    
    private fun observeViewModel() {
        loginViewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                showLoading()
            } else {
                hideLoading()
            }
        }
        
        loginViewModel.loginResult.observe(this) { result ->
            when (result) {
                is LoginViewModel.LoginResult.Success -> {
                    ToastUtils.showSuccess(this, "欢迎回来，${result.username}!")
                    HapticUtils.success(this)
                    UIUtils.showSuccessState(binding.btnLogin, this)
                    navigateToMain()
                }
                is LoginViewModel.LoginResult.Error -> {
                    ToastUtils.showError(this, result.message)
                    HapticUtils.error(this)
                    UIUtils.showErrorState(binding.btnLogin, this)
                }
                is LoginViewModel.LoginResult.LoggedOut -> {
                    // Handle logout if needed
                }
                null -> {
                    // No result yet
                }
            }
        }
        
        loginViewModel.usernameError.observe(this) { error ->
            binding.tilUsername.error = error
        }
        
        loginViewModel.passwordError.observe(this) { error ->
            binding.tilPassword.error = error
        }
    }
    
        private fun showLoading() {
        UIUtils.fadeIn(binding.progressBar)
        UIUtils.setLoadingState(binding.btnLogin, true)
        binding.etUsername.isEnabled = false
        binding.etPassword.isEnabled = false
        binding.cbRememberLogin.isEnabled = false
    }

    private fun hideLoading() {
        UIUtils.fadeOut(binding.progressBar)
        UIUtils.setLoadingState(binding.btnLogin, false)
        binding.etUsername.isEnabled = true
        binding.etPassword.isEnabled = true
        binding.cbRememberLogin.isEnabled = true
    }
    
    private fun navigateToMain() {
        val intent = Intent(this, com.example.reminderdemo.MainActivity::class.java)
        startActivity(intent)
        // 应用淡入淡出动画效果
        AnimationUtils.applyFadeAnimation(this)
        finish()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        loginViewModel.clearLoginResult()
        loginViewModel.clearErrors()
    }
} 