package com.example.reminderdemo.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.reminderdemo.R
import com.example.reminderdemo.databinding.ActivityReminderDetailBinding
import com.example.reminderdemo.model.Priority
import com.example.reminderdemo.model.Reminder
import com.example.reminderdemo.model.ReminderCategory
import com.example.reminderdemo.ui.viewmodel.ReminderViewModel
import com.example.reminderdemo.utils.DateUtils
import com.example.reminderdemo.utils.ValidationUtils
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        binding = ActivityReminderDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupToolbar()
        getIntentData()
        setupDropdowns()
        setupUI()
        observeViewModel()
        
        if (mode == MODE_EDIT || mode == MODE_VIEW) {
            loadReminderData()
        } else {
            setupForNewReminder()
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

    private fun setupDropdowns() {
        // Setup category dropdown
        val categories = ReminderCategory.getAllCategories()
        val categoryAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categories)
        binding.actvCategory.setAdapter(categoryAdapter)
        binding.actvCategory.setText(ReminderCategory.DEFAULT.displayName, false)

        // Setup priority dropdown
        val priorities = Priority.getAllPriorities().map { it.displayName }
        val priorityAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, priorities)
        binding.actvPriority.setAdapter(priorityAdapter)
        binding.actvPriority.setText(Priority.NORMAL.displayName, false)
    }

    private fun setupUI() {
        // Set up edit mode vs view mode
        val isEditable = mode != MODE_VIEW
        
        binding.etTitle.isEnabled = isEditable
        binding.etContent.isEnabled = isEditable
        binding.actvCategory.isEnabled = isEditable
        binding.actvPriority.isEnabled = isEditable
        
        if (mode == MODE_VIEW) {
            binding.btnSave.visibility = View.GONE
            binding.btnCancel.text = "返回"
        }

        binding.btnSave.setOnClickListener {
            saveReminder()
        }

        binding.btnCancel.setOnClickListener {
            finish()
        }
        
        // Show/hide metadata card based on mode
        binding.cardMetadata.visibility = if (mode == MODE_ADD) View.GONE else View.VISIBLE
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
        binding.cardMetadata.visibility = View.GONE
    }

    private fun populateFields(reminder: Reminder) {
        binding.etTitle.setText(reminder.title)
        binding.etContent.setText(reminder.content)
        binding.actvCategory.setText(reminder.category, false)
        
        val priority = Priority.fromValue(reminder.priority)
        binding.actvPriority.setText(priority.displayName, false)
        
        // Update metadata
        binding.tvCreatedAt.text = DateUtils.formatFullDate(reminder.createdAt)
        binding.tvUpdatedAt.text = DateUtils.formatFullDate(reminder.updatedAt)
    }

    private fun saveReminder() {
        if (!validateInput()) {
            return
        }

        val title = binding.etTitle.text.toString().trim()
        val content = binding.etContent.text.toString().trim()
        val category = binding.actvCategory.text.toString()
        val priorityName = binding.actvPriority.text.toString()
        val priority = Priority.getAllPriorities().find { it.displayName == priorityName }?.value ?: 0

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
        val currentCategory = binding.actvCategory.text.toString()
        val currentPriorityName = binding.actvPriority.text.toString()
        val currentPriority = Priority.getAllPriorities().find { it.displayName == currentPriorityName }?.value ?: 0

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