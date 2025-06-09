package com.example.reminderdemo

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu

import androidx.activity.viewModels

import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.reminderdemo.databinding.ActivityMainBinding
import com.example.reminderdemo.ui.activity.LoginActivity
import com.example.reminderdemo.ui.activity.ReminderDetailActivity
import com.example.reminderdemo.ui.adapter.ReminderAdapter
import com.example.reminderdemo.ui.viewmodel.LoginViewModel
import com.example.reminderdemo.ui.viewmodel.ReminderViewModel
import com.example.reminderdemo.model.Reminder
import com.example.reminderdemo.data.DatabaseInitializer
import com.example.reminderdemo.data.ReminderDatabase
import com.example.reminderdemo.utils.AnimationUtils
import com.example.reminderdemo.utils.ToastUtils
import com.example.reminderdemo.utils.DialogUtils
import com.example.reminderdemo.utils.UIUtils
import com.example.reminderdemo.utils.HapticUtils

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val loginViewModel: LoginViewModel by viewModels()
    private val reminderViewModel: ReminderViewModel by viewModels()
    private lateinit var reminderAdapter: ReminderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Check if user is logged in
        if (!loginViewModel.isLoggedIn()) {
            navigateToLogin()
                return
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        
        setupUI()
        setupRecyclerView()
        observeViewModel()
        initializeDatabase()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            R.id.action_logout -> {
                showLogoutDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupUI() {
        // 设置FAB动画和触觉反馈
        UIUtils.setupFabAnimation(binding.fab) {
            navigateToAddReminder()
        }
        
        // Search functionality
        binding.etSearch.addTextChangedListener { editable ->
            val query = editable.toString()
            if (query.isBlank()) {
                reminderViewModel.allReminders.observe(this) { reminders ->
                    reminderAdapter.submitList(reminders)
                    updateEmptyState(reminders.isEmpty())
                }
            } else {
                reminderViewModel.searchReminders(query).observe(this) { reminders ->
                    reminderAdapter.submitList(reminders)
                    updateEmptyState(reminders.isEmpty())
                }
            }
        }
    }
    
    private fun setupRecyclerView() {
        reminderAdapter = ReminderAdapter(
            onItemClick = { reminder ->
                navigateToEditReminder(reminder.id)
            },
            onMoreClick = { reminder, view ->
                showMoreOptionsMenu(reminder, view)
            }
        )
        
        binding.rvReminders.apply {
            adapter = reminderAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }
    
    private fun observeViewModel() {
        reminderViewModel.allReminders.observe(this) { reminders ->
            reminderAdapter.submitList(reminders)
            updateEmptyState(reminders.isEmpty())
        }
        
        reminderViewModel.isLoading.observe(this) { isLoading ->
            if (isLoading) {
                UIUtils.fadeIn(binding.progressBar)
                UIUtils.setLoadingState(binding.fab, true)
            } else {
                UIUtils.fadeOut(binding.progressBar)
                UIUtils.setLoadingState(binding.fab, false)
            }
        }
        
        reminderViewModel.errorMessage.observe(this) { error ->
            error?.let {
                ToastUtils.showError(this, it)
                HapticUtils.error(this)
                reminderViewModel.clearErrorMessage()
            }
        }
        
        reminderViewModel.operationSuccess.observe(this) { message ->
            message?.let {
                ToastUtils.showSuccess(this, it)
                HapticUtils.success(this)
                reminderViewModel.clearSuccessMessage()
            }
        }
    }
    
    private fun initializeDatabase() {
        val database = ReminderDatabase.getDatabase(this)
        //DatabaseInitializer.initializeWithSampleData(database)
    }
    
    private fun updateEmptyState(isEmpty: Boolean) {
        binding.layoutEmptyState.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.rvReminders.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }
    
    private fun showMoreOptionsMenu(reminder: Reminder, anchorView: View) {
        PopupMenu(this, anchorView).apply {
            menuInflater.inflate(R.menu.menu_reminder_item, menu)
            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.action_edit -> {
                        navigateToEditReminder(reminder.id)
                        true
                    }
                    R.id.action_delete -> {
                        showDeleteConfirmDialog(reminder)
                        true
                    }
                    else -> false
                }
            }
            show()
        }
    }
    
    private fun showDeleteConfirmDialog(reminder: Reminder) {
        HapticUtils.warning(this)
        DialogUtils.showDeleteDialog(
            context = this,
            itemName = reminder.title
        ) {
            HapticUtils.delete(this)
            reminderViewModel.deleteReminder(reminder)
        }
    }
    
    private fun showLogoutDialog() {
        DialogUtils.showExitDialog(
            context = this,
            message = "确定要退出登录吗？"
        ) {
            logout()
        }
    }
    
    private fun logout() {
        loginViewModel.logout()
        navigateToLogin()
    }
    
    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        // 应用淡入淡出动画
        AnimationUtils.applyFadeAnimation(this)
        finish()
    }
    
    private fun navigateToAddReminder() {
        val intent = Intent(this, ReminderDetailActivity::class.java).apply {
            putExtra(ReminderDetailActivity.EXTRA_MODE, ReminderDetailActivity.MODE_ADD)
        }
        startActivity(intent)
        // 应用向前导航动画
        AnimationUtils.applyForwardAnimation(this)
    }
    
    private fun navigateToEditReminder(reminderId: Long) {
        val intent = Intent(this, ReminderDetailActivity::class.java).apply {
            putExtra(ReminderDetailActivity.EXTRA_MODE, ReminderDetailActivity.MODE_EDIT)
            putExtra(ReminderDetailActivity.EXTRA_REMINDER_ID, reminderId)
        }
        startActivity(intent)
        // 应用向前导航动画
        AnimationUtils.applyForwardAnimation(this)
    }
    
    private fun navigateToViewReminder(reminderId: Long) {
        val intent = Intent(this, ReminderDetailActivity::class.java).apply {
            putExtra(ReminderDetailActivity.EXTRA_MODE, ReminderDetailActivity.MODE_VIEW)
            putExtra(ReminderDetailActivity.EXTRA_REMINDER_ID, reminderId)
        }
        startActivity(intent)
        // 应用向前导航动画
        AnimationUtils.applyForwardAnimation(this)
    }
}