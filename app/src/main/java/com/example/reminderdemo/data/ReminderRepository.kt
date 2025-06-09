package com.example.reminderdemo.data

import androidx.lifecycle.LiveData
import com.example.reminderdemo.model.Reminder

class ReminderRepository(private val reminderDao: ReminderDao) {
    
    fun getAllReminders(userId: Long): LiveData<List<Reminder>> {
        return reminderDao.getAllReminders(userId)
    }
    
    suspend fun getReminderById(id: Long, userId: Long): Reminder? {
        return reminderDao.getReminderById(id, userId)
    }
    
    fun searchReminders(userId: Long, query: String): LiveData<List<Reminder>> {
        return reminderDao.searchReminders(userId, "%$query%")
    }
    
    fun getRemindersByCategory(userId: Long, category: String): LiveData<List<Reminder>> {
        return reminderDao.getRemindersByCategory(userId, category)
    }
    
    fun getRemindersByPriority(userId: Long, priority: Int): LiveData<List<Reminder>> {
        return reminderDao.getRemindersByPriority(userId, priority)
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
    
    suspend fun deleteReminderById(id: Long, userId: Long) {
        reminderDao.deleteReminderById(id, userId)
    }
    
    fun getAllCategories(userId: Long): LiveData<List<String>> {
        return reminderDao.getAllCategories(userId)
    }
    
    suspend fun getReminderCount(userId: Long): Int {
        return reminderDao.getReminderCount(userId)
    }
    
    suspend fun deleteAllRemindersForUser(userId: Long) {
        reminderDao.deleteAllRemindersForUser(userId)
    }
} 