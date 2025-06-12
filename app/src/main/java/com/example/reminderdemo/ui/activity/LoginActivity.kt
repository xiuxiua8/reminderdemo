package com.example.reminderdemo.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast

import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.reminderdemo.data.DatabaseInitializer
import com.example.reminderdemo.data.ReminderDatabase
import com.example.reminderdemo.databinding.ActivityLoginBinding
import com.example.reminderdemo.ui.viewmodel.LoginViewModel
import com.example.reminderdemo.utils.AnimationUtils
import com.example.reminderdemo.utils.UIUtils
import com.example.reminderdemo.utils.HapticUtils

class LoginActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel: LoginViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 初始化数据库
        initializeDatabase()
        
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
    
    private fun initializeDatabase() {
        val database = ReminderDatabase.getDatabase(this)
        DatabaseInitializer.initializeWithSampleData(database)
    }
    
    private fun setupUI() {
        // 设置登录按钮点击事件
        val originalClickListener = android.view.View.OnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val rememberLogin = binding.cbRememberLogin.isChecked
            
            // 清除之前的错误信息
            loginViewModel.clearErrorMessage()
            
            // 尝试登录
            loginViewModel.login(username, password, rememberLogin)
        }
        
        // 保存原始点击监听器到tag中
        binding.btnLogin.tag = originalClickListener
        
        // 为登录按钮添加点击动画
        UIUtils.setupClickAnimation(binding.btnLogin)
        
        // 设置注册链接点击事件
        binding.tvRegisterLink.setOnClickListener {
            HapticUtils.lightTap(it)
            navigateToRegister()
        }
    }

    private fun observeViewModel() {
        loginViewModel.isLoading.observe(this) { isLoading ->
            UIUtils.setLoadingState(binding.btnLogin, isLoading)
            if (isLoading) {
                binding.btnLogin.text = "登录中..."
            } else {
                binding.btnLogin.text = "登录"
            }
        }

        loginViewModel.loginSuccess.observe(this) { success ->
            success?.let {
                if (it) {
                    val username = loginViewModel.getCurrentUserDisplayName() ?: loginViewModel.getCurrentUsername()
                    HapticUtils.success(this)
                    Toast.makeText(this, "欢迎回来，$username!", Toast.LENGTH_SHORT).show()
                    navigateToMain()
                }
            }
        }

        loginViewModel.errorMessage.observe(this) { error ->
            error?.let {
                HapticUtils.error(this)
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun navigateToMain() {
        val intent = Intent(this, com.example.reminderdemo.MainActivity::class.java)
        startActivity(intent)
        // 应用淡入淡出动画效果
        AnimationUtils.applyFadeAnimation(this)
        finish()
    }
    
    private fun navigateToRegister() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
        AnimationUtils.applyFadeAnimation(this)
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // 清理资源
        loginViewModel.clearErrorMessage()
    }
} 