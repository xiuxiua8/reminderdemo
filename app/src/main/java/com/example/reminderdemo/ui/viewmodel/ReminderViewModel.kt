package com.example.reminderdemo.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.reminderdemo.data.ReminderDatabase
import com.example.reminderdemo.data.ReminderRepository
import com.example.reminderdemo.model.Reminder
import com.example.reminderdemo.utils.DateUtils
import com.example.reminderdemo.utils.PreferencesManager
import kotlinx.coroutines.launch

class ReminderViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: ReminderRepository
    private val preferencesManager: PreferencesManager
    
    // LiveData for UI observation
    val allReminders: LiveData<List<Reminder>>
    val categories: LiveData<List<String>>
    
    // UI state
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage
    
    private val _operationSuccess = MutableLiveData<String?>()
    val operationSuccess: LiveData<String?> = _operationSuccess
    
    init {
        val reminderDao = ReminderDatabase.getDatabase(application).reminderDao()
        repository = ReminderRepository(reminderDao)
        preferencesManager = PreferencesManager(application)
        
        // 获取当前用户的数据
        val currentUserId = getCurrentUserId()
        allReminders = repository.getAllReminders(currentUserId)
        categories = repository.getAllCategories(currentUserId)
    }
    
    private fun getCurrentUserId(): Long {
        val userId = preferencesManager.currentUserId
        if (userId <= 0) {
            throw IllegalStateException("用户未登录或会话无效")
        }
        return userId
    }
    
    fun insertReminder(title: String, content: String, category: String, priority: Int) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val currentTime = DateUtils.getCurrentDate()
                val currentUserId = getCurrentUserId()
                
                val reminder = Reminder(
                    userId = currentUserId,
                    title = title,
                    content = content,
                    createdAt = currentTime,
                    updatedAt = currentTime,
                    category = category,
                    priority = priority
                )
                repository.insertReminder(reminder)
                _operationSuccess.value = "备忘录创建成功"
            } catch (e: Exception) {
                _errorMessage.value = "创建备忘录失败: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun updateReminder(id: Long, title: String, content: String, category: String, priority: Int) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val currentUserId = getCurrentUserId()
                val existingReminder = repository.getReminderById(id, currentUserId)
                
                if (existingReminder != null) {
                    val updatedReminder = existingReminder.copy(
                        title = title,
                        content = content,
                        category = category,
                        priority = priority,
                        updatedAt = DateUtils.getCurrentDate()
                    )
                    repository.updateReminder(updatedReminder)
                    _operationSuccess.value = "备忘录更新成功"
                } else {
                    _errorMessage.value = "找不到要更新的备忘录"
                }
            } catch (e: Exception) {
                _errorMessage.value = "更新备忘录失败: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun deleteReminder(reminder: Reminder) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                repository.deleteReminder(reminder)
                _operationSuccess.value = "备忘录删除成功"
            } catch (e: Exception) {
                _errorMessage.value = "删除备忘录失败: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun deleteReminderById(id: Long) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val currentUserId = getCurrentUserId()
                repository.deleteReminderById(id, currentUserId)
                _operationSuccess.value = "备忘录删除成功"
            } catch (e: Exception) {
                _errorMessage.value = "删除备忘录失败: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun searchReminders(query: String): LiveData<List<Reminder>> {
        val currentUserId = getCurrentUserId()
        return repository.searchReminders(currentUserId, query)
    }
    
    fun getRemindersByCategory(category: String): LiveData<List<Reminder>> {
        val currentUserId = getCurrentUserId()
        return repository.getRemindersByCategory(currentUserId, category)
    }
    
    suspend fun getReminderById(id: Long): Reminder? {
        val currentUserId = getCurrentUserId()
        return repository.getReminderById(id, currentUserId)
    }
    
    fun clearErrorMessage() {
        _errorMessage.value = null
    }
    
    fun clearSuccessMessage() {
        _operationSuccess.value = null
    }
} 