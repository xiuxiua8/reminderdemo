package com.example.reminderdemo.data

import androidx.lifecycle.LiveData
import com.example.reminderdemo.model.User
import android.content.Context
import com.example.reminderdemo.data.ReminderDatabase

class UserRepository(private val userDao: UserDao) {
    
    companion object {
        @Volatile
        private var INSTANCE: UserRepository? = null
        
        fun getInstance(context: Context? = null): UserRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = if (context != null) {
                    val database = ReminderDatabase.getDatabase(context)
                    UserRepository(database.userDao())
                } else {
                    // 如果没有context，假设已经初始化过了
                    throw IllegalStateException("UserRepository not initialized")
                }
                INSTANCE = instance
                instance
            }
        }
    }
    
    fun getAllUsers(): LiveData<List<User>> {
        return userDao.getAllUsers()
    }
    
    suspend fun getUserById(id: Long): User? {
        return userDao.getUserById(id)
    }
    
    suspend fun getUserByUsername(username: String): User? {
        return userDao.getUserByUsername(username)
    }
    
    suspend fun getUserByEmail(email: String): User? {
        return userDao.getUserByEmail(email)
    }
    
    suspend fun authenticateUser(username: String, password: String): User? {
        return userDao.getUserByCredentials(username, password)
    }
    
    suspend fun insertUser(user: User): Long {
        return userDao.insertUser(user)
    }
    
    suspend fun insertUsers(users: List<User>) {
        userDao.insertUsers(users)
    }
    
    suspend fun updateUser(user: User) {
        userDao.updateUser(user)
    }
    
    suspend fun deleteUser(user: User) {
        userDao.deleteUser(user)
    }
    
    suspend fun deleteUserById(id: Long) {
        userDao.deleteUserById(id)
    }
    
    suspend fun getUserCount(): Int {
        return userDao.getUserCount()
    }
    
    suspend fun isUsernameAvailable(username: String): Boolean {
        return getUserByUsername(username) == null
    }
    
    suspend fun createUser(user: User): Boolean {
        return try {
            val userId = insertUser(user)
            userId > 0
        } catch (e: Exception) {
            false
        }
    }
    
    suspend fun loginUser(username: String, password: String): User? {
        return authenticateUser(username, password)
    }
} 