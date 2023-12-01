package com.alvintio.pedulipangan.ui.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.alvintio.pedulipangan.data.repo.UserPreferences
import com.google.firebase.auth.FirebaseAuth

class SettingsViewModel(private val pref: UserPreferences) : ViewModel() {
    private val _logoutComplete = MutableLiveData<Boolean>()
    val logoutComplete: LiveData<Boolean> = _logoutComplete

    fun userLogout() {
        FirebaseAuth.getInstance().signOut()
        _logoutComplete.value = true
    }
}
