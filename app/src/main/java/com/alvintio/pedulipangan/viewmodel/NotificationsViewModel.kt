package com.alvintio.pedulipangan.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alvintio.pedulipangan.model.Notification

class NotificationsViewModel : ViewModel() {
    private val _notifications = MutableLiveData<List<Notification>>()

    val notifications: LiveData<List<Notification>> get() = _notifications

    fun addNotification(notification: Notification) {
        _notifications.value = _notifications.value.orEmpty() + listOf(notification)
    }
}
