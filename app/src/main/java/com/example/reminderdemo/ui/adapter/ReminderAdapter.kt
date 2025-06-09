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
                
                // Category
                tvCategory.text = reminder.category
                val category = ReminderCategory.fromString(reminder.category)
                try {
                    val colorRes = when (category) {
                        ReminderCategory.WORK -> android.graphics.Color.parseColor(category.color)
                        ReminderCategory.PERSONAL -> android.graphics.Color.parseColor(category.color)
                        ReminderCategory.STUDY -> android.graphics.Color.parseColor(category.color)
                        ReminderCategory.HEALTH -> android.graphics.Color.parseColor(category.color)
                        ReminderCategory.SHOPPING -> android.graphics.Color.parseColor(category.color)
                        ReminderCategory.TRAVEL -> android.graphics.Color.parseColor(category.color)
                        ReminderCategory.OTHER -> android.graphics.Color.parseColor(category.color)
                        else -> android.graphics.Color.parseColor(ReminderCategory.DEFAULT.color)
                    }
                    tvCategory.setBackgroundColor(colorRes)
                } catch (e: Exception) {
                    // Use default color if parsing fails
                    tvCategory.setBackgroundColor(android.graphics.Color.parseColor("#6200EE"))
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
                root.setOnClickListener { onItemClick(reminder) }
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