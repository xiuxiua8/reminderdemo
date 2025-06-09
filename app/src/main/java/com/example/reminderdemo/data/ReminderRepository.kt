package com.example.reminderdemo.data

import androidx.lifecycle.LiveData
import com.example.reminderdemo.model.Reminder

class ReminderRepository(private val reminderDao: ReminderDao) {
    
    fun getAllReminders(): LiveData<List<Reminder>> {
        return reminderDao.getAllReminders()
    }
    
    suspend fun getReminderById(id: Long): Reminder? {
        return reminderDao.getReminderById(id)
    }
    
    fun searchReminders(query: String): LiveData<List<Reminder>> {
        return reminderDao.searchReminders("%$query%")
    }
    
    fun getRemindersByCategory(category: String): LiveData<List<Reminder>> {
        return reminderDao.getRemindersByCategory(category)
    }
    
    suspend fun insertReminder(reminder: Reminder): Long {
        return reminderDao.insertReminder(reminder)
    }
    
    suspend fun updateReminder(reminder: Reminder) {
        reminderDao.updateReminder(reminder)
    }
    
    suspend fun deleteReminder(reminder: Reminder) {
        reminderDao.deleteReminder(reminder)
    }
    
    suspend fun deleteReminderById(id: Long) {
        reminderDao.deleteReminderById(id)
    }
    
    fun getAllCategories(): LiveData<List<String>> {
        return reminderDao.getAllCategories()
    }
} 