package com.example.reminderdemo.utils

import android.app.Activity
import com.example.reminderdemo.R

/**
 * 动画工具类
 * 提供统一的页面过渡动画管理
 */
object AnimationUtils {
    
    /**
     * 应用向前导航的动画（进入新页面）
     * 新页面从右侧滑入，当前页面向左侧滑出
     */
    fun applyForwardAnimation(activity: Activity) {
        activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }
    
    /**
     * 应用向后导航的动画（返回上一页）
     * 当前页面向右侧滑出，上一页面从左侧滑入
     */
    fun applyBackwardAnimation(activity: Activity) {
        activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
    
    /**
     * 应用淡入淡出动画（用于登录等特殊场景）
     * 新页面淡入，当前页面淡出
     */
    fun applyFadeAnimation(activity: Activity) {
        activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }
    
    /**
     * 移除默认动画（用于某些特殊情况）
     */
    fun removeAnimation(activity: Activity) {
        activity.overridePendingTransition(0, 0)
    }
} 