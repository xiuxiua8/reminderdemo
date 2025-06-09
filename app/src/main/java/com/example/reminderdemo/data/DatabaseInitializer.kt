package com.example.reminderdemo.data

import com.example.reminderdemo.model.Reminder
import com.example.reminderdemo.utils.DateUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

object DatabaseInitializer {
    
    fun initializeWithSampleData(database: ReminderDatabase) {
        CoroutineScope(Dispatchers.IO).launch {
            val reminderDao = database.reminderDao()
            
            // 检查是否已有数据
            val existingReminders = reminderDao.getAllReminders()
            
            // 如果没有数据，插入示例数据
            val sampleReminders = getSampleReminders()
            sampleReminders.forEach { reminder ->
                reminderDao.insertReminder(reminder)
            }
        }
    }
    
    private fun getSampleReminders(): List<Reminder> {
        val now = DateUtils.getCurrentDate()
        val calendar = Calendar.getInstance()
        
        return listOf(
            Reminder(
                title = "欢迎使用备忘录应用",
                content = "这是您的第一条备忘录！您可以在这里记录重要的事情，设置分类和优先级。",
                createdAt = now,
                updatedAt = now,
                category = "默认",
                priority = 1
            ),
            Reminder(
                title = "完成项目报告",
                content = "需要在本周五之前完成季度项目报告，包括进度总结和下季度计划。",
                createdAt = calendar.apply { add(Calendar.HOUR, -2) }.time,
                updatedAt = calendar.time,
                category = "工作",
                priority = 2
            ),
            Reminder(
                title = "购买生活用品",
                content = "牙膏、洗发水、纸巾、洗衣液",
                createdAt = calendar.apply { add(Calendar.DAY_OF_MONTH, -1) }.time,
                updatedAt = calendar.time,
                category = "购物",
                priority = 0
            ),
            Reminder(
                title = "学习新技术",
                content = "研究Android Jetpack Compose的使用方法，准备在下个项目中应用。",
                createdAt = calendar.apply { add(Calendar.DAY_OF_MONTH, -3) }.time,
                updatedAt = calendar.time,
                category = "学习",
                priority = 1
            ),
            Reminder(
                title = "健身计划",
                content = "每周至少运动3次，每次30分钟以上。可以选择跑步、游泳或健身房训练。",
                createdAt = calendar.apply { add(Calendar.DAY_OF_MONTH, -5) }.time,
                updatedAt = calendar.time,
                category = "健康",
                priority = 1
            )
        )
    }
} 