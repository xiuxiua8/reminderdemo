package com.example.reminderdemo.utils

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.HapticFeedbackConstants
import android.view.View

/**
 * 触觉反馈工具类
 * 提供统一的触觉反馈管理
 */
object HapticUtils {
    
    /**
     * 轻微点击反馈
     */
    fun lightTap(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            view.performHapticFeedback(HapticFeedbackConstants.CLOCK_TICK)
        } else {
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
        }
    }
    
    /**
     * 标准点击反馈
     */
    fun mediumTap(view: View) {
        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
    }
    
    /**
     * 重度点击反馈
     */
    fun heavyTap(view: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
        } else {
            view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
        }
    }
    
    /**
     * 成功操作反馈
     */
    fun success(context: Context) {
        performVibration(context, VibrationPattern.SUCCESS)
    }
    
    /**
     * 错误操作反馈
     */
    fun error(context: Context) {
        performVibration(context, VibrationPattern.ERROR)
    }
    
    /**
     * 警告操作反馈
     */
    fun warning(context: Context) {
        performVibration(context, VibrationPattern.WARNING)
    }
    
    /**
     * 删除操作反馈
     */
    fun delete(context: Context) {
        performVibration(context, VibrationPattern.DELETE)
    }
    
    /**
     * 振动模式
     */
    private enum class VibrationPattern(val pattern: LongArray, val amplitudes: IntArray? = null) {
        SUCCESS(longArrayOf(0, 100), intArrayOf(0, 80)),
        ERROR(longArrayOf(0, 100, 50, 100), intArrayOf(0, 255, 0, 255)),
        WARNING(longArrayOf(0, 150), intArrayOf(0, 150)),
        DELETE(longArrayOf(0, 50, 50, 100), intArrayOf(0, 100, 0, 200))
    }
    
    /**
     * 执行振动
     */
    private fun performVibration(context: Context, pattern: VibrationPattern) {
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vibratorManager.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            }
            
            if (vibrator.hasVibrator()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val amplitudes = pattern.amplitudes ?: IntArray(pattern.pattern.size) { 255 }
                    vibrator.vibrate(VibrationEffect.createWaveform(pattern.pattern, amplitudes, -1))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(pattern.pattern, -1)
                }
            }
        } catch (e: Exception) {
            // 忽略振动错误，确保应用继续正常运行
        }
    }
} 