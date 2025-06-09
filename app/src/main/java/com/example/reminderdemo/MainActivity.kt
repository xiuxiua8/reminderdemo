package com.example.reminderdemo

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
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
        binding.fab.setOnClickListener {
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
        AlertDialog.Builder(this)
            .setTitle("删除备忘录")
            .setMessage("确定要删除\"${reminder.title}\"吗？")
            .setPositiveButton("删除") { _, _ ->
                reminderViewModel.deleteReminder(reminder)
            }
            .setNegativeButton("取消", null)
            .show()
    }
    
    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle("退出登录")
            .setMessage("确定要退出登录吗？")
            .setPositiveButton("确定") { _, _ ->
                logout()
            }
            .setNegativeButton("取消", null)
            .show()
    }
    
    private fun logout() {
        loginViewModel.logout()
        navigateToLogin()
    }
    
    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
    
    private fun navigateToAddReminder() {
        val intent = Intent(this, ReminderDetailActivity::class.java).apply {
            putExtra(ReminderDetailActivity.EXTRA_MODE, ReminderDetailActivity.MODE_ADD)
        }
        startActivity(intent)
    }
    
    private fun navigateToEditReminder(reminderId: Long) {
        val intent = Intent(this, ReminderDetailActivity::class.java).apply {
            putExtra(ReminderDetailActivity.EXTRA_MODE, ReminderDetailActivity.MODE_EDIT)
            putExtra(ReminderDetailActivity.EXTRA_REMINDER_ID, reminderId)
        }
        startActivity(intent)
    }
    
    private fun navigateToViewReminder(reminderId: Long) {
        val intent = Intent(this, ReminderDetailActivity::class.java).apply {
            putExtra(ReminderDetailActivity.EXTRA_MODE, ReminderDetailActivity.MODE_VIEW)
            putExtra(ReminderDetailActivity.EXTRA_REMINDER_ID, reminderId)
        }
        startActivity(intent)
    }
}