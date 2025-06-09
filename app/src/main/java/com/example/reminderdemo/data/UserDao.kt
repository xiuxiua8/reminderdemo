package com.example.reminderdemo.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.reminderdemo.model.User

@Dao
interface UserDao {
    
    @Query("SELECT * FROM users ORDER BY username")
    fun getAllUsers(): LiveData<List<User>>
    
    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserById(id: Long): User?
    
    @Query("SELECT * FROM users WHERE username = :username")
    suspend fun getUserByUsername(username: String): User?
    
    @Query("SELECT * FROM users WHERE username = :username AND password = :password")
    suspend fun getUserByCredentials(username: String, password: String): User?
    
    @Insert
    suspend fun insertUser(user: User): Long
    
    @Insert
    suspend fun insertUsers(users: List<User>)
    
    @Update
    suspend fun updateUser(user: User)
    
    @Delete
    suspend fun deleteUser(user: User)
    
    @Query("DELETE FROM users WHERE id = :id")
    suspend fun deleteUserById(id: Long)
    
    @Query("SELECT COUNT(*) FROM users")
    suspend fun getUserCount(): Int
} 