package com.ipv.farmmonitor.viewmodel

import androidx.lifecycle.*
import com.ipv.farmmonitor.adapter.NotificationsAdapter
import com.ipv.farmmonitor.adapter.PlantationsAdapter
import javax.inject.Inject


class MainViewModel @Inject constructor(): ViewModel() {
    val hasNotifications = MutableLiveData<Boolean>()
    var plantationsAdapter = MutableLiveData<PlantationsAdapter>()
    var notificationsAdapter = MutableLiveData<NotificationsAdapter>()
    var isNotificationsEnabled = MutableLiveData<Boolean>()
    var isLoginButton = MutableLiveData<Boolean>()
}