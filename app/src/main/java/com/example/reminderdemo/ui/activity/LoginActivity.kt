package com.example.reminderdemo.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.reminderdemo.databinding.ActivityLoginBinding
import com.example.reminderdemo.ui.viewmodel.LoginViewModel

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
        binding.btnLogin.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val rememberLogin = binding.cbRememberLogin.isChecked
            
            loginViewModel.login(username, password, rememberLogin)
        }
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
                    Toast.makeText(this, "欢迎回来，${result.username}!", Toast.LENGTH_SHORT).show()
                    navigateToMain()
                }
                is LoginViewModel.LoginResult.Error -> {
                    Toast.makeText(this, result.message, Toast.LENGTH_LONG).show()
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
        binding.progressBar.visibility = View.VISIBLE
        binding.btnLogin.isEnabled = false
        binding.etUsername.isEnabled = false
        binding.etPassword.isEnabled = false
        binding.cbRememberLogin.isEnabled = false
    }
    
    private fun hideLoading() {
        binding.progressBar.visibility = View.GONE
        binding.btnLogin.isEnabled = true
        binding.etUsername.isEnabled = true
        binding.etPassword.isEnabled = true
        binding.cbRememberLogin.isEnabled = true
    }
    
    private fun navigateToMain() {
        val intent = Intent(this, com.example.reminderdemo.MainActivity::class.java)
        startActivity(intent)
        finish()
    }
    
    override fun onDestroy() {
        super.onDestroy()
        loginViewModel.clearLoginResult()
        loginViewModel.clearErrors()
    }
} 