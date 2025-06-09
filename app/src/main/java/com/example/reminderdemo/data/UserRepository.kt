package com.example.reminderdemo.data

import androidx.lifecycle.LiveData
import com.example.reminderdemo.model.User

class UserRepository(private val userDao: UserDao) {
    
    fun getAllUsers(): LiveData<List<User>> {
        return userDao.getAllUsers()
    }
    
    suspend fun getUserById(id: Long): User? {
        return userDao.getUserById(id)
    }
    
    suspend fun getUserByUsername(username: String): User? {
        return userDao.getUserByUsername(username)
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
} 