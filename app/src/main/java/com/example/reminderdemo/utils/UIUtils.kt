package com.example.reminderdemo.utils

import android.content.Context
import android.view.View
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import com.example.reminderdemo.R

/**
 * UI交互增强工具类
 * 提供统一的UI交互效果管理
 */
object UIUtils {
    
    /**
     * 为按钮添加点击动画和触觉反馈
     */
    fun setupClickAnimation(view: View, hapticFeedback: Boolean = true) {
        view.setOnClickListener { v ->
            // 播放点击动画
            val animation = AnimationUtils.loadAnimation(v.context, R.anim.button_click)
            v.startAnimation(animation)
            
            // 触觉反馈
            if (hapticFeedback) {
                HapticUtils.lightTap(v)
            }
            
            // 恢复原来的点击监听器
            v.tag?.let { originalListener ->
                if (originalListener is View.OnClickListener) {
                    originalListener.onClick(v)
                }
            }
        }
    }
    
    /**
     * 为FAB添加弹跳动画
     */
    fun setupFabAnimation(fab: View, originalClickListener: View.OnClickListener? = null) {
        fab.setOnClickListener { v ->
            // 播放弹跳动画
            val animation = AnimationUtils.loadAnimation(v.context, R.anim.fab_bounce)
            v.startAnimation(animation)
            
            // 触觉反馈
            HapticUtils.mediumTap(v)
            
            // 延迟执行原来的点击事件
            v.postDelayed({
                originalClickListener?.onClick(v)
            }, 100)
        }
    }
    
    /**
     * 为芯片添加选择动画
     */
    fun setupChipAnimation(chip: View) {
        chip.setOnClickListener { v ->
            // 轻微的缩放动画
            v.animate()
                .scaleX(0.95f)
                .scaleY(0.95f)
                .setDuration(50)
                .withEndAction {
                    v.animate()
                        .scaleX(1.0f)
                        .scaleY(1.0f)
                        .setDuration(100)
                        .start()
                }
                .start()
            
            // 触觉反馈
            HapticUtils.lightTap(v)
        }
    }
    
    /**
     * 添加视图淡入动画
     */
    fun fadeIn(view: View, duration: Long = 300) {
        view.alpha = 0f
        view.visibility = View.VISIBLE
        view.animate()
            .alpha(1f)
            .setDuration(duration)
            .start()
    }
    
    /**
     * 添加视图淡出动画
     */
    fun fadeOut(view: View, duration: Long = 300, onComplete: (() -> Unit)? = null) {
        view.animate()
            .alpha(0f)
            .setDuration(duration)
            .withEndAction {
                view.visibility = View.GONE
                onComplete?.invoke()
            }
            .start()
    }
    
    /**
     * 添加视图滑入动画（从底部）
     */
    fun slideInFromBottom(view: View, duration: Long = 300) {
        view.translationY = view.height.toFloat()
        view.visibility = View.VISIBLE
        view.animate()
            .translationY(0f)
            .setDuration(duration)
            .start()
    }
    
    /**
     * 添加视图滑出动画（到底部）
     */
    fun slideOutToBottom(view: View, duration: Long = 300, onComplete: (() -> Unit)? = null) {
        view.animate()
            .translationY(view.height.toFloat())
            .setDuration(duration)
            .withEndAction {
                view.visibility = View.GONE
                onComplete?.invoke()
            }
            .start()
    }
    
    /**
     * 设置加载状态
     */
    fun setLoadingState(view: View, isLoading: Boolean) {
        if (isLoading) {
            view.alpha = 0.7f
            view.isEnabled = false
        } else {
            view.alpha = 1.0f
            view.isEnabled = true
        }
    }
    
    /**
     * 显示成功状态（绿色背景闪烁）
     */
    fun showSuccessState(view: View, context: Context) {
        val originalBackground = view.background
        val successColor = ContextCompat.getColor(context, R.color.success_light)
        
        view.setBackgroundColor(successColor)
        view.animate()
            .alpha(0.8f)
            .setDuration(200)
            .withEndAction {
                view.animate()
                    .alpha(1.0f)
                    .setDuration(200)
                    .withEndAction {
                        view.background = originalBackground
                    }
                    .start()
            }
            .start()
    }
    
    /**
     * 显示错误状态（红色背景闪烁）
     */
    fun showErrorState(view: View, context: Context) {
        val originalBackground = view.background
        val errorColor = ContextCompat.getColor(context, R.color.error_light)
        
        view.setBackgroundColor(errorColor)
        view.animate()
            .alpha(0.8f)
            .setDuration(200)
            .withEndAction {
                view.animate()
                    .alpha(1.0f)
                    .setDuration(200)
                    .withEndAction {
                        view.background = originalBackground
                    }
                    .start()
            }
            .start()
    }
} 