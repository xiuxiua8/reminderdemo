package com.example.reminderdemo.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.reminderdemo.model.Reminder

@Dao
interface ReminderDao {
    
    @Query("SELECT * FROM reminders WHERE userId = :userId ORDER BY updatedAt DESC")
    fun getAllReminders(userId: Long): LiveData<List<Reminder>>
    
    @Query("SELECT * FROM reminders WHERE id = :id AND userId = :userId")
    suspend fun getReminderById(id: Long, userId: Long): Reminder?
    
    @Query("SELECT * FROM reminders WHERE userId = :userId AND (title LIKE :searchQuery OR content LIKE :searchQuery) ORDER BY updatedAt DESC")
    fun searchReminders(userId: Long, searchQuery: String): LiveData<List<Reminder>>
    
    @Query("SELECT * FROM reminders WHERE userId = :userId AND category = :category ORDER BY updatedAt DESC")
    fun getRemindersByCategory(userId: Long, category: String): LiveData<List<Reminder>>
    
    @Query("SELECT * FROM reminders WHERE userId = :userId AND priority = :priority ORDER BY updatedAt DESC")
    fun getRemindersByPriority(userId: Long, priority: Int): LiveData<List<Reminder>>
    
    @Insert
    suspend fun insertReminder(reminder: Reminder): Long
    
    @Update
    suspend fun updateReminder(reminder: Reminder)
    
    @Delete
    suspend fun deleteReminder(reminder: Reminder)
    
    @Query("DELETE FROM reminders WHERE id = :id AND userId = :userId")
    suspend fun deleteReminderById(id: Long, userId: Long)
    
    @Query("SELECT DISTINCT category FROM reminders WHERE userId = :userId ORDER BY category")
    fun getAllCategories(userId: Long): LiveData<List<String>>
    
    @Query("SELECT COUNT(*) FROM reminders WHERE userId = :userId")
    suspend fun getReminderCount(userId: Long): Int
    
    @Query("DELETE FROM reminders WHERE userId = :userId")
    suspend fun deleteAllRemindersForUser(userId: Long)
} 