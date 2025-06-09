package com.example.reminderdemo.utils

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * Dialog工具类
 * 提供统一的对话框管理
 */
object DialogUtils {
    
    /**
     * 显示确认对话框
     */
    fun showConfirmDialog(
        context: Context,
        title: String,
        message: String,
        positiveText: String = "确定",
        negativeText: String = "取消",
        onPositive: (() -> Unit)? = null,
        onNegative: (() -> Unit)? = null
    ): AlertDialog {
        return MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(positiveText) { _, _ -> onPositive?.invoke() }
            .setNegativeButton(negativeText) { _, _ -> onNegative?.invoke() }
            .show()
    }
    
    /**
     * 显示删除确认对话框
     */
    fun showDeleteDialog(
        context: Context,
        itemName: String,
        onConfirm: () -> Unit
    ): AlertDialog {
        return MaterialAlertDialogBuilder(context)
            .setTitle("🗑️ 删除确认")
            .setMessage("确定要删除「$itemName」吗？\n此操作无法撤销。")
            .setPositiveButton("删除") { _, _ -> onConfirm() }
            .setNegativeButton("取消", null)
            .show()
    }
    
    /**
     * 显示退出确认对话框
     */
    fun showExitDialog(
        context: Context,
        message: String = "确定要退出吗？",
        onConfirm: () -> Unit
    ): AlertDialog {
        return MaterialAlertDialogBuilder(context)
            .setTitle("🚪 退出确认")
            .setMessage(message)
            .setPositiveButton("确定") { _, _ -> onConfirm() }
            .setNegativeButton("取消", null)
            .show()
    }
    
    /**
     * 显示未保存更改对话框
     */
    fun showUnsavedChangesDialog(
        context: Context,
        onLeave: () -> Unit,
        onStay: (() -> Unit)? = null
    ): AlertDialog {
        return MaterialAlertDialogBuilder(context)
            .setTitle("⚠️ 未保存的更改")
            .setMessage("您有未保存的更改，确定要离开吗？")
            .setPositiveButton("离开") { _, _ -> onLeave() }
            .setNegativeButton("继续编辑") { _, _ -> onStay?.invoke() }
            .show()
    }
    
    /**
     * 显示信息对话框
     */
    fun showInfoDialog(
        context: Context,
        title: String,
        message: String,
        buttonText: String = "知道了",
        onDismiss: (() -> Unit)? = null
    ): AlertDialog {
        return MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(buttonText) { _, _ -> onDismiss?.invoke() }
            .show()
    }
    
    /**
     * 显示成功对话框
     */
    fun showSuccessDialog(
        context: Context,
        title: String = "✅ 操作成功",
        message: String,
        onDismiss: (() -> Unit)? = null
    ): AlertDialog {
        return showInfoDialog(context, title, message, "好的", onDismiss)
    }
    
    /**
     * 显示错误对话框
     */
    fun showErrorDialog(
        context: Context,
        title: String = "❌ 操作失败",
        message: String,
        onDismiss: (() -> Unit)? = null
    ): AlertDialog {
        return showInfoDialog(context, title, message, "知道了", onDismiss)
    }
} 