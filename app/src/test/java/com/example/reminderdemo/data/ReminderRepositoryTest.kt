package com.example.reminderdemo.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.reminderdemo.model.Reminder
import com.example.reminderdemo.utils.DateUtils
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

class ReminderRepositoryTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var reminderDao: ReminderDao

    private lateinit var repository: ReminderRepository

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        repository = ReminderRepository(reminderDao)
    }

    @Test
    fun `insertReminder should call dao insertReminder`() = runBlocking {
        // Given
        val reminder = Reminder(
            title = "Test Reminder",
            content = "Test Content",
            createdAt = DateUtils.getCurrentDate(),
            updatedAt = DateUtils.getCurrentDate(),
            category = "测试",
            priority = 1
        )

        // When
        repository.insertReminder(reminder)

        // Then
        verify(reminderDao).insertReminder(reminder)
    }

    @Test
    fun `updateReminder should call dao updateReminder`() = runBlocking {
        // Given
        val reminder = Reminder(
            id = 1,
            title = "Updated Reminder",
            content = "Updated Content",
            createdAt = DateUtils.getCurrentDate(),
            updatedAt = DateUtils.getCurrentDate(),
            category = "测试",
            priority = 2
        )

        // When
        repository.updateReminder(reminder)

        // Then
        verify(reminderDao).updateReminder(reminder)
    }

    @Test
    fun `deleteReminder should call dao deleteReminder`() = runBlocking {
        // Given
        val reminder = Reminder(
            id = 1,
            title = "Test Reminder",
            content = "Test Content",
            createdAt = DateUtils.getCurrentDate(),
            updatedAt = DateUtils.getCurrentDate(),
            category = "测试",
            priority = 1
        )

        // When
        repository.deleteReminder(reminder)

        // Then
        verify(reminderDao).deleteReminder(reminder)
    }

    @Test
    fun `getReminderById should call dao getReminderById`() = runBlocking {
        // Given
        val reminderId = 1L
        val expectedReminder = Reminder(
            id = reminderId,
            title = "Test Reminder",
            content = "Test Content",
            createdAt = DateUtils.getCurrentDate(),
            updatedAt = DateUtils.getCurrentDate(),
            category = "测试",
            priority = 1
        )
        `when`(reminderDao.getReminderById(reminderId)).thenReturn(expectedReminder)

        // When
        val result = repository.getReminderById(reminderId)

        // Then
        verify(reminderDao).getReminderById(reminderId)
        assert(result == expectedReminder)
    }

    @Test
    fun `searchReminders should call dao searchReminders with correct query`() {
        // Given
        val query = "test"
        val expectedQuery = "%test%"

        // When
        repository.searchReminders(query)

        // Then
        verify(reminderDao).searchReminders(expectedQuery)
    }
} 