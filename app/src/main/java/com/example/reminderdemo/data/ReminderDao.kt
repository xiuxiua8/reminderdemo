package com.example.reminderdemo.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.reminderdemo.model.Reminder

@Dao
interface ReminderDao {
    
    @Query("SELECT * FROM reminders ORDER BY updatedAt DESC")
    fun getAllReminders(): LiveData<List<Reminder>>
    
    @Query("SELECT * FROM reminders WHERE id = :id")
    suspend fun getReminderById(id: Long): Reminder?
    
    @Query("SELECT * FROM reminders WHERE title LIKE :searchQuery OR content LIKE :searchQuery ORDER BY updatedAt DESC")
    fun searchReminders(searchQuery: String): LiveData<List<Reminder>>
    
    @Query("SELECT * FROM reminders WHERE category = :category ORDER BY updatedAt DESC")
    fun getRemindersByCategory(category: String): LiveData<List<Reminder>>
    
    @Insert
    suspend fun insertReminder(reminder: Reminder): Long
    
    @Update
    suspend fun updateReminder(reminder: Reminder)
    
    @Delete
    suspend fun deleteReminder(reminder: Reminder)
    
    @Query("DELETE FROM reminders WHERE id = :id")
    suspend fun deleteReminderById(id: Long)
    
    @Query("SELECT DISTINCT category FROM reminders ORDER BY category")
    fun getAllCategories(): LiveData<List<String>>
} 