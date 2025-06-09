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
import kotlinx.coroutines.launch

class ReminderViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: ReminderRepository
    
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
        allReminders = repository.getAllReminders()
        categories = repository.getAllCategories()
    }
    
    fun insertReminder(title: String, content: String, category: String, priority: Int) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val currentTime = DateUtils.getCurrentDate()
                val reminder = Reminder(
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
                val existingReminder = repository.getReminderById(id)
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
                repository.deleteReminderById(id)
                _operationSuccess.value = "备忘录删除成功"
            } catch (e: Exception) {
                _errorMessage.value = "删除备忘录失败: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun searchReminders(query: String): LiveData<List<Reminder>> {
        return repository.searchReminders(query)
    }
    
    fun getRemindersByCategory(category: String): LiveData<List<Reminder>> {
        return repository.getRemindersByCategory(category)
    }
    
    suspend fun getReminderById(id: Long): Reminder? {
        return repository.getReminderById(id)
    }
    
    fun clearErrorMessage() {
        _errorMessage.value = null
    }
    
    fun clearSuccessMessage() {
        _operationSuccess.value = null
    }
} 