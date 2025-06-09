package com.example.reminderdemo.utils

import android.content.Context
import android.view.Gravity
import android.widget.Toast
import androidx.annotation.StringRes

/**
 * Toast工具类
 * 提供统一的消息提示管理
 */
object ToastUtils {
    
    private var currentToast: Toast? = null
    
    /**
     * 显示成功消息
     */
    fun showSuccess(context: Context, message: String) {
        showToast(context, "✅ $message", Toast.LENGTH_SHORT)
    }
    
    fun showSuccess(context: Context, @StringRes messageRes: Int) {
        showSuccess(context, context.getString(messageRes))
    }
    
    /**
     * 显示错误消息
     */
    fun showError(context: Context, message: String) {
        showToast(context, "❌ $message", Toast.LENGTH_LONG)
    }
    
    fun showError(context: Context, @StringRes messageRes: Int) {
        showError(context, context.getString(messageRes))
    }
    
    /**
     * 显示警告消息
     */
    fun showWarning(context: Context, message: String) {
        showToast(context, "⚠️ $message", Toast.LENGTH_SHORT)
    }
    
    fun showWarning(context: Context, @StringRes messageRes: Int) {
        showWarning(context, context.getString(messageRes))
    }
    
    /**
     * 显示信息消息
     */
    fun showInfo(context: Context, message: String) {
        showToast(context, "ℹ️ $message", Toast.LENGTH_SHORT)
    }
    
    fun showInfo(context: Context, @StringRes messageRes: Int) {
        showInfo(context, context.getString(messageRes))
    }
    
    /**
     * 显示普通消息
     */
    fun show(context: Context, message: String, duration: Int = Toast.LENGTH_SHORT) {
        showToast(context, message, duration)
    }
    
    fun show(context: Context, @StringRes messageRes: Int, duration: Int = Toast.LENGTH_SHORT) {
        show(context, context.getString(messageRes), duration)
    }
    
    /**
     * 取消当前显示的Toast
     */
    fun cancel() {
        currentToast?.cancel()
        currentToast = null
    }
    
    /**
     * 内部显示Toast的方法
     */
    private fun showToast(context: Context, message: String, duration: Int) {
        // 取消之前的Toast
        cancel()
        
        // 创建新的Toast
        currentToast = Toast.makeText(context, message, duration).apply {
            setGravity(Gravity.CENTER, 0, 0)
            show()
        }
    }
} 