package com.example.reminderdemo.data

import com.example.reminderdemo.model.Reminder
import com.example.reminderdemo.model.User
import com.example.reminderdemo.utils.DateUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

object DatabaseInitializer {
    
    fun initializeWithSampleData(database: ReminderDatabase) {
        CoroutineScope(Dispatchers.IO).launch {
            val userDao = database.userDao()
            val reminderDao = database.reminderDao()
            
            // 检查是否已有用户数据
            val userCount = userDao.getUserCount()
            if (userCount == 0) {
                // 插入默认用户
                val defaultUsers = User.getDefaultUsers()
                userDao.insertUsers(defaultUsers)
            }
            
            // 检查是否已有备忘录数据
            val reminderCount = reminderDao.getReminderCount(1) // 检查admin用户的备忘录
            if (reminderCount == 0) {
                // 插入示例备忘录数据
                val sampleReminders = getSampleReminders()
                sampleReminders.forEach { reminder ->
                    reminderDao.insertReminder(reminder)
                }
            }
        }
    }
    
    private fun getSampleReminders(): List<Reminder> {
        val now = DateUtils.getCurrentDate()
        val calendar = Calendar.getInstance()
        
        return listOf(
            // admin用户的备忘录 (userId = 1)
            Reminder(
                userId = 1,
                title = "欢迎使用备忘录应用",
                content = "这是管理员账户的欢迎备忘录！您可以在这里记录重要的事情，设置分类和优先级。",
                createdAt = now,
                updatedAt = now,
                category = "默认",
                priority = 1
            ),
            Reminder(
                userId = 1,
                title = "管理系统维护",
                content = "需要定期检查系统性能和用户反馈，确保应用稳定运行。",
                createdAt = calendar.apply { add(Calendar.HOUR, -1) }.time,
                updatedAt = calendar.time,
                category = "工作",
                priority = 2
            ),
            
            // user用户的备忘录 (userId = 2)
            Reminder(
                userId = 2,
                title = "完成项目报告",
                content = "需要在本周五之前完成季度项目报告，包括进度总结和下季度计划。",
                createdAt = calendar.apply { add(Calendar.HOUR, -2) }.time,
                updatedAt = calendar.time,
                category = "工作",
                priority = 2
            ),
            Reminder(
                userId = 2,
                title = "购买生活用品",
                content = "牙膏、洗发水、纸巾、洗衣液",
                createdAt = calendar.apply { add(Calendar.DAY_OF_MONTH, -1) }.time,
                updatedAt = calendar.time,
                category = "购物",
                priority = 0
            ),
            Reminder(
                userId = 2,
                title = "健身计划",
                content = "每周至少运动3次，每次30分钟以上。可以选择跑步、游泳或健身房训练。",
                createdAt = calendar.apply { add(Calendar.DAY_OF_MONTH, -2) }.time,
                updatedAt = calendar.time,
                category = "健康",
                priority = 1
            ),
            
            // test用户的备忘录 (userId = 3)
            Reminder(
                userId = 3,
                title = "学习新技术",
                content = "研究Android Jetpack Compose的使用方法，准备在下个项目中应用。",
                createdAt = calendar.apply { add(Calendar.DAY_OF_MONTH, -3) }.time,
                updatedAt = calendar.time,
                category = "学习",
                priority = 1
            ),
            Reminder(
                userId = 3,
                title = "测试应用功能",
                content = "全面测试备忘录应用的各项功能，记录bug和改进建议。",
                createdAt = calendar.apply { add(Calendar.DAY_OF_MONTH, -1) }.time,
                updatedAt = calendar.time,
                category = "工作",
                priority = 1
            )
        )
    }
} 