package com.example.reminderdemo.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.reminderdemo.databinding.ItemReminderBinding
import com.example.reminderdemo.model.Priority
import com.example.reminderdemo.model.Reminder
import com.example.reminderdemo.model.ReminderCategory
import com.example.reminderdemo.utils.DateUtils
import com.example.reminderdemo.utils.AnimationUtils

class ReminderAdapter(
    private val onItemClick: (Reminder) -> Unit,
    private val onMoreClick: (Reminder, View) -> Unit
) : ListAdapter<Reminder, ReminderAdapter.ReminderViewHolder>(ReminderDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        val binding = ItemReminderBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ReminderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        holder.bind(getItem(position))
        // 添加列表项进入动画
        AnimationUtils.animateListItem(holder.itemView, position)
    }

    inner class ReminderViewHolder(
        private val binding: ItemReminderBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(reminder: Reminder) {
            binding.apply {
                // Basic info
                tvTitle.text = reminder.title
                tvContent.text = reminder.content.ifEmpty { "无内容" }
                tvDate.text = DateUtils.formatRelativeTime(reminder.updatedAt)
                
                // Category - 使用新的彩虹渐变色
                tvCategory.text = reminder.category
                val category = ReminderCategory.fromString(reminder.category)
                try {
                    // 直接使用category的color属性，简化逻辑
                    val colorRes = android.graphics.Color.parseColor(category.color)
                    tvCategory.setBackgroundColor(colorRes)
                } catch (e: Exception) {
                    // Use default color if parsing fails
                    tvCategory.setBackgroundColor(android.graphics.Color.parseColor(ReminderCategory.DEFAULT.color))
                }
                
                // Priority
                val priority = Priority.fromValue(reminder.priority)
                when (priority) {
                    Priority.URGENT -> {
                        vPriorityIndicator.setBackgroundColor(
                            android.graphics.Color.parseColor(priority.color)
                        )
                        ivPriority.visibility = View.VISIBLE
                        ivPriority.setColorFilter(android.graphics.Color.parseColor(priority.color))
                    }
                    Priority.IMPORTANT -> {
                        vPriorityIndicator.setBackgroundColor(
                            android.graphics.Color.parseColor(priority.color)
                        )
                        ivPriority.visibility = View.VISIBLE
                        ivPriority.setColorFilter(android.graphics.Color.parseColor(priority.color))
                    }
                    Priority.NORMAL -> {
                        vPriorityIndicator.setBackgroundColor(
                            android.graphics.Color.parseColor(priority.color)
                        )
                        ivPriority.visibility = View.GONE
                    }
                }
                
                // Click listeners
                root.setOnClickListener { 
                    AnimationUtils.animateCardPress(it)
                    onItemClick(reminder) 
                }
                ivMore.setOnClickListener { onMoreClick(reminder, it) }
            }
        }
    }

    class ReminderDiffCallback : DiffUtil.ItemCallback<Reminder>() {
        override fun areItemsTheSame(oldItem: Reminder, newItem: Reminder): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Reminder, newItem: Reminder): Boolean {
            return oldItem == newItem
        }
    }
} 