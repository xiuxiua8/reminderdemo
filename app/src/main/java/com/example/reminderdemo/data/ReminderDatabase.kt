package com.example.reminderdemo.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.reminderdemo.model.Reminder
import com.example.reminderdemo.model.User

@Database(
    entities = [Reminder::class, User::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class ReminderDatabase : RoomDatabase() {
    
    abstract fun reminderDao(): ReminderDao
    abstract fun userDao(): UserDao
    
    companion object {
        @Volatile
        private var INSTANCE: ReminderDatabase? = null
        
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // 1. 创建用户表
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS users (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        username TEXT NOT NULL,
                        password TEXT NOT NULL,
                        displayName TEXT NOT NULL,
                        email TEXT,
                        createdAt INTEGER NOT NULL
                    )
                """)
                
                // 2. 插入默认用户
                database.execSQL("""
                    INSERT OR IGNORE INTO users (id, username, password, displayName, email, createdAt)
                    VALUES 
                    (1, 'admin', '123456', '管理员', 'admin@example.com', ${System.currentTimeMillis()}),
                    (2, 'user', 'password', '普通用户', 'user@example.com', ${System.currentTimeMillis()}),
                    (3, 'test', 'test123', '测试用户', 'test@example.com', ${System.currentTimeMillis()})
                """)
                
                // 3. 为reminders表添加userId列（如果不存在）
                try {
                    database.execSQL("ALTER TABLE reminders ADD COLUMN userId INTEGER NOT NULL DEFAULT 1")
                } catch (e: Exception) {
                    // 列可能已经存在，忽略错误
                }
                
                // 4. 创建索引
                database.execSQL("CREATE INDEX IF NOT EXISTS index_reminders_userId ON reminders(userId)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_reminders_createdAt ON reminders(createdAt)")
                database.execSQL("CREATE INDEX IF NOT EXISTS index_reminders_category ON reminders(category)")
            }
        }
        
        fun getDatabase(context: Context): ReminderDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ReminderDatabase::class.java,
                    "reminder_database_v2"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
        
        /**
         * 重建数据库 - 开发阶段使用
         */
        fun rebuildDatabase(context: Context) {
            synchronized(this) {
                INSTANCE?.close()
                context.deleteDatabase("reminder_database")
                INSTANCE = null
            }
        }
    }
} 