package com.example.reminderdemo.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    
    private const val DATE_FORMAT_FULL = "yyyy-MM-dd HH:mm:ss"
    private const val DATE_FORMAT_SHORT = "MM-dd HH:mm"
    private const val DATE_FORMAT_DATE_ONLY = "yyyy-MM-dd"
    private const val DATE_FORMAT_TIME_ONLY = "HH:mm"
    
    private val fullDateFormat = SimpleDateFormat(DATE_FORMAT_FULL, Locale.getDefault())
    private val shortDateFormat = SimpleDateFormat(DATE_FORMAT_SHORT, Locale.getDefault())
    private val dateOnlyFormat = SimpleDateFormat(DATE_FORMAT_DATE_ONLY, Locale.getDefault())
    private val timeOnlyFormat = SimpleDateFormat(DATE_FORMAT_TIME_ONLY, Locale.getDefault())
    
    fun formatFullDate(date: Date): String {
        return fullDateFormat.format(date)
    }
    
    fun formatShortDate(date: Date): String {
        return shortDateFormat.format(date)
    }
    
    fun formatDateOnly(date: Date): String {
        return dateOnlyFormat.format(date)
    }
    
    fun formatTimeOnly(date: Date): String {
        return timeOnlyFormat.format(date)
    }
    
    fun formatRelativeTime(date: Date): String {
        val now = Date()
        val diff = now.time - date.time
        
        return when {
            diff < 60 * 1000 -> "刚刚"
            diff < 60 * 60 * 1000 -> "${diff / (60 * 1000)}分钟前"
            diff < 24 * 60 * 60 * 1000 -> "${diff / (60 * 60 * 1000)}小时前"
            diff < 7 * 24 * 60 * 60 * 1000 -> "${diff / (24 * 60 * 60 * 1000)}天前"
            else -> formatShortDate(date)
        }
    }
    
    fun isToday(date: Date): Boolean {
        val today = Calendar.getInstance()
        val targetDate = Calendar.getInstance().apply { time = date }
        
        return today.get(Calendar.YEAR) == targetDate.get(Calendar.YEAR) &&
                today.get(Calendar.DAY_OF_YEAR) == targetDate.get(Calendar.DAY_OF_YEAR)
    }
    
    fun isYesterday(date: Date): Boolean {
        val yesterday = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -1)
        }
        val targetDate = Calendar.getInstance().apply { time = date }
        
        return yesterday.get(Calendar.YEAR) == targetDate.get(Calendar.YEAR) &&
                yesterday.get(Calendar.DAY_OF_YEAR) == targetDate.get(Calendar.DAY_OF_YEAR)
    }
    
    fun getCurrentDate(): Date {
        return Date()
    }
} 