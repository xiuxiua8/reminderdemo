package com.example.reminderdemo.ui.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.reminderdemo.R
import com.example.reminderdemo.databinding.ActivityReminderDetailBinding
import com.example.reminderdemo.model.Priority
import com.example.reminderdemo.model.Reminder
import com.example.reminderdemo.model.ReminderCategory
import com.example.reminderdemo.ui.viewmodel.ReminderViewModel
import com.example.reminderdemo.utils.DateUtils
import com.example.reminderdemo.utils.ValidationUtils
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.chip.Chip
import kotlinx.coroutines.launch

class ReminderDetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_REMINDER_ID = "extra_reminder_id"
        const val EXTRA_MODE = "extra_mode"
        const val MODE_ADD = "mode_add"
        const val MODE_EDIT = "mode_edit"
        const val MODE_VIEW = "mode_view"
    }

    private lateinit var binding: ActivityReminderDetailBinding
    private val reminderViewModel: ReminderViewModel by viewModels()
    
    private var currentReminder: Reminder? = null
    private var mode = MODE_ADD
    private var reminderId: Long = -1
    
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private var selectedCategory: ReminderCategory = ReminderCategory.DEFAULT
    private var selectedPriority: Priority = Priority.NORMAL

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        binding = ActivityReminderDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupToolbar()
        getIntentData()
        setupChips()
        setupBottomSheet()
        setupUI()
        observeViewModel()
        
        if (mode == MODE_EDIT || mode == MODE_VIEW) {
            loadReminderData()
        } else {
            setupForNewReminder()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_detail_toolbar, menu)
        
        // Hide info button for new reminders (no metadata to show)
        if (mode == MODE_ADD) {
            menu?.findItem(R.id.action_info)?.isVisible = false
        }
        
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_info -> {
                toggleBottomSheet()
                true
            }
            R.id.action_save -> {
                saveReminder()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun getIntentData() {
        mode = intent.getStringExtra(EXTRA_MODE) ?: MODE_ADD
        reminderId = intent.getLongExtra(EXTRA_REMINDER_ID, -1)
        
        updateToolbarTitle()
    }

    private fun updateToolbarTitle() {
        supportActionBar?.title = when (mode) {
            MODE_ADD -> "新建备忘录"
            MODE_EDIT -> "编辑备忘录"
            MODE_VIEW -> "备忘录详情"
            else -> "备忘录"
        }
    }

    private fun setupChips() {
        setupCategoryChips()
        setupPriorityChips()
    }

    private fun setupCategoryChips() {
        val categories = ReminderCategory.getAllCategories()
        
        categories.forEach { category ->
            val chip = Chip(this)
            chip.text = category.displayName
            chip.isCheckable = true
            
            chip.textSize = 15f  
            chip.chipCornerRadius = 16f  
            chip.chipStartPadding = 10f  
            chip.chipEndPadding = 10f  
            chip.textStartPadding = 8f  
            chip.textEndPadding = 8f  
            chip.chipMinHeight = 44f  
            
            chip.chipStrokeColor = ContextCompat.getColorStateList(this, category.colorRes)
            chip.setTextColor(ContextCompat.getColor(this, R.color.white))             

            // 设置选中状态的背景色
            val states = arrayOf(
                intArrayOf(-android.R.attr.state_checked),
                intArrayOf(android.R.attr.state_checked)
            )
            val colors = intArrayOf(
                ContextCompat.getColor(this, category.colorRes),
                ContextCompat.getColor(this, android.R.color.transparent)
            )
            chip.chipBackgroundColor = android.content.res.ColorStateList(states, colors)
            
            chip.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectedCategory = category
                    chip.setTextColor(ContextCompat.getColor(this, category.colorRes))
                } else {
                    chip.setTextColor(ContextCompat.getColor(this, R.color.white))
                }
            }
            
            binding.chipGroupCategory.addView(chip)
            
            // Set default selection
            if (category == ReminderCategory.DEFAULT) {
                chip.isChecked = true
            }
        }
    }

    private fun setupPriorityChips() {
        val priorities = Priority.getAllPriorities()
        
        priorities.forEach { priority ->
            val chip = Chip(this)
            chip.text = priority.displayName
            chip.isCheckable = true
            
            chip.textSize = 16f 
            chip.chipCornerRadius = 20f  
            chip.chipStartPadding = 14f 
            chip.chipEndPadding = 14f  
            chip.textStartPadding = 10f
            chip.textEndPadding = 10f 
            chip.chipMinHeight = 48f 
            chip.chipStrokeWidth = 2.5f  
            
            // 使用预定义的颜色
            val color = when(priority) {
                Priority.NORMAL -> R.color.priority_normal
                Priority.IMPORTANT -> R.color.priority_important
                Priority.URGENT -> R.color.priority_urgent
            }
            chip.chipStrokeColor = ContextCompat.getColorStateList(this, color)
            chip.setTextColor(ContextCompat.getColor(this, color))
            
            // 设置选中状态的背景色
            val states = arrayOf(
                intArrayOf(android.R.attr.state_checked),
                intArrayOf(-android.R.attr.state_checked)
            )
            val colors = intArrayOf(
                ContextCompat.getColor(this, color),
                ContextCompat.getColor(this, android.R.color.transparent)
            )
            chip.chipBackgroundColor = android.content.res.ColorStateList(states, colors)
            
            chip.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectedPriority = priority
                    // 选中时文字变白色
                    chip.setTextColor(ContextCompat.getColor(this, R.color.white))
                } else {
                    // 未选中时文字使用原色
                    chip.setTextColor(ContextCompat.getColor(this, color))
                }
            }
            
            binding.chipGroupPriority.addView(chip)
            
            // Set default selection
            if (priority == Priority.NORMAL) {
                chip.isChecked = true
            }
        }
    }

    private fun setupBottomSheet() {
        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheetMetadata)
        bottomSheetBehavior.isHideable = true
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
    }
    
    private fun toggleBottomSheet() {
        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_HIDDEN) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        } else {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    private fun setupUI() {
        // Set up edit mode vs view mode
        val isEditable = mode != MODE_VIEW
        
        binding.etTitle.isEnabled = isEditable
        binding.etContent.isEnabled = isEditable
        binding.chipGroupCategory.isEnabled = isEditable
        binding.chipGroupPriority.isEnabled = isEditable
        
        // Set up chip groups for edit mode
        for (i in 0 until binding.chipGroupCategory.childCount) {
            binding.chipGroupCategory.getChildAt(i).isEnabled = isEditable
        }
        for (i in 0 until binding.chipGroupPriority.childCount) {
            binding.chipGroupPriority.getChildAt(i).isEnabled = isEditable
        }
        
        // Info button is now in toolbar, no FAB to manage
        
        // Set up character counter
        setupCharacterCounter()
    }

    private fun setupCharacterCounter() {
        binding.etContent.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val count = s?.length ?: 0
                binding.tvCharCount.text = "$count 字符"
            }
        })
    }

    private fun observeViewModel() {
        reminderViewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        reminderViewModel.errorMessage.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                reminderViewModel.clearErrorMessage()
            }
        }

        reminderViewModel.operationSuccess.observe(this) { message ->
            message?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                reminderViewModel.clearSuccessMessage()
                finish()
            }
        }
    }

    private fun loadReminderData() {
        if (reminderId == -1L) {
            Toast.makeText(this, "无效的备忘录ID", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        lifecycleScope.launch {
            try {
                val reminder = reminderViewModel.getReminderById(reminderId)
                if (reminder != null) {
                    currentReminder = reminder
                    populateFields(reminder)
                } else {
                    Toast.makeText(this@ReminderDetailActivity, "备忘录不存在", Toast.LENGTH_SHORT).show()
                    finish()
                }
            } catch (e: Exception) {
                Toast.makeText(this@ReminderDetailActivity, "加载失败: ${e.message}", Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

    private fun setupForNewReminder() {
        // Info button is handled in toolbar for all modes
    }

    private fun populateFields(reminder: Reminder) {
        binding.etTitle.setText(reminder.title)
        binding.etContent.setText(reminder.content)
        
        // Set category chip
        val categoryToSelect = ReminderCategory.getAllCategories().find { it.displayName == reminder.category }
            ?: ReminderCategory.DEFAULT
        selectedCategory = categoryToSelect
        
        for (i in 0 until binding.chipGroupCategory.childCount) {
            val chip = binding.chipGroupCategory.getChildAt(i) as Chip
            chip.isChecked = chip.text == categoryToSelect.displayName
        }
        
        // Set priority chip
        val priorityToSelect = Priority.fromValue(reminder.priority)
        selectedPriority = priorityToSelect
        
        for (i in 0 until binding.chipGroupPriority.childCount) {
            val chip = binding.chipGroupPriority.getChildAt(i) as Chip
            chip.isChecked = chip.text == priorityToSelect.displayName
        }
        
        // Update metadata
        binding.tvCreatedAt.text = DateUtils.formatFullDate(reminder.createdAt)
        binding.tvUpdatedAt.text = DateUtils.formatFullDate(reminder.updatedAt)
        binding.tvCharCount.text = "${reminder.content.length} 字符"
    }

    private fun saveReminder() {
        if (!validateInput()) {
            return
        }

        val title = binding.etTitle.text.toString().trim()
        val content = binding.etContent.text.toString().trim()
        val category = selectedCategory.displayName
        val priority = selectedPriority.value

        when (mode) {
            MODE_ADD -> {
                reminderViewModel.insertReminder(title, content, category, priority)
            }
            MODE_EDIT -> {
                currentReminder?.let { reminder ->
                    reminderViewModel.updateReminder(reminder.id, title, content, category, priority)
                }
            }
        }
    }

    private fun validateInput(): Boolean {
        val title = binding.etTitle.text.toString().trim()
        val content = binding.etContent.text.toString().trim()

        // Clear previous errors
        binding.tilTitle.error = null
        binding.tilContent.error = null

        var isValid = true

        // Validate title
        val titleError = ValidationUtils.getReminderTitleError(title)
        if (titleError != null) {
            binding.tilTitle.error = titleError
            isValid = false
        }

        // Validate content
        val contentError = ValidationUtils.getReminderContentError(content)
        if (contentError != null) {
            binding.tilContent.error = contentError
            isValid = false
        }

        return isValid
    }

    override fun onBackPressed() {
        // Close bottom sheet if open
        if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_HIDDEN) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            return
        }
        
        // Check if there are unsaved changes
        if (hasUnsavedChanges()) {
            androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("未保存的更改")
                .setMessage("您有未保存的更改，确定要离开吗？")
                .setPositiveButton("离开") { _, _ ->
                    super.onBackPressed()
                }
                .setNegativeButton("继续编辑", null)
                .show()
        } else {
            super.onBackPressed()
        }
    }

    private fun hasUnsavedChanges(): Boolean {
        if (mode == MODE_VIEW) return false
        
        val currentTitle = binding.etTitle.text.toString().trim()
        val currentContent = binding.etContent.text.toString().trim()
        val currentCategory = selectedCategory.displayName
        val currentPriority = selectedPriority.value

        return when (mode) {
            MODE_ADD -> {
                currentTitle.isNotBlank() || currentContent.isNotBlank()
            }
            MODE_EDIT -> {
                currentReminder?.let { reminder ->
                    reminder.title != currentTitle ||
                    reminder.content != currentContent ||
                    reminder.category != currentCategory ||
                    reminder.priority != currentPriority
                } ?: false
            }
            else -> false
        }
    }
} 