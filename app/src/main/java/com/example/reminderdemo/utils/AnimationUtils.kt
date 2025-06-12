package com.example.reminderdemo.utils

import android.app.Activity
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils as AndroidAnimationUtils
import com.example.reminderdemo.R

/**
 * 动画工具类
 * 提供统一的页面过渡动画管理和微交互效果
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
     * 应用弹性进入动画（用于对话框和重要页面）
     * 新页面弹性缩放进入，当前页面淡出
     */
    fun applyBounceAnimation(activity: Activity) {
        activity.overridePendingTransition(R.anim.scale_bounce_in, R.anim.scale_fade_out)
    }
    
    /**
     * 应用向上滑入动画（用于底部弹出页面）
     * 新页面从底部滑入，当前页面保持不动
     */
    fun applySlideUpAnimation(activity: Activity) {
        activity.overridePendingTransition(R.anim.slide_up_enter, R.anim.fade_out)
    }
    
    /**
     * 应用向下滑出动画（用于关闭底部页面）
     * 当前页面向下滑出，上一页面淡入
     */
    fun applySlideDownAnimation(activity: Activity) {
        activity.overridePendingTransition(R.anim.fade_in, R.anim.slide_down_exit)
    }
    
    /**
     * 移除默认动画（用于某些特殊情况）
     */
    fun removeAnimation(activity: Activity) {
        activity.overridePendingTransition(0, 0)
    }
    
    // ================== 微交互动画 ==================
    
    /**
     * 按钮点击动画效果
     * 提供触觉反馈，按下缩小，释放弹回
     */
    fun animateButtonPress(view: View, onAnimationEnd: (() -> Unit)? = null) {
        val pressAnimation = AndroidAnimationUtils.loadAnimation(view.context, R.anim.button_press)
        val releaseAnimation = AndroidAnimationUtils.loadAnimation(view.context, R.anim.button_release)
        
        pressAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationRepeat(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                view.startAnimation(releaseAnimation)
            }
        })
        
        releaseAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationRepeat(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                onAnimationEnd?.invoke()
            }
        })
        
        view.startAnimation(pressAnimation)
    }
    
    /**
     * 表单错误震动动画
     * 用于输入验证失败时的视觉反馈
     */
    fun animateShake(view: View) {
        val animation = AndroidAnimationUtils.loadAnimation(view.context, R.anim.shake)
        view.startAnimation(animation)
    }
    
    /**
     * 元素弹性出现动画
     * 用于重要元素的强调显示
     */
    fun animateBounceIn(view: View, delay: Long = 0) {
        val animation = AndroidAnimationUtils.loadAnimation(view.context, R.anim.scale_bounce_in)
        if (delay > 0) {
            animation.startOffset = delay
        }
        view.visibility = View.VISIBLE
        view.startAnimation(animation)
    }
    
    /**
     * 脉冲动画效果
     * 用于Loading状态和吸引注意力
     */
    fun startPulseAnimation(view: View) {
        val animation = AndroidAnimationUtils.loadAnimation(view.context, R.anim.pulse)
        view.startAnimation(animation)
    }
    
    /**
     * 停止脉冲动画
     */
    fun stopPulseAnimation(view: View) {
        view.clearAnimation()
    }
    
    /**
     * 淡入动画
     * 用于内容的优雅显示
     */
    fun animateFadeIn(view: View, duration: Long = 300, delay: Long = 0) {
        view.alpha = 0f
        view.visibility = View.VISIBLE
        view.animate()
            .alpha(1f)
            .setDuration(duration)
            .setStartDelay(delay)
            .start()
    }
    
    /**
     * 淡出动画
     * 用于内容的优雅隐藏
     */
    fun animateFadeOut(view: View, duration: Long = 300, hideAfter: Boolean = true) {
        view.animate()
            .alpha(0f)
            .setDuration(duration)
            .withEndAction {
                if (hideAfter) {
                    view.visibility = View.GONE
                }
            }
            .start()
    }
    
    /**
     * 滑动显示动画
     * 从指定方向滑入
     */
    fun animateSlideIn(view: View, fromDirection: SlideDirection, duration: Long = 300) {
        val (startX, startY) = when (fromDirection) {
            SlideDirection.LEFT -> -view.width.toFloat() to 0f
            SlideDirection.RIGHT -> view.width.toFloat() to 0f
            SlideDirection.TOP -> 0f to -view.height.toFloat()
            SlideDirection.BOTTOM -> 0f to view.height.toFloat()
        }
        
        view.translationX = startX
        view.translationY = startY
        view.visibility = View.VISIBLE
        
        view.animate()
            .translationX(0f)
            .translationY(0f)
            .setDuration(duration)
            .start()
    }
    
    /**
     * 列表项动画
     * 为RecyclerView项目添加进入动画
     */
    fun animateListItem(view: View, position: Int) {
        val delay = (position * 50).toLong()
        view.alpha = 0f
        view.translationY = 100f
        
        view.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(300)
            .setStartDelay(delay)
            .start()
    }
    
    /**
     * 卡片点击效果
     * 轻微缩放和阴影变化
     */
    fun animateCardPress(view: View) {
        view.animate()
            .scaleX(0.98f)
            .scaleY(0.98f)
            .setDuration(100)
            .withEndAction {
                view.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(100)
                    .start()
            }
            .start()
    }
    
    enum class SlideDirection {
        LEFT, RIGHT, TOP, BOTTOM
    }
} 